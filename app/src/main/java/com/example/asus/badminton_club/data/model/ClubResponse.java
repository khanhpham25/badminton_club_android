package com.example.asus.badminton_club.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by khanh on 15/11/2017.
 */

public class ClubResponse{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("club")
    @Expose
    private Club club;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
