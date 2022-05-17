package com.example.appchatdemo.fragment.bottomnavigationmain;

import static android.app.Activity.RESULT_OK;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.appchatdemo.CustomProgress;
import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.MainActivity;
import com.example.appchatdemo.viewmodel.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private AuthViewModel authViewModel;
    private Button btnSignOut, btnUpdateProfile;
    private CircleImageView imgUserAvatar;
    private ImageView imgPickImage;
    private EditText edtUserName, edtStatus;

    String userNameUpdated, userId, userStatusUpdated, imgUrl;
    FirebaseFirestore fireStore;
    FirebaseAuth firebaseAuth;
    public static final int CAMERA_PICK = 100; // CAMERA A
    public static final int GALLERY_PICK = 200; // GALLERY PIC
    Uri imageUri = null;
    CustomProgress customProgress = CustomProgress.getInstance();
    //public  String regex_username = "[a-zA-Z_ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ ?]*";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(AuthViewModel.class);

        authViewModel.getUserLoggedStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgPickImage = view.findViewById(R.id.imgPickImage);
        imgUserAvatar = view.findViewById(R.id.imgUserAvatar);

        edtUserName = view.findViewById(R.id.edtUserName);
        edtStatus = view.findViewById(R.id.edtUserStatus);

        btnSignOut = view.findViewById(R.id.btn_sign_out);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        imgPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Bạn có muốn đăng xuất không ?");
                alertDialogBuilder
                        .setMessage("Bấm OK để đăng xuất")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        authViewModel.signOut();
                                    }
                                })

                        .setNegativeButton("Ở lại", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle(getString(R.string.profile_title_dialog));
                alertDialogBuilder
                        .setMessage(getString(R.string.update_ok))
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                (dialog, id) -> {
                                    userNameUpdated = edtUserName.getText().toString();
                                    userStatusUpdated = edtStatus.getText().toString();

                                    if (userNameUpdated.isEmpty()) {
                                        edtUserName.setError(getString(R.string.forgot_enter_name));
                                    } else if (!userNameUpdated.matches(getString(R.string.regex_username))) {
                                        edtUserName.setError(getString(R.string.regex_username_error));
                                    } else if (userNameUpdated.length() > 50) {
                                        edtUserName.setError(getString(R.string.username_length_error));
                                    } else {
                                        updateProfile(userNameUpdated, userStatusUpdated);
                                    }


                                })

                        .setNegativeButton("Ở lại", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String theUserId = firebaseUser.getUid();

        fireStore.collection("Users").document(theUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot snapshot = task.getResult();
                    String userName = snapshot.getString("username");
                    String status = snapshot.getString("status");
                    imgUrl = snapshot.getString("imageUrl");

                    edtUserName.setText(userName);
                    edtStatus.setText(status);
                    Glide.with(requireContext()).load(imgUrl).centerCrop().into(imgUserAvatar);
                }
            }
        });
    }

    private void showImagePickDialog() {

        String[] items = {"Máy ảnh", "Bộ sưu tập"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

        builder.setTitle("Chọn ảnh");
        builder.setItems(items, (dialog, i) -> {

            if (i == 0) {
                showDialogCamera();
            }

            if (i == 1) {
                showGalleryDialogue();
            }
        });

        builder.create().show();


    }

    private void showGalleryDialogue() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK);
    }

    private void showDialogCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.TITLE, "Temp Desc");
        imageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            customProgress.showProgress(getContext(), getString(R.string.loading), false);
            assert data != null;
            Uri uri = data.getData();

            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = "Photos/" + "photos_" + timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String photoId = uri.toString();
                            imageUri = uri;
                            Glide.with(requireContext()).load(imageUri).centerCrop().into(imgUserAvatar);

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            userId = firebaseUser.getUid();

                            fireStore.collection("Users").document(userId).update("imageUrl", photoId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            customProgress.hideProgress();
                                            FancyToast.makeText(getContext(), getString(R.string.update_profile_success), Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                        }
                                    });
                        }
                    });
                }
            });
        }

        if (requestCode == CAMERA_PICK && resultCode == RESULT_OK) {
            customProgress.showProgress(getContext(), getString(R.string.loading), false);
            Uri uri = imageUri;
            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = "Photos/" + "photos_" + timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String photoId = uri.toString();
                            imageUri = uri;
                            Glide.with(requireContext()).load(imageUri).centerCrop().into(imgUserAvatar);

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            userId = firebaseUser.getUid();


                            fireStore.collection("Users").document(userId).update("imageUrl", photoId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            customProgress.hideProgress();
                                            FancyToast.makeText(getContext(), getString(R.string.update_profile_success), Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    private void updateProfile(String userNameUpdated, String userStatusUpdated) {

        customProgress.showProgress(getContext(), getString(R.string.loading), false);
        String userIdAgain = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", userNameUpdated);
        hashMap.put("status", userStatusUpdated);

        fireStore.collection("Users").document(userIdAgain).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (isAdded()) {
                    if (task.isSuccessful()) {
                        customProgress.hideProgress();
                        FancyToast.makeText(getContext(), getString(R.string.update_profile_success), Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    } else {
                        customProgress.hideProgress();
                        FancyToast.makeText(getContext(), getString(R.string.update_profile_fail), Toast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    }
                }
            }


        });
    }

}