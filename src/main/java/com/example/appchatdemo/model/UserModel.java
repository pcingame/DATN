package com.example.appchatdemo.model;

public class UserModel {

    private String userId;
    private String imageUrl;
    private String username;
    private String status;

    public UserModel(String userId, String imageUrl, String username, String status) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.username = username;
        this.status = status;
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
}
