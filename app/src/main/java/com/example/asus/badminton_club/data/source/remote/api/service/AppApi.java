package com.example.asus.badminton_club.data.source.remote.api.service;

import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.model.UserResponse;

import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by le.quang.dao on 10/03/2017.
 */

public interface AppApi {
    @POST("users")
    Observable<BaseResponse<User>> register(@Query("user[name]") String userName,
                                      @Query("user[email]") String email,
                                      @Query("user[password]") String password,
                                      @Query("user[password_confirmation]") String passwordConfirm);

    @POST("users/sign_in")
    Observable<BaseResponse<User>> login (@Query("session[email]") String email,
                            @Query("session[password]") String password);

    @DELETE("users/sign_out")
    Observable<BaseResponse> logout (@Query("auth_token") String auth_token);
}
