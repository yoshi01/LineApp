package com.yoshi.lineapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.yoshi.lineapp.CheckUserActivity.PREFERENCES_FILE_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyPageFragment extends Fragment  implements LoaderManager.LoaderCallbacks<JSONArray>{

    private int userId;
    private String userName;
    private String userEmail;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
        userId = settings.getInt("user_id", 0);
        getLoaderManager().restartLoader(3, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        return view;
    }

    @Override
    public Loader<JSONArray> onCreateLoader(int id, Bundle args) {
        Log.d("httpJson","JSONLoadStart");
        String urlText = "http://line-app-83253.herokuapp.com/users?id=" + userId;
        Log.d("user_id", String.valueOf(userId));
        JsonLoader jsonLoader = new JsonLoader(getActivity(), urlText);
        jsonLoader.forceLoad();
        return  jsonLoader;
    }

    @Override
    public void onLoadFinished(Loader<JSONArray> loader, JSONArray data) {
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewFriend);
        if (data != null) {
                try {
                    Log.d("httpJson","start??" + data.length());
                    JSONObject jsonObject = data.getJSONObject(0);
                    Log.d("httperror","start parse");
                    try {
                        userName = jsonObject.getString("name");
                        userEmail = jsonObject.getString("email");
                        TextView tvName = (TextView)getActivity().findViewById(R.id.textViewName);
                        tvName.setText(userName);
                        TextView tvEmail = (TextView)getActivity().findViewById(R.id.textViewEmail);
                        tvEmail.setText(userEmail);
                        Log.d("httpJson","set " + userName + ", " + userEmail);
                    }catch (JSONException e){
                        Log.d("httperror","json error!");
                    }
                    Log.d("httpJson","complete??");
                } catch (JSONException e) {
                    Log.d("onLoadFinished","JSONのパースに失敗しました。 JSONException=" + e);
                }

        }else{
            Log.d("onLoadFinished", "onLoadFinished error!");
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONArray> loader) {
        // 処理なし
    }


}
