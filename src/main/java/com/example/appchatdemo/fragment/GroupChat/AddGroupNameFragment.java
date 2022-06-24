package com.example.appchatdemo.fragment.GroupChat;

import static android.app.Activity.RESULT_OK;

import android.content.ContentValues;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.CreateGroupChatActivity;
import com.example.appchatdemo.utility.CustomProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddGroupNameFragment extends Fragment {

    private Button btnAddMember;
    private ImageButton btnBack;
    private ImageView btnAddImage;
    private CircleImageView circleImageViewGroup;
    FirebaseFirestore fireStore;
    FirebaseAuth firebaseAuth;
    private EditText edtGroupName;
    private NavController navController;
    public static final int CAMERA_PICK = 10; // CAMERA A
    public static final int GALLERY_PICK = 20; // GALLERY PIC
    private String userId;
    Uri imageUri = null;
    private String path;
    private Uri uri;
    CustomProgress customProgress = CustomProgress.getInstance();
    public final String TAG = CreateGroupChatActivity.class.getSimpleName();
    private String imgLinkDefaultAvatar = "https://firebasestorage.googleapis.com/v0/b/appmiochat.appspot.com/o/Photos%2Fd0742ce2f56f4d7ea522a9149e4b8658.png?alt=media&token=044fbbae-c5f0-4e4f-81d5-0cd31c50abc1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToDashBoardActivity();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_name_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAddMember = view.findViewById(R.id.btnAddMember);
        btnBack = view.findViewById(R.id.btnBack_name);
        btnAddImage = view.findViewById(R.id.imgPickImageGroup);
        edtGroupName = view.findViewById(R.id.edtGroupName);
        circleImageViewGroup = view.findViewById(R.id.imgGroupAvatarCreate);
        navController = Navigation.findNavController(view);
        btnAddMember.setEnabled(true);

        btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtGroupName.getText().toString().trim().isEmpty()) {
                    FancyToast.makeText(getContext(), getString(R.string.validate_group_name), Toast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                } else {
                    Bundle bundle = new Bundle();

                    bundle.putString("groupName", edtGroupName.getText().toString());
                    if (path != null) {
                        bundle.putString("groupAvatar", path);
                    } else bundle.putString("groupAvatar", "");
                    if (uri != null) {
                        bundle.putString("uri", uri.toString());
                    } else bundle.putString("uri", imgLinkDefaultAvatar);

                    navController.navigate(R.id.action_addGroupNameFragment_to_addGroupMemberFragment, bundle);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToDashBoardActivity();
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
    }


    public void backToDashBoardActivity() {
        getActivity().finish();
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
            assert data != null;
            uri = data.getData();
            circleImageViewGroup.setImageURI(uri);
            String timestamp = String.valueOf(System.currentTimeMillis());
            path = "Photos/" + "photos_" + timestamp;

        }

        if (requestCode == CAMERA_PICK && resultCode == RESULT_OK) {
            uri = imageUri;
            circleImageViewGroup.setImageURI(imageUri);
            String timestamp = String.valueOf(System.currentTimeMillis());
            path = "Photos/" + "photos_" + timestamp;

        }
    }

}