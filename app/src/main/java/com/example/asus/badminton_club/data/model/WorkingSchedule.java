package com.example.asus.badminton_club.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by khanh on 07/01/2018.
 */

public class WorkingSchedule {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("working_date")
    @Expose
    private String workingDate;
    @SerializedName("club_id")
    @Expose
    private Integer clubId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(String workingDate) {
        this.workingDate = workingDate;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }
}
