package com.example.appchatdemo.fragment.bottomnavigationmain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.adapter.ListChatAdapter;
import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.model.ListChatModel;
import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.repositories.GroupRepository;
import com.example.appchatdemo.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ProjectFragment extends Fragment {

    private RecyclerView rcvListChat;
    private ListChatAdapter listChatAdapter;
    String userId;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    GroupRepository.OnGroupAvailableInFireStore interfaceOfGroup;
    UserRepository.OnUserAvailableInFireStore interfaceOfUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvListChat = view.findViewById(R.id.rcvListChatReal);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvListChat.setLayoutManager(linearLayoutManager);
        getListChat(new OnFinalLoading() {
            @Override
            public void onSuccess(List<ListChatModel> listChatModels) {
                listChatAdapter = new ListChatAdapter(listChatModels, getContext());
                rcvListChat.setAdapter(listChatAdapter);
            }
        });
    }

    private void getListChat(OnFinalLoading onFinalLoading) {

        List<UserModel> userList = new ArrayList<>();
        List<GroupModel> groupList = new ArrayList<>();
        List<ListChatModel> listChatModels = new ArrayList<>();

        userId = firebaseAuth.getCurrentUser().getUid();

        fireStore.collection("Users").orderBy("username", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                userList.clear();
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    UserModel userModel = snapshot.toObject(UserModel.class);

                    if (!userId.equals(userModel.getUserId())) {
                        userList.add(userModel);
                    }
                }

                if (onFinalLoading != null) {
                    onFinalLoading.onSuccess(listChatModels);
                }
                listChatModels.add(new ListChatModel(ListChatAdapter.TYPE_USER_MODEL, userList, null));
            }
        });

        fireStore.collection("Group").orderBy("groupName", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                groupList.clear();

                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    if (snapshot != null) {
                        GroupModel groupModel = snapshot.toObject(GroupModel.class);
                        groupList.add(groupModel);
                    }
                }

                if (onFinalLoading != null) {
                    onFinalLoading.onSuccess(listChatModels);
                }
                listChatModels.add(new ListChatModel(ListChatAdapter.TYPE_GROUP_MODEL, null, groupList));
            }
        });

    }
    interface OnFinalLoading {
        void onSuccess(List<ListChatModel> listChatModels);
    }
}