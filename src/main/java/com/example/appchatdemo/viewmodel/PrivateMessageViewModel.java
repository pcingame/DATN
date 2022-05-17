package com.example.appchatdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchatdemo.model.PrivateMessageModel;
import com.example.appchatdemo.repositories.PrivateMessageRepository;

import java.util.List;

public class PrivateMessageViewModel extends ViewModel implements PrivateMessageRepository.OnMessageAdded {
    MutableLiveData<List<PrivateMessageModel>> mutableLiveData = new MutableLiveData<>();
    PrivateMessageRepository privateMessageRepository = new PrivateMessageRepository(this);

    public PrivateMessageViewModel() {
    }

    public void getMessageFromFireStore(String friendId) {
        privateMessageRepository.getAllMessages(friendId);
    }

    public  void resetAll(){
        mutableLiveData.postValue(null);
    }

    public LiveData<List<PrivateMessageModel>> returnMyMessages(){
        return mutableLiveData;
    }

    @Override
    public void MessagesFromFireStore(List<PrivateMessageModel> messageModelList) {
        mutableLiveData.setValue(messageModelList);
    }
}
