package com.example.appchatdemo.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appchatdemo.R;
import com.example.appchatdemo.utility.CustomProgress;
import com.example.appchatdemo.utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePasswordd;
    private TextInputLayout textInputOldPassword, textInputNewPassword, textInputRePassword;
    CustomProgress customProgress = CustomProgress.getInstance();
    private ImageView imgBack;
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Boolean isAllFieldsChecked = false;
    private String userIdd;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        imgBack = findViewById(R.id.img_back_change_password);
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_new_password);
        textInputOldPassword = findViewById(R.id.text_input_old_password);
        textInputNewPassword = findViewById(R.id.text_input_new_password);
        textInputRePassword = findViewById(R.id.text_input_confirm_new_password);
        btnChangePasswordd = findViewById(R.id.btn_change_pass);

        imgBack.setOnClickListener(v -> finish());
        btnChangePasswordd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                String newPassword = edtConfirmPassword.getText().toString().trim();
                isAllFieldsChecked = checkAllField();
                if (isAllFieldsChecked) {
                    fireStore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String oldPassword = document.get("password").toString();
                                    String email = document.get("email").toString();
                                    AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                customProgress.showProgress(ChangePasswordActivity.this, "Đang kiểm tra", false);
                                                changePassword(userId, newPassword);
                                            } else {
                                                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("FFF", "No such document");
                                }
                            } else {
                                Log.d("FFF", "get failed with ", task.getException());
                            }
                        }
                    });
                }
            }
        });
    }

    private Boolean checkAllField() {
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        if (!validateEmpty(oldPass, textInputOldPassword, getString(R.string.validate_password), getString(R.string.invalid_information)) ||
                !validateEmpty(newPass, textInputNewPassword, getString(R.string.validate_password), getString(R.string.invalid_information)) ||
                !validateEmpty(confirmPass, textInputRePassword, getString(R.string.validate_password), getString(R.string.invalid_information)))
            return false;

        if (newPass.length() < 6 || newPass.length() > 29 || !newPass.matches(getString(R.string.regex_password))) {
            showMessageErrorScreen(textInputNewPassword, getString(R.string.regex_password_error), getString(R.string.invalid_information));
            return false;
        } else textInputNewPassword.setErrorEnabled(false);

        if (!newPass.equals(confirmPass)) {
            showMessageErrorScreen(textInputRePassword, getString(R.string.duplicate_password), getString(R.string.invalid_information));
            return false;
        } else textInputRePassword.setErrorEnabled(false);

        return true;
    }

    private void changePassword(String userId, String newPassword) {
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    customProgress.hideProgress();
                    DocumentReference documentReference = fireStore.collection("Users").document(userId);
                    documentReference.update("password", newPassword);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePasswordActivity.this);
                    alertDialogBuilder.setTitle(getString(R.string.notice));
                    alertDialogBuilder
                            .setMessage(getString(R.string.change_pass_dialog))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.sign_out),
                                    (dialog, id) -> {
                                        signout();
                                    }).setNegativeButton("Ở lại", (dialog, which) -> {
                                dialog.dismiss();
                                Intent intent = new Intent(ChangePasswordActivity.this, DashBoardActivity.class);
                                startActivity(intent);
                                finish();
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signout() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        finishActivity();
    }

    boolean validateEmpty(String field, TextInputLayout fieldScreen, String messageError, String messageInfo) {
        if (field.isEmpty()) {
            showMessageErrorScreen(fieldScreen, messageError, messageInfo);
            return false;
        }
        fieldScreen.setErrorEnabled(false);
        return true;
    }

    void showMessageErrorScreen(TextInputLayout fieldScreen, String messageError, String messageInfo) {
        fieldScreen.setError(messageError);
        FancyToast.makeText(ChangePasswordActivity.this, messageInfo, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void finishActivity() {
        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
        startActivity(intent);
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