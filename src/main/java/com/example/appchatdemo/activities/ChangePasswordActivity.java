package com.example.appchatdemo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.appchatdemo.CustomProgress;
import com.example.appchatdemo.R;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private TextInputLayout textInputOldPassword, textInputNewPassword, textInputRePassword;
    CustomProgress customProgress = CustomProgress.getInstance();
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        imgBack = findViewById(R.id.img_back_change_password);
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_new_password);
        textInputOldPassword = findViewById(R.id.text_input_email_sign_up);
        textInputNewPassword = findViewById(R.id.text_input_password_sign_up);
        textInputRePassword = findViewById(R.id.text_input_confirm_password_sign_up);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        imgBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}