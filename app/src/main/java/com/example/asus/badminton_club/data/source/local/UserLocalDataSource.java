package com.example.asus.badminton_club.data.source.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.asus.badminton_club.data.model.User;
import com.google.gson.Gson;


/**
 * Created by khanh on 09/11/2017.
 */

public class UserLocalDataSource extends BaseLocalDataSource{

    private static final String PREF_USER = "PREF_USER";

    public UserLocalDataSource(Context context) {
        super(context);
    }

    public boolean saveUser(User user) {
        String userStringStr = new Gson().toJson(user);
        mEditor.putString(PREF_USER, userStringStr);
        return mEditor.commit();
    }

    public User getCurrentUser() {
        String userStr = mSharePreferences.getString(PREF_USER, null);
        return userStr != null ? new Gson().fromJson(userStr, User.class) : null;
    }

    public void clearUser() {
        mEditor.clear();
        mEditor.commit();
    }
}
