package com.example.appchatdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchatdemo.model.GroupMessageModel;
import com.example.appchatdemo.repositories.GroupMessageRepository;

import java.util.List;

public class GroupMessageViewModel extends ViewModel implements GroupMessageRepository.OnMessageAdded {

    MutableLiveData<List<GroupMessageModel>> mutableLiveData = new MutableLiveData<>();
    GroupMessageRepository groupMessageRepository = new GroupMessageRepository(this);

    public GroupMessageViewModel() {
    }

    public void getGroupMessageFromFireStore(String groupId) {
        groupMessageRepository.getAllMessages(groupId);
    }

    public LiveData<List<GroupMessageModel>> returnMyMessages() {
        return mutableLiveData;
    }

    public void resetAll() {
        mutableLiveData.postValue(null);
    }


    @Override
    public void MessagesFromFireStore(List<GroupMessageModel> groupMessageModelList) {
        mutableLiveData.setValue(groupMessageModelList);
    }
}
