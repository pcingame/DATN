package com.example.appchatdemo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupModel implements Serializable {
    private String host, groupAvata, groupName, groupId;
    private ArrayList<MemberModel> groupMember;

    public GroupModel() {
    }

    public GroupModel(String host, String groupAvata, String groupName, String groupId, ArrayList<MemberModel> groupMember) {
        this.host = host;
        this.groupAvata = groupAvata;
        this.groupName = groupName;
        this.groupId = groupId;
        this.groupMember = groupMember;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getGroupAvata() {
        return groupAvata;
    }

    public void setGroupAvata(String groupAvata) {
        this.groupAvata = groupAvata;
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

    public ArrayList<MemberModel> getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(ArrayList<MemberModel> groupMember) {
        this.groupMember = groupMember;
    }
}
