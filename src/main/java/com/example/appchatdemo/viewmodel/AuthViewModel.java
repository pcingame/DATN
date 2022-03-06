package com.example.appchatdemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.example.appchatdemo.repositories.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel{
    private AuthenticationRepository authenticationRepository;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> userLoggedStatus;
    // private MutableLiveData<Boolean> progressbarObservable;

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getUserLoggedStatus() {
        return userLoggedStatus;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authenticationRepository = new AuthenticationRepository(application);
        userData = authenticationRepository.getFirebaseUserMutableLiveData();
        userLoggedStatus = authenticationRepository.getUserLogged();
    }


    public void register(String email, String password){
        authenticationRepository.register(email, password);
    }

    public void signIn(String email, String password){
        authenticationRepository.login(email, password);
    }

    public void forgotPassword(String email){
        authenticationRepository.forgotPassword(email);
    }

    public void signOut(){
        authenticationRepository.signOut();
    }

}
