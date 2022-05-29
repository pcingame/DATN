package com.example.appchatdemo.model;

import java.io.Serializable;
import java.util.List;

public class UserModel implements Serializable {

    private String userId;
    private String imageUrl;
    private String username;
    private String email;
    private String status;
    private String activeStatus;
    private List<String> listChatPrivate;
    private List<String> listChatGroup;


    public UserModel() {
    }

    public UserModel(String userId, String imageUrl, String username, String email, String status, String activeStatus, List<String> listChatPrivate, List<String> listChatGroup) {
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.username = username;
        this.email = email;
        this.status = status;
        this.activeStatus = activeStatus;
        this.listChatPrivate = listChatPrivate;
        this.listChatGroup = listChatGroup;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getListChatPrivate() {
        return listChatPrivate;
    }

    public void setListChatPrivate(List<String> listChatPrivate) {
        this.listChatPrivate = listChatPrivate;
    }

    public List<String> getListChatGroup() {
        return listChatGroup;
    }

    public void setListChatGroup(List<String> listChatGroup) {
        this.listChatGroup = listChatGroup;
    }
}
