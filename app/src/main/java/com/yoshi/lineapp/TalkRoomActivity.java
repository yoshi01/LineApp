package com.yoshi.lineapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.yoshi.lineapp.CheckUserActivity.PREFERENCES_FILE_NAME;

public class TalkRoomActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<JSONArray>{

    private int userId;
    private int toUserId;
    private String userName;
    private String toUserName;
    private String message;

    private InputMethodManager inputMethodManager;
    private EditText           editText;


    private ArrayList<String> messages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_room);

        Intent intent = getIntent();
        if(intent != null){
            toUserId = Integer.parseInt(intent.getStringExtra("toUserId"));
            toUserName = intent.getStringExtra("toUserName");
        }

        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
        userId = settings.getInt("user_id", 0);
        userName = settings.getString("user_name", " ");

        Log.d("id: ", "from " + userId +" to "+ toUserId);

        //キーボードを閉じたいEditTextオブジェクト
        editText = (EditText) findViewById(R.id.editText);
        //キーボード表示を制御するためのオブジェクト
        inputMethodManager =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        //EditTextにリスナーをセット
        editText.setOnKeyListener(new View.OnKeyListener() {

            //コールバックとしてonKey()メソッドを定義
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //イベントを取得するタイミングには、ボタンが押されてなおかつエンターキーだったときを指定
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    message = editText.getText().toString();
                    Log.d("debug", message);
                    editText.setText("");

                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    String url = "http://line-app-83253.herokuapp.com/messages";
                    //idはとりあえず
                    params.put("from", String.valueOf(userId));
                    params.put("to", String.valueOf(toUserId));
                    params.put("text", message);
                    client.addHeader("Accept", "application/json");

                    //POST
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            // 通信成功時の処理
                            System.out.println(response);

                            Intent intent = getIntent();
                            overridePendingTransition(0, 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();

                            overridePendingTransition(0, 0);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable e, String response){
                            // 通信失敗時の処理
                            System.out.println(response);

                            //失敗
                            Toast toast = Toast.makeText(TalkRoomActivity.this, "送信失敗", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });


                    //キーボードを閉じる
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    return true;
                }
                return false;
            }
        });
        getSupportLoaderManager().restartLoader(5, null, this);

    }

    @Override
    public Loader<JSONArray> onCreateLoader(int id, Bundle args) {
        Log.d("httpJson","JSONLoadStart");
        String urlText = "http://line-app-83253.herokuapp.com/messages?from=" + userId + "&to=" + toUserId;
        JsonLoader jsonLoader = new JsonLoader(this, urlText);
        jsonLoader.forceLoad();
        return  jsonLoader;
    }

    @Override
    public void onLoadFinished(Loader<JSONArray> loader, JSONArray data) {
        ListView listView = (ListView) findViewById(R.id.talkListView);
        if (data != null) {
                try {
                    Log.d("httpJson","start??" + data.length());
                    for(int i=0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Log.d("httperror","start parse");
                        setTalks(jsonObject);

                    }
                    Log.d("httpJson","complete??");
                } catch (JSONException e) {
                    Log.d("onLoadFinished","JSONのパースに失敗しました。 JSONException=" + e);
            }
            //リストをListViewにセットする
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);
            listView.setAdapter(adapter);

        }else{
            Log.d("onLoadFinished", "onLoadFinished error!");
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONArray> loader) {
        // 処理なし
    }

    public void setTalks(JSONObject jsonObject){
        String id = new String();
        String text = new String();
        try {
            id = jsonObject.getString("from");
            text = jsonObject.getString("text");
            Log.d("httpJson","set " + id + ", " + text);
        }catch (JSONException e){
            Log.d("httperror","json error!");
        }
        if(Integer.parseInt(id) == userId){
            messages.add(userName + " : " + text);
        }else{
            messages.add(toUserName + " : " + text);
        }

    }
}
