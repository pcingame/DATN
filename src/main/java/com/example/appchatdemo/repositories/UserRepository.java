package com.example.appchatdemo.repositories;

import androidx.annotation.Nullable;

import com.example.appchatdemo.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    OnUserAvailableInFireStore interfaceOfUser;
    String userId;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    List<UserModel> userModelList = new ArrayList<>();

    public UserRepository(OnUserAvailableInFireStore interfaceOfUser) {
        this.interfaceOfUser = interfaceOfUser;
    }

    public void getUserInFireStore (){
        userId = firebaseAuth.getCurrentUser().getUid();

        fireStore.collection("Users").orderBy("username", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userModelList.clear();

                for (DocumentSnapshot snapshot : value.getDocuments()){
                    UserModel userModel = snapshot.toObject(UserModel.class);

                    if(!userId.equals(userModel.getUserId())){
                        userModelList.add(userModel);
                        //adding the list of users into interface
                        interfaceOfUser.showListOfUser(userModelList);
                    }
                }
            }
        });

    }

    public interface OnUserAvailableInFireStore {
        //provide the user list
        void showListOfUser(List<UserModel> userModels);
    }
}
