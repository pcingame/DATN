package com.example.appchatdemo.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.appchatdemo.adapter.PrivateMessageAdapter;
import com.example.appchatdemo.model.PrivateMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrivateMessageRepository {

    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    String userId;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    List<PrivateMessageModel> privateMessageModelsList = new ArrayList<>();
    OnMessageAdded interfaceForMessages;
    PrivateMessageAdapter adapter;

    public PrivateMessageRepository(OnMessageAdded interfaceForMessages) {
        this.interfaceForMessages = interfaceForMessages;
    }

    public void getAllMessages(String friend) {
        userId = auth.getCurrentUser().getUid();
        fireStore.collection("PrivateMessages").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("AAA", "Listen failed.", error);
                    return;
                }

                privateMessageModelsList.clear();
                for (DocumentSnapshot ds : value.getDocuments()) {
                    PrivateMessageModel privateMessageModel = ds.toObject(PrivateMessageModel.class);
                    //only display conversation between two user
                    //since every user will have different conversation
                    assert privateMessageModel != null;
                    if (privateMessageModel.getSender().equals(userId) && privateMessageModel.getReceiver().equals(friend)
                            || privateMessageModel.getReceiver().equals(userId)
                            && privateMessageModel.getSender().equals(friend)) {
                        privateMessageModelsList.add(privateMessageModel);
                        interfaceForMessages.MessagesFromFireStore(privateMessageModelsList);
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }


                }
            }
        });

    }

    public interface OnMessageAdded {
        void MessagesFromFireStore(List<PrivateMessageModel> messageModelList);
    }
}
