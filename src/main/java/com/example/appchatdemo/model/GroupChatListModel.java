package com.example.appchatdemo.model;

import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.model.UserModel;

import java.io.Serializable;
import java.util.List;

public class GroupChatListModel implements Serializable {
    private int type;
    private List<UserModel> userModelList;
    private List<GroupModel> groupModelList;

    public GroupChatListModel(int type, List<UserModel> userModelList, List<GroupModel> groupModelList) {
        this.type = type;
        this.userModelList = userModelList;
        this.groupModelList = groupModelList;
    }

    public GroupChatListModel() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    public List<GroupModel> getGroupModelList() {
        return groupModelList;
    }

    public void setGroupModelList(List<GroupModel> groupModelList) {
        this.groupModelList = groupModelList;
    }
}
