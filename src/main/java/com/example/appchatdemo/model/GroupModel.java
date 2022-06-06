package com.example.appchatdemo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class GroupModel implements Serializable {

    private String groupId;
    private String host;
    private String groupAvatar;
    private String groupName;
    private List<String> memberList;
    private Date lastMessageTime;

    public GroupModel() {
    }

    public GroupModel(String groupId, String host, String groupAvatar, String groupName, List<String> memberList, Date lastMessageTime) {
        this.groupId = groupId;
        this.host = host;
        this.groupAvatar = groupAvatar;
        this.groupName = groupName;
        this.memberList = memberList;
        this.lastMessageTime = lastMessageTime;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvata) {
        this.groupAvatar = groupAvata;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<String> memberList) {
        this.memberList = memberList;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
