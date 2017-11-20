package com.example.asus.badminton_club.data.source.remote.api.service;

import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.model.UserResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
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

    @Multipart
    @PATCH("users/{id}")
    Observable<BaseResponse<User>> uploadUserAvatar(@Path("id") int id,
                                            @Part MultipartBody.Part file,
                                            @Header("Authorization") String auth_token);

    @PATCH("users/{id}")
    Observable<BaseResponse<User>> updateUserInfo(@Path("id") int id,
                                                  @Query("user[name]") String name,
                                                  @Query("user[mobile]") String mobile,
                                                  @Query("user[main_rackquet]") String main_racket,
                                                  @Query("user[gender]") Integer gender,
                                                  @Query("user[badminton_level]") Integer skill,
                                                  @Header("Authorization") String auth_token);

    @POST("users/sign_in")
    Observable<BaseResponse<User>> login (@Query("session[email]") String email,
                            @Query("session[password]") String password);

    @DELETE("users/sign_out")
    Observable<BaseResponse> logout (@Query("auth_token") String auth_token);

    @POST("auth/omniauths")
    Observable<BaseResponse<User>> omniauth_login (@Query("session[email]") String email,
                                                   @Query("session[provider]") String name,
                                                   @Query("session[provider]") String provider,
                                                   @Query("session[auth_token]") String auth_token);

    @POST("clubs")
    Observable<BaseResponse<Club>> createClub(@Query("club[name]") String clubName,
                                            @Query("club[location]") String location,
                                            @Query("club[is_recruiting]") Boolean isRecruiting,
                                            @Query("club[allow_friendly_match]") Boolean allowMatch,
                                            @Query("club[user_clubs_attributes][0][user_id]") Integer userId,
                                            @Query("club[user_clubs_attributes][0][is_owner]") Boolean isOwner);

    @GET("clubs")
    Observable<BaseResponse<ArrayList<Club>>> getAllClubs();

    @Multipart
    @PATCH("clubs/{id}")
    Observable<BaseResponse<Club>> uploadClubAvatar(@Path("id") int id,
                                                @Part MultipartBody.Part file,
                                                @Header("Authorization") String auth_token);

    @PATCH("clubs/{id}")
    Observable<BaseResponse<Club>> updateClubInfo(@Path("id") int id,
                                              @Query("club[name]") String clubName,
                                              @Query("club[location]") String location,
                                              @Query("club[description]") String description,
                                              @Query("club[average_level]") Integer averageLevel,
                                              @Query("club[is_recruiting]") Boolean isRecruiting,
                                              @Query("club[allow_friendly_match]") Boolean allowMatch,
                                              @Header("Authorization") String auth_token);

    @POST("password_resets")
    Observable<BaseResponse<User>> submitEmail (@Query("password_reset[email]") String email);

    @PATCH("password_resets")
    Observable<BaseResponse<User>> resetPassword(@Query("email") String email,
                                                    @Query("user[reset_digest]") String resetToken,
                                                    @Query("user[password]") String password,
                                                    @Query("user[password_confirmation]") String cPassword);
}
