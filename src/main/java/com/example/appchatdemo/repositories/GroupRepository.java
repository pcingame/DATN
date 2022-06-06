package com.example.appchatdemo.repositories;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appchatdemo.model.GroupModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("Groups").orderBy("lastMessageGroupTime", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                boolean checkMember = false;
                groupModelList.clear();
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    GroupModel groupModel = snapshot.toObject(GroupModel.class);
                    for (int i = 0; i < groupModel.getMemberList().size(); i++) {
                        if (groupModel.getMemberList().get(i).equals(userId)) {
                            groupModelList.add(groupModel);
                            interfaceOfGroup.showListOfGroup(groupModelList);
                        }
                    }
                }
            }
        });

    }

    public interface OnGroupAvailableInFireStore {
        void showListOfGroup(List<GroupModel> groupModels);
    }
}
