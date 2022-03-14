package com.example.appchatdemo.repositories;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.appchatdemo.CustomProgress;
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
                    hashMap.put("imageUrl", "default");
                    hashMap.put("status", "offline");
                    hashMap.put("username", name);

                    fireStore.collection("Users").document(userId).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    //Toast.makeText(application, "Signed up", Toast.LENGTH_SHORT).show();
                    FancyToast.makeText(application,"Signed up",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                } else {
                    customProgress.hideProgress();
                    //Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    FancyToast.makeText(application,task.getException().getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
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
                   // Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    FancyToast.makeText(application,task.getException().getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                }
            }
        });
    }

    public void forgotPassword(String email) {

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    customProgress.hideProgress();
                   // Toast.makeText(application.getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                   // FancyToast.makeText(application,task.getException().getMessage(),FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                    FancyToast.makeText(application,"Vui lòng kiểm tra email của bạn",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
                } else {
                    customProgress.hideProgress();
                    //Toast.makeText(application.getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    FancyToast.makeText(application,task.getException().getMessage(),FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                }
            }
        });
    }

    public void signOut() {
        auth.signOut();
        userLogged.postValue(true);
    }
}
