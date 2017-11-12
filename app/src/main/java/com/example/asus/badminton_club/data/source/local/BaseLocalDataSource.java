package com.example.asus.badminton_club.data.source.local;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by khanh on 09/11/2017.
 */

public abstract class BaseLocalDataSource {
    private static final String SHARE_PREF_NAME = "SHARE_PREF_NAME";

    protected SharedPreferences mSharePreferences;
    protected SharedPreferences.Editor mEditor;

    public BaseLocalDataSource(Context context) {
        mSharePreferences = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharePreferences.edit();
    }
}
