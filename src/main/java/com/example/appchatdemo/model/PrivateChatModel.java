package com.example.appchatdemo.model;

import java.io.Serializable;
import java.util.Date;

public class PrivateChatModel implements Serializable {
    private String id;
    private Date lastMessageTime;
    private String user1;
    private String user2;
    private Date createAt;

    public PrivateChatModel() {
    }

    public PrivateChatModel(String id, Date lastMessageTime, String user1, String user2, Date createAt) {
        this.id = id;
        this.lastMessageTime = lastMessageTime;
        this.user1 = user1;
        this.user2 = user2;
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}
