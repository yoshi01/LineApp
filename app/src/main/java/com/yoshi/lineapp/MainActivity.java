package com.yoshi.lineapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;

import static com.yoshi.lineapp.CheckUserActivity.PREFERENCES_FILE_NAME;


public class MainActivity extends AppCompatActivity implements FragmentTabHost.OnTabChangeListener {


    private Menu mainMenu;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FragmentTabHost を取得する
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);

        TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3;

        // TabSpec を生成する
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("友達");
        // TabHost に追加
        tabHost.addTab(tabSpec1, FriendListFragment.class, null);

        // TabSpec を生成する
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("トーク");
        // TabHost に追加
        tabHost.addTab(tabSpec2, TalkListFragment.class, null);

        // TabSpec を生成する
        tabSpec3 = tabHost.newTabSpec("tab3");
        tabSpec3.setIndicator("マイページ");
        // TabHost に追加
        tabHost.addTab(tabSpec3, MyPageFragment.class, null);

        // リスナー登録
        tabHost.setOnTabChangedListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yoshi.lineapp.LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        }, intentFilter);

    }

    @Override
    public void onTabChanged(String tabId) {
        Log.d("onTabChanged", "tabId: " + tabId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        mainMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                // カスタムビューを設定
                LayoutInflater inflater = (LayoutInflater)this.getSystemService(
                        LAYOUT_INFLATER_SERVICE);
                final View dialogLayout = inflater.inflate(R.layout.add_friend_dialog,
                        (ViewGroup)findViewById(R.id.layout_root));

                // ダイヤログ表示
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("友達追加");
                builder.setView(dialogLayout);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                        // ID を取得
                        EditText id = (EditText)dialogLayout.findViewById(R.id.customDlg_id);
                        String strId   = id.getText().toString();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                    }
                });
                builder.create().show();
                return true;

            case R.id.menu_logout:
                logout();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.yoshi.lineapp.LOGOUT");
                sendBroadcast(broadcastIntent);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final int action = event.getAction();
        final int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_UP) {
            // メニュー表示
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (mainMenu != null) {
                    mainMenu.performIdentifierAction(R.id.overflow_options, 0);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    // ログアウト処理
    public void logout(){
        SharedPreferences settings = getSharedPreferences(PREFERENCES_FILE_NAME, 0); // 0 -> MODE_PRIVATE
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("logged-in", 0);
        editor.commit();
    }

}
