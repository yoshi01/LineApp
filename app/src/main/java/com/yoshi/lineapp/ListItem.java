package com.yoshi.lineapp;

import android.graphics.Bitmap;


public class ListItem {
    private Bitmap mListImage = null;
    private String mListName = null;

    /**
     * 空のコンストラクタ
     */
    public ListItem() {};

    /**
     * コンストラクタ
     */
    public ListItem(Bitmap image, String name) {
        mListImage = image;
        mListName = name;
    }

    /**
     * 画像を設定
     */
    public void setFriendImage(Bitmap image) {
        mListImage = image;
    }

    /**
     * 名前を設定
     */
    public void setFriendName(String name) {
        mListName = name;
    }

    /**
     * 画像を取得
     */
    public Bitmap getFriendImage() {
        return mListImage;
    }

    /**
     * 名前を取得
     */
    public String getFriendName() {
        return mListName;
    }
}
