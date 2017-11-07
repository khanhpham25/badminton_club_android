package com.example.asus.badminton_club.data.source.remote.api.service;

import android.app.Application;
import android.support.annotation.NonNull;

import com.example.asus.badminton_club.utils.Constant;
import com.example.asus.badminton_club.data.source.remote.api.middleware.RetrofitInterceptor;

/**
 * Created by le.quang.dao on 10/03/2017.
 */

public class AppServiceClient extends ServiceClient {

    private static AppApi sInstance;

    public static void initialize(@NonNull Application application) {
        RetrofitInterceptor interceptor = null;
        interceptor = new RetrofitInterceptor();
        sInstance = createService(application, Constant.END_POINT_URL, AppApi.class, interceptor);
    }

    public static AppApi getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("Need call method AppServiceClient#initialize() first");
        }
        return sInstance;
    }
}
