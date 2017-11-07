package com.example.asus.badminton_club.data.source.remote.api.service;

import com.example.asus.badminton_club.data.model.UserResponse;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by le.quang.dao on 10/03/2017.
 */

public interface AppApi {
    @POST("users")
    Observable<UserResponse> register(@Query("user[name]") String userName,
                                     @Query("user[email]") String email,
                                     @Query("user[password]") String password,
                                     @Query("user[password_confirmation]") String passwordConfirm);
}
