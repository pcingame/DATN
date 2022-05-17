package com.example.appchatdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.repositories.GroupRepository;

import java.util.List;

public class GroupViewModel extends ViewModel implements GroupRepository.OnGroupAvailableInFireStore {

    MutableLiveData<List<GroupModel>> mutableLiveData = new MutableLiveData<>();

    GroupRepository groupRepository = new GroupRepository(this);

    public GroupViewModel() {
        groupRepository.getGroupUserJoinInFireStore();
    }

    public LiveData<List<GroupModel>> getAllGroupJoin() {
        return mutableLiveData;
    }
    @Override
    public void showListOfGroup(List<GroupModel> groupModelList) {
        mutableLiveData.setValue(groupModelList);
    }
}
