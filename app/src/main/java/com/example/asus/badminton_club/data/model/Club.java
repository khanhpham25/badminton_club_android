package com.example.asus.badminton_club.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by khanh on 15/11/2017.
 */

public class Club implements Serializable{
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("avatar")
    @Expose
    private Avatar avatar;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("average_level")
    @Expose
    private Integer averageLevel;
    @SerializedName("number_of_members")
    @Expose
    private Integer numberOfMembers;
    @SerializedName("all_members_count")
    @Expose
    private Integer allMembersCount;
    @SerializedName("is_recruiting")
    @Expose
    private Boolean isRecruiting;
    @SerializedName("allow_friendly_match")
    @Expose
    private Boolean allowFriendlyMatch;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getAverageLevel() {
        return averageLevel;
    }

    public void setAverageLevel(Integer averageLevel) {
        this.averageLevel = averageLevel;
    }

    public Integer getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(Integer numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public Integer getAllMembersCount() {
        return allMembersCount;
    }

    public void setAllMembersCount(Integer allMembersCount) {
        this.allMembersCount = allMembersCount;
    }

    public Boolean getRecruiting() {
        return isRecruiting;
    }

    public void setRecruiting(Boolean recruiting) {
        isRecruiting = recruiting;
    }

    public Boolean getAllowFriendlyMatch() {
        return allowFriendlyMatch;
    }

    public void setAllowFriendlyMatch(Boolean allowFriendlyMatch) {
        this.allowFriendlyMatch = allowFriendlyMatch;
    }
}
