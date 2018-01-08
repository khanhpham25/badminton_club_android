package com.example.asus.badminton_club.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by khanh on 07/11/2017.
 */

public class User implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gender")
    @Expose
    private Integer gender;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("badminton_level")
    @Expose
    private Integer badmintonLevel;
    @SerializedName("avatar")
    @Expose
    private Avatar avatar;
    @SerializedName("main_rackquet")
    @Expose
    private String mainRackquet;
    @SerializedName("requested_club_ids")
    @Expose
    private List<Integer> requestedClubIds;
    @SerializedName("owned_club_ids")
    @Expose
    private List<Integer> ownedClubIds;
    @SerializedName("member_club_ids")
    @Expose
    private List<Integer> memberClubIds;
    @SerializedName("provider")
    @Expose
    private String provider;
    @SerializedName("is_admin")
    @Expose
    private Object isAdmin;
    @SerializedName("is_owner")
    @Expose
    private Boolean isOwner;
    @SerializedName("deleted_at")
    @Expose
    private Object deletedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("auth_token")
    @Expose
    private String authToken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() { return gender; }

    public void setGender(Integer gender) { this.gender = gender; }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getBadmintonLevel() { return badmintonLevel; }

    public void setBadmintonLevel(Integer badmintonLevel) {
        this.badmintonLevel = badmintonLevel;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getMainRackquet() {
        return mainRackquet;
    }

    public void setMainRackquet(String mainRackquet) {
        this.mainRackquet = mainRackquet;
    }

    public List<Integer> getRequestedClubIds() { return requestedClubIds; }

    public void setRequestedClubIds(List<Integer> requestedClubIds) { this.requestedClubIds = requestedClubIds; }

    public List<Integer> getOwnedClubIds() { return ownedClubIds; }

    public void setOwnedClubIds(List<Integer> ownedClubIds) { this.ownedClubIds = ownedClubIds; }

    public List<Integer> getMemberClubIds() { return memberClubIds; }

    public void setMemberClubIds(List<Integer> memberClubIds) { this.memberClubIds = memberClubIds; }

    public String getProvider() { return provider; }

    public void setProvider(String provider) { this.provider = provider; }

    public Boolean getOwner() {return isOwner;}

    public void setOwner(Boolean owner) {isOwner = owner;}

    public Object getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Object isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}