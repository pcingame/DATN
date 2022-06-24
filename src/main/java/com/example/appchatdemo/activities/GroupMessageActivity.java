package com.example.appchatdemo.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.utility.CustomProgress;
import com.example.appchatdemo.utility.NetworkChangeListener;
import com.example.appchatdemo.viewmodel.GroupMessageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private String userIdd;
    private GroupMessageViewModel groupMessageViewModel;
    List<GroupMessageModel> groupMessageModelList;
    GroupMessageAdapter groupMessageAdapter;
    CustomProgress customProgress = CustomProgress.getInstance();
    private String displayName;
    private final int IMAGE = 11, PDF = 22, DOCX = 33, FILE = 44;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private Double capacity =  20971520.0; //5242880.0;

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

        binding.btnSendFileGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePickDialog();
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

    private void showFilePickDialog() {
        CharSequence options[] = new CharSequence[]{
                "Ảnh",
                "PDF",
                "Docx",
                "Exel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageActivity.this);
        builder.setTitle("Chọn loại file bạn muốn gửi");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), IMAGE);
                }

                if (which == 1) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(intent, "Chọn file pdf"), PDF);
                }

                if (which == 2) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    startActivityForResult(Intent.createChooser(intent, "Chọn file docx"), DOCX);
                }

                if (which == 3) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    startActivityForResult(Intent.createChooser(intent, "Chọn file exel"), FILE);
                }
            }
        });
        builder.create().show();
    }

    private void onClickDownloadFile(GroupMessageModel groupMessageModel) {
        String urlFile = groupMessageModel.getFile();
        String fileName = groupMessageModel.getFileName();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlFile));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle("Tải file");
        request.setDescription("Đang tải" + fileName);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()));

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }

    private void SendMessage(String groupId, String message, String yourId) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);

        fireStore.collection("Users").document(yourId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username = document.get("username").toString();
                        String avatar = document.get("imageUrl").toString();
                        send(yourId, groupId, message, currentTime, date, username, avatar);
                    }
                }
            }
        });
    }

    private void send(String yourId, String groupId, String message, String currentTime, Date date, String username, String avatar) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", yourId);
        hashMap.put("receiver", groupId);
        hashMap.put("message", message);
        hashMap.put("time", currentTime);
        hashMap.put("file", "default");
        hashMap.put("date", date);
        hashMap.put("fileName", "");
        hashMap.put("fileType", "");
        hashMap.put("memberName", username);
        hashMap.put("avatarLink", avatar);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            int rs = displayMediaFileMetaData(uri);
            if (rs > capacity) {
                Toast.makeText(GroupMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
                return;
            } else {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String path = "Image File/" + "images_" + timestamp;

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(path);
                customProgress.showProgress(this, "Đang tải", false);

                storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            customProgress.hideProgress();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    sendFile(groupId, filePath, yourId, displayName, "image");
                                }
                            });
                        }
                    }
                });
            }
        }


        if (requestCode == PDF && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            int rs = displayMediaFileMetaData(uri);
            if (rs > capacity) {
                Toast.makeText(GroupMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
                return;
            } else {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String path = "PDF File/" + "pdf_" + timestamp;

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(path);
                customProgress.showProgress(this, "Đang tải", false);

                storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            customProgress.hideProgress();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    sendFile(groupId, filePath, yourId, displayName, "pdf");
                                }
                            });
                        }

                    }
                });

            }
        }


        if (requestCode == DOCX && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            int rs = displayMediaFileMetaData(uri);
            if (rs > capacity) {
                Toast.makeText(GroupMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
                return;
            } else {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String path = "Docx File/" + "docx_" + timestamp;
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(path);
                customProgress.showProgress(this, "Đang tải", false);

                storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            customProgress.hideProgress();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    sendFile(groupId, filePath, yourId, displayName, "docx");
                                }
                            });

                        }

                    }
                });

            }
        }

        if (requestCode == FILE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            int rs = displayMediaFileMetaData(uri);
            if (rs > capacity) {
                Toast.makeText(GroupMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
                return;
            } else {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String path = "Other Files/" + "files_" + timestamp;
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(path);
                customProgress.showProgress(this, "Đang tải", false);

                storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            customProgress.hideProgress();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    sendFile(groupId, filePath, yourId, displayName, "other");
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void sendFile(String friendId, String file, String userId, String fileName, String fileType) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);

        fireStore.collection("Users").document(yourId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username = document.get("username").toString();
                        String avatar = document.get("imageUrl").toString();
                        sendFileToGroup(yourId, groupId, currentTime, date, file, fileName, fileType, username, avatar);
                    }
                }
            }
        });

    }

    private void sendFileToGroup(String yourId, String groupId, String currentTime, Date date, String file, String fileName, String fileType, String username, String avatar) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", yourId);
        hashMap.put("receiver", groupId);
        hashMap.put("message", "");
        hashMap.put("time", currentTime);
        hashMap.put("file", file);
        hashMap.put("date", date);
        hashMap.put("fileName", fileName);
        hashMap.put("fileType", fileType);
        hashMap.put("memberName", username);
        hashMap.put("avatarLink", avatar);

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

    public int displayMediaFileMetaData(Uri uri) {

        try (Cursor cursor = this.getContentResolver()
                .query(uri, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // DISPLAY NAME
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

                // FILE SIZE
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                int byte_size = -1;
                if (!cursor.isNull(sizeIndex)) {
                    byte_size = cursor.getInt(sizeIndex);
                }
                return byte_size;
            }
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.to_left, R.anim.to_right);
        finish();
    }

    public void setIsOnline(String isOnline) {
        FirebaseUser userOfFirebase = FirebaseAuth.getInstance().getCurrentUser();
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