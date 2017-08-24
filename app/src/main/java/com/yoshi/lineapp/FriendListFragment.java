package com.yoshi.lineapp;


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
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.yoshi.lineapp.CheckUserActivity.PREFERENCES_FILE_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends Fragment implements LoaderManager.LoaderCallbacks<JSONArray>{

    private ArrayList<ListItem> friends = new ArrayList<>();
    private int userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        SharedPreferences settings = getActivity().getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
        userId = settings.getInt("user_id", 0);
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
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
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewFriend);
        if (data != null) {
            if(friends.isEmpty()){
                try {
                    Log.d("httpJson","start??" + data.length());
                    for(int i=0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Log.d("httperror","start parse");
                        setFriends(jsonObject);
                    }
                    Log.d("httpJson","complete??");
                } catch (JSONException e) {
                    Log.d("onLoadFinished","JSONのパースに失敗しました。 JSONException=" + e);
                }
            }
            //リストをListViewにセットする
            ListAdapter adapter = new ListAdapter(getActivity(), R.layout.list_item, friends);
            listView.setAdapter(adapter);
        }else{
            Log.d("onLoadFinished", "onLoadFinished error!");
        }
    }

    @Override
    public void onLoaderReset(Loader<JSONArray> loader) {
        // 処理なし
    }

    public void setFriends(JSONObject jsonObject){
        String name = new String();;
        try {
            name = jsonObject.getString("name");
            Log.d("httpJson","set " + name);
        }catch (JSONException e){
            Log.d("httperror","json error!");
        }
        //サンプル画像
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.friend_sample);
        ListItem item = new ListItem(bmp, name);
        friends.add(item);
    }

}
