package com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.PrivateMessageActivity;
import com.example.appchatdemo.adapter.PrivateChatListAdapter;
import com.example.appchatdemo.interfaces.IClickItemUserListener;
import com.example.appchatdemo.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListChatFragment extends Fragment {

    private RecyclerView recyclerPrivateChatList;
    private PrivateChatListAdapter privateChatListAdapter;
    private List<UserModel> mUsers;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String userId;
    private ArrayList<String> listChat;
    FirebaseUser fuser;
    private Map<String, String> map = new HashMap<>();
    private List<String> listTime ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerPrivateChatList = view.findViewById(R.id.recyclerPrivateChatList);
        recyclerPrivateChatList.setHasFixedSize(true);
        recyclerPrivateChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        userId = firebaseAuth.getCurrentUser().getUid();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        getListChat();
    }

    private void getListChat() {
        mUsers = new ArrayList<>();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mUsers = new ArrayList<>();
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        listChat = (ArrayList<String>) documentSnapshot.get("listChatPrivate");
                        if (listChat.size() == 0) {
                            FancyToast.makeText(getContext(), "Chưa có tin nhắn nào", Toast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        } else {
                            fetchingList(listChat);
                        }
                    }
                }
            }
        });

    }

    private void fetchingList(ArrayList<String> listChat) {
        mUsers = new ArrayList<>();
        listTime = new ArrayList<>();
        firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mUsers.clear();
                assert value != null;
                for (DocumentSnapshot ds : value.getDocuments()) {
                    UserModel userModel = ds.toObject(UserModel.class);
                    for (int i = 0; i < listChat.size(); i++) {
                        if (userModel.getUserId().equals(listChat.get(i))) {
                            mUsers.add(userModel);
                        }
                    }
                }
                privateChatListAdapter = new PrivateChatListAdapter(getContext(), mUsers, new IClickItemUserListener() {
                    @Override
                    public void onClickItemUser(UserModel userModel) {
                        onClickGoToPrivateMessage(userModel);
                    }
                });
                recyclerPrivateChatList.setAdapter(privateChatListAdapter);
            }
        });
    }

    private void onClickGoToPrivateMessage(UserModel userModel) {
        Intent intent = new Intent(getContext(), PrivateMessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userModel", userModel);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
