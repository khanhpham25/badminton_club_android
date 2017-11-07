package com.example.asus.badminton_club;

import android.app.Application;

import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;

/**
 * Created by khanh on 07/11/2017.
 */

public class PleaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppServiceClient.initialize(this);
    }
}
