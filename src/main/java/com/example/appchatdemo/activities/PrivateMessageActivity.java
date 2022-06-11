package com.example.appchatdemo.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import com.bumptech.glide.Glide;
import com.example.appchatdemo.utility.CustomProgress;
import com.example.appchatdemo.R;
import com.example.appchatdemo.databinding.ActivityPrivateMessageBinding;
import com.example.appchatdemo.interfaces.IClickItemFile;
import com.example.appchatdemo.adapter.PrivateMessageAdapter;
import com.example.appchatdemo.model.PrivateMessageModel;
import com.example.appchatdemo.model.UserModel;
import com.example.appchatdemo.utility.NetworkChangeListener;
import com.example.appchatdemo.viewmodel.PrivateMessageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PrivateMessageActivity extends AppCompatActivity {

    private ActivityPrivateMessageBinding binding;
    FirebaseFirestore fireStore;
    FirebaseAuth firebaseAuth;
    PrivateMessageAdapter mPrivateMessageAdapter;
    private PrivateMessageViewModel privateMessageViewModel;
    private String friendId, message, userId, userName, userIdd;
    private final int IMAGE = 11, PDF = 22, DOCX = 33, FILE = 44;
    CustomProgress customProgress = CustomProgress.getInstance();
    private String displayName;
    List<PrivateMessageModel> privateMessageModelList;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivateMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        UserModel userModel = (UserModel) bundle.get("userModel");

        friendId = userModel.getUserId();
        userName = userModel.getUsername();

        binding.tvFriendName.setText(userModel.getUsername());
        Glide.with(this).load(userModel.getImageUrl()).centerCrop().into(binding.imgAvatarFriend);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.rcvPrivateMessage.setLayoutManager(linearLayoutManager);

        privateMessageModelList = new ArrayList<>();
        mPrivateMessageAdapter = new PrivateMessageAdapter(privateMessageModelList, new IClickItemFile() {
            @Override
            public void onClickItemFile(PrivateMessageModel privateMessageModel) {
                onClickDownloadFile(privateMessageModel);
            }
        });
        binding.rcvPrivateMessage.setAdapter(mPrivateMessageAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        binding.btnBackPrivateMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToContactFragment();

            }
        });

        binding.btnSendMessagePrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = binding.edtMessage.getText().toString().trim();

                if (message.isEmpty()) {
                    binding.edtMessage.setError("Chưa nhập tin nhắn kìa");
                } else {
                    SendMessage(friendId, message, userId);
                    binding.edtMessage.setText(" ");
                }
            }
        });

        binding.btnSendFilePrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilePickDialog();
            }
        });

        privateMessageViewModel = new ViewModelProvider(this).get(PrivateMessageViewModel.class);
        privateMessageViewModel.getMessageFromFireStore(friendId);
        privateMessageViewModel.returnMyMessages().observe(this, new Observer<List<PrivateMessageModel>>() {
            @Override
            public void onChanged(List<PrivateMessageModel> privateMessageModels) {
                mPrivateMessageAdapter.setPrivateMessageModelList(privateMessageModels);
                binding.rcvPrivateMessage.setAdapter(mPrivateMessageAdapter);
            }
        });

    }

    private void backToContactFragment() {
        Intent intent = new Intent(PrivateMessageActivity.this, DashBoardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }

    private void onClickDownloadFile(PrivateMessageModel privateMessageModel) {
        String urlFile = privateMessageModel.getFile();
        String fileName = privateMessageModel.getFileName();
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


    private void showFilePickDialog() {
        CharSequence options[] = new CharSequence[]{
                "Ảnh",
                "PDF",
                "Docx",
                "Khác"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(PrivateMessageActivity.this);
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
                    intent.setType("*/*");
                    startActivityForResult(Intent.createChooser(intent, "Chọn file khác"), FILE);
                }
            }
        });
        builder.create().show();
    }


    private void SendMessage(String friendId, String message, String userId) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        //SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", userId);
        hashMap.put("receiver", friendId);
        hashMap.put("message", message);
        hashMap.put("time", currentTime);
        hashMap.put("file", "default");
        hashMap.put("date", date);
        hashMap.put("fileName", "");
        hashMap.put("fileType", "");
        hashMap.put("id", userId + friendId);

        fireStore.collection("PrivateMessages").document(currentTime).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                }
            }
        });

        DocumentReference documentReference = fireStore.collection("Users").document(userId);
        documentReference.update("listChatPrivate", FieldValue.arrayUnion(friendId));

        DocumentReference documentReference2 = fireStore.collection("Users").document(friendId);
        documentReference2.update("listChatPrivate", FieldValue.arrayUnion(userId));

        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("user1", userId);
        hashMap2.put("user2", friendId);
        hashMap2.put("lastMessageTime", date);

        fireStore.collection("Users").document(userId).collection("chat")
                .document(userId + "_" + friendId).set(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void sendFile(String friendId, String file, String userId, String fileName, String fileType) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        // SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", userId);
        hashMap.put("receiver", friendId);
        hashMap.put("message", "");
        hashMap.put("time", currentTime);
        hashMap.put("file", file);
        hashMap.put("date", date);
        hashMap.put("fileName", fileName);
        hashMap.put("fileType", fileType);


        fireStore.collection("PrivateMessages").document(currentTime).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
        DocumentReference documentReference = fireStore.collection("Users").document(userId);
        documentReference.update("listChatPrivate", FieldValue.arrayUnion(friendId));

        DocumentReference documentReference2 = fireStore.collection("Users").document(friendId);
        documentReference2.update("listChatPrivate", FieldValue.arrayUnion(userId));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            int rs = displayMediaFileMetaData(uri);
            if (rs > 20971520) {
                Toast.makeText(PrivateMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
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
                                    sendFile(friendId, filePath, userId, displayName, "image");
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
            if (rs > 20971520) {
                Toast.makeText(PrivateMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
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
                                    sendFile(friendId, filePath, userId, displayName, "pdf");
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
            if (rs > 20971520) {
                Toast.makeText(PrivateMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
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
                                    sendFile(friendId, filePath, userId, displayName, "docx");
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
            if (rs > 20971520) {
                Toast.makeText(PrivateMessageActivity.this, getString(R.string.file_size), Toast.LENGTH_SHORT).show();
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
                                    sendFile(friendId, filePath, userId, displayName, "other");
                                }
                            });
                        }
                    }
                });
            }
        }
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
        backToContactFragment();
    }

    public void setIsOnline(String isOnline) {
        FirebaseUser userOfFirebase = FirebaseAuth.getInstance().getCurrentUser();
        if (userOfFirebase != null) {
            userId = userOfFirebase.getUid();
        }

        fireStore.collection("Users").document(userId).update("activeStatus", isOnline).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        privateMessageViewModel.resetAll();
        super.onStop();

    }

}