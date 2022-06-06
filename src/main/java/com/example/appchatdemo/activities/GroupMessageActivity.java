package com.example.appchatdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.R;
import com.example.appchatdemo.adapter.GroupMessageAdapter;
import com.example.appchatdemo.interfaces.IClickItemFileInGroup;
import com.example.appchatdemo.databinding.ActivityGroupMessageBinding;
import com.example.appchatdemo.model.GroupMessageModel;
import com.example.appchatdemo.model.GroupModel;
import com.example.appchatdemo.viewmodel.GroupMessageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class GroupMessageActivity extends AppCompatActivity {

    private ActivityGroupMessageBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth firebaseAuth;

    private String groupId, groupName, groupAvatar, message, yourId;
    private int position, memberCount;

    private GroupMessageViewModel groupMessageViewModel;
    List<GroupMessageModel> groupMessageModelList;
    GroupMessageAdapter groupMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        yourId = firebaseAuth.getCurrentUser().getUid();

        binding.btnBackGroupMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        GroupModel groupModel = (GroupModel) bundle.get("groupModel");
        groupId = groupModel.getGroupId();
        groupName = groupModel.getGroupName();
        memberCount = groupModel.getMemberList().size();
        groupAvatar = groupModel.getGroupAvatar();

        binding.tvGroupName.setText(groupName);
        binding.tvNumberOfMember.setText(memberCount + " thành viên");
        Glide.with(this).load(groupAvatar).centerCrop().into(binding.imgAvatarGroup);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.rcvMessageGroup.setLayoutManager(linearLayoutManager);

        groupMessageModelList = new ArrayList<>();
        groupMessageAdapter = new GroupMessageAdapter(groupMessageModelList, new IClickItemFileInGroup() {
            @Override
            public void onClickItemFile(GroupMessageModel groupMessageModel) {
                onClickDownloadFile(groupMessageModel);
            }
        });
        binding.rcvMessageGroup.setAdapter(groupMessageAdapter);

        binding.btnSendMessageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = binding.edtMessage.getText().toString().trim();

                if (message.isEmpty()) {
                    binding.edtMessage.setError("Chưa nhập tin nhắn");
                } else {
                    SendMessage(groupId, message, yourId);
                    binding.edtMessage.setText(" ");
                }
            }
        });

        groupMessageViewModel = new ViewModelProvider(this).get(GroupMessageViewModel.class);
        groupMessageViewModel.getGroupMessageFromFireStore(groupId);
        groupMessageViewModel.returnMyMessages().observe(this, new Observer<List<GroupMessageModel>>() {
            @Override
            public void onChanged(List<GroupMessageModel> groupMessageModels) {
                groupMessageAdapter.setGroupMessageModelList(groupMessageModels);
                binding.rcvMessageGroup.setAdapter(groupMessageAdapter);

            }
        });
    }

    private void onClickDownloadFile(GroupMessageModel groupMessageModel) {
        Toast.makeText(getApplicationContext(), "Download file Out of Order", Toast.LENGTH_SHORT).show();
    }

    private void SendMessage(String groupId, String message, String yourId) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", yourId);
        hashMap.put("receiver", groupId);
        hashMap.put("message", message);
        hashMap.put("time", currentTime);
        hashMap.put("file", "default");
        hashMap.put("date", date);
        hashMap.put("fileName", "");
        hashMap.put("fileType", "");

        fireStore.collection("GroupMessages").document(currentTime).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DocumentReference documentReference = fireStore.collection("Groups").document(groupId);
                    documentReference.update("lastMessageGroupTime", date);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.to_left, R.anim.to_right);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}