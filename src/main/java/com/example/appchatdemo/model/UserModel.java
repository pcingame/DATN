package com.example.appchatdemo.model;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String userId;
    private String imageUrl;
    private String username;
    private String status;
    private String activeStatus;

    public UserModel() {
    }

    public UserModel(String userId, String imageUrl, String username, String status, String activeStatus) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.username = username;
        this.status = status;
        this.activeStatus = activeStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }
}
