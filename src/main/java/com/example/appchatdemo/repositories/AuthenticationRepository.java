package com.example.appchatdemo.repositories;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appchatdemo.CustomProgress;
import com.example.appchatdemo.R;
import com.example.appchatdemo.fragment.Login.ForgotPasswordFragment;
import com.example.appchatdemo.fragment.Login.SignInFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;

public class AuthenticationRepository {

    private final Application application;
    private final MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private final MutableLiveData<Boolean> userLogged;
    private final FirebaseAuth auth;
    private final FirebaseFirestore fireStore;
    private String userId;
    private String imgLinkDefaultAvatar = "https://firebasestorage.googleapis.com/v0/b/appmiochat.appspot.com/o/logo.png?alt=media&token=7e409ee9-6038-45b9-b365-206072a56490";
    CustomProgress customProgress = CustomProgress.getInstance();


    public AuthenticationRepository(Application application) {
        this.application = application;
        firebaseUserMutableLiveData = new MutableLiveData<>();
        userLogged = new MutableLiveData<>();
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
        }

    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {

        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLogged() {
        return userLogged;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void register(String name, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    customProgress.hideProgress();
                    firebaseUserMutableLiveData.postValue(auth.getCurrentUser());

                    userId = auth.getCurrentUser().getUid();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("userId", userId);
                    hashMap.put("imageUrl", imgLinkDefaultAvatar);
                    hashMap.put("status", "offline");
                    hashMap.put("activeStatus", "offline");
                    hashMap.put("username", name);

                    fireStore.collection("Users").document(userId).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    FancyToast.makeText(application, application.getString(R.string.sign_up_success), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                } else {
                    customProgress.hideProgress();
                    String a = task.getException().getMessage();
                    if (a.equals(application.getString(R.string.used_email_el))) {
                        a = application.getString(R.string.used_email_vn);
                    } else if (a.equals(application.getString(R.string.format_email_el))) {
                        a = application.getString(R.string.invalid_information);
                    } else if (a.equals(application.getString(R.string.block_request_el))) {
                        a = application.getString(R.string.block_request_vn);
                    }
                    FancyToast.makeText(application, a, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();

                }
            }
        });
    }

    public void login(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    customProgress.hideProgress();
                    firebaseUserMutableLiveData.postValue(auth.getCurrentUser());
                } else {
                    customProgress.hideProgress();
                    String b = task.getException().getMessage();

                    if (b.equals(application.getString(R.string.invalid_password_el))) {
                        b = application.getString(R.string.exist_account_login_vn);
                    } else if (b.equals(application.getString(R.string.exist_account_el))) {
                        b = application.getString(R.string.exist_account_login_vn);
                    } else if (b.equals(application.getString(R.string.block_request_el))) {
                        b = application.getString(R.string.block_request_vn);
                    }else if(b.equals(application.getString(R.string.format_email_el))){
                        b = application.getString(R.string.invalid_information);
                    }

                    FancyToast.makeText(application, b, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });
    }


    public void signOut() {
        auth.signOut();
        userLogged.postValue(true);
    }


}
