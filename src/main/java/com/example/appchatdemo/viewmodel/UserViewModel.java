package com.example.appchatdemo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.repositories.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel implements UserRepository.OnUserAvailableInFireStore {

    MutableLiveData<List<UserModel>> mutableLiveData = new MutableLiveData<>();

    UserRepository userRepository = new UserRepository(this);

    public UserViewModel() {
        userRepository.getUserInFireStore();
    }

    public LiveData<List<UserModel>> getAllUsers(){
        return mutableLiveData;
    }

    @Override
    public void showListOfUser(List<UserModel> userModelList) {
        mutableLiveData.setValue(userModelList);
    }
}
