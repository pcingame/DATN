package com.example.appchatdemo.repositories;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.appchatdemo.model.GroupMessageModel;
import com.example.appchatdemo.adapter.GroupMessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupMessageRepository {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    List<GroupMessageModel> groupMessageModelList = new ArrayList<>();
    OnMessageAdded interfaceForGroupMessages;
    GroupMessageAdapter adapter;
    String yourId;

    public GroupMessageRepository(OnMessageAdded interfaceForGroupMessages) {
        this.interfaceForGroupMessages = interfaceForGroupMessages;
    }

    public void getAllMessages(String groupId) {
        firestore.collection("GroupMessages").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                groupMessageModelList.clear();
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    GroupMessageModel groupMessageModel = snapshot.toObject(GroupMessageModel.class);
                    if (groupMessageModel == null){
                        return;
                    }else {
                        if (groupMessageModel.getReceiver().equals(groupId)) {
                            groupMessageModelList.add(groupMessageModel);
                            interfaceForGroupMessages.MessagesFromFireStore(groupMessageModelList);
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            }
        });
    }

    public interface OnMessageAdded {
        void MessagesFromFireStore(List<GroupMessageModel> groupMessageModelList);
    }
}
