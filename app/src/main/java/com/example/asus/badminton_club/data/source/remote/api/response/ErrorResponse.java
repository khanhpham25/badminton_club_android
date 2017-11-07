package com.example.asus.badminton_club.data.source.remote.api.response;

import com.google.gson.annotations.Expose;

/**
 * Created by Sun on 4/16/2017.
 */

public class ErrorResponse extends BaseRespone {
    @Expose
    private int status;
    @Expose
    private String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
