package com.example.appchatdemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.appchatdemo.repositories.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel{

    public AuthenticationRepository authenticationRepository;
    private final MutableLiveData<FirebaseUser> userData;
    private final MutableLiveData<Boolean> userLoggedStatus;
    private final FirebaseUser currentUser;


    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getUserLoggedStatus() {
        return userLoggedStatus;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authenticationRepository = new AuthenticationRepository(application);
        userData = authenticationRepository.getFirebaseUserMutableLiveData();
        userLoggedStatus = authenticationRepository.getUserLogged();
        currentUser = authenticationRepository.getCurrentUser();
    }


    public void register(String name, String email, String password){
        authenticationRepository.register(name, email, password);
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
