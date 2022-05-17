package com.example.appchatdemo.repositories;


import androidx.annotation.NonNull;

import com.example.appchatdemo.model.GroupModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupRepository {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    OnGroupAvailableInFireStore interfaceOfGroup;
    List<GroupModel> groupModelList = new ArrayList<>();

    public GroupRepository(OnGroupAvailableInFireStore interfaceOfGroup) {
        this.interfaceOfGroup = interfaceOfGroup;
    }

    public void getGroupUserJoinInFireStore() {
        firestore.collection("Group")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            groupModelList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                GroupModel groupModel = documentSnapshot.toObject(GroupModel.class);
                                groupModelList.add(groupModel);
                                interfaceOfGroup.showListOfGroup(groupModelList);

                            }
                        }
                    }
                });
    }

    public interface OnGroupAvailableInFireStore {
        void showListOfGroup(List<GroupModel> groupModels);
    }
}
