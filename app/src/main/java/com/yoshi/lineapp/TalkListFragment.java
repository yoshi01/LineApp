package com.yoshi.lineapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.yoshi.lineapp.CheckUserActivity.PREFERENCES_FILE_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class TalkListFragment extends Fragment  implements LoaderManager.LoaderCallbacks<JSONArray>{

    private ArrayList<ListItem> talks = new ArrayList<>();
    private int userId;
    private HashMap<String, String> hashMapId = new HashMap<String, String>();
    private HashMap<String, String> hashMapName = new HashMap<String, String>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
        userId = settings.getInt("user_id", 0);
        getLoaderManager().restartLoader(4, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk_list, container, false);

        return view;
    }


    @Override
    public Loader<JSONArray> onCreateLoader(int id, Bundle args) {
        Log.d("httpJson","JSONLoadStart");
        String urlText = "http://line-app-83253.herokuapp.com/friends?id=" + userId;
        JsonLoader jsonLoader = new JsonLoader(getActivity(), urlText);
        jsonLoader.forceLoad();
        return  jsonLoader;
    }

    @Override
    public void onLoadFinished(Loader<JSONArray> loader, JSONArray data) {
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewTalk);
        if (data != null) {
            if(talks.isEmpty()){
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
            }
            //リストをListViewにセットする
            ListAdapter adapter = new ListAdapter(getActivity(), R.layout.list_item, talks);
            listView.setAdapter(adapter);
            // リストクリック時のイベントハンドラ登録
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TalkRoomActivity に遷移

                    Intent intent = new Intent(getActivity().getApplicationContext(), TalkRoomActivity.class);
                    intent.putExtra("toUserId", hashMapId.get(Integer.toString(position)));
                    intent.putExtra("toUserName", hashMapName.get(Integer.toString(position)));
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }
            });
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
        String name = new String();
        try {
            id = jsonObject.getString("id");
            name = jsonObject.getString("name");
            Log.d("httpJson","set " + id + ", " + name);
        }catch (JSONException e){
            Log.d("httperror","json error!");
        }
        //サンプル画像
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.friend_sample);
        ListItem item = new ListItem(bmp, name);
        talks.add(item);
        hashMapId.put(String.valueOf(talks.size()-1), id);
        hashMapName.put(String.valueOf(talks.size()-1), name);
    }
}
