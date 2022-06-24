package com.example.appchatdemo.activities;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appchatdemo.R;
import com.example.appchatdemo.utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateGroupChatActivity extends AppCompatActivity {

    FirebaseFirestore fireStore;
    private String userIdd;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);
    }

    public void setIsOnline(String isOnline) {
        FirebaseUser userOfFirebase = FirebaseAuth.getInstance().getCurrentUser();
        fireStore =  FirebaseFirestore.getInstance();
        if (userOfFirebase != null) {
            userIdd = userOfFirebase.getUid();
        }

        fireStore.collection("Users").document(userIdd).update("activeStatus", isOnline).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setIsOnline("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        setIsOnline("offline");
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}