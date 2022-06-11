package com.example.appchatdemo.fragment.Login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appchatdemo.utility.CheckNetwork;
import com.example.appchatdemo.utility.CustomProgress;
import com.example.appchatdemo.R;
import com.example.appchatdemo.viewmodel.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class ForgotPasswordFragment extends Fragment {

    private ImageView imgBack;
    private TextInputEditText edtInputEmail;
    private Button btnSend;
    private NavController navController;
    private AuthViewModel authViewModel;
    public CustomProgress customProgress = CustomProgress.getInstance();
    FirebaseAuth auth;
    CheckNetwork checkNetwork = new CheckNetwork();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onBackPress();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgBack = view.findViewById(R.id.img_back_forget_password);
        edtInputEmail = view.findViewById(R.id.edt_email_forget);
        btnSend = view.findViewById(R.id.btn_send_forgot_password);
        navController = Navigation.findNavController(view);
        auth = FirebaseAuth.getInstance();

        initListener();
        edtInputEmail.addTextChangedListener(checkEmail);
    }

    private void initListener() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_forgotPasswordFragment_to_signInFragment);
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickForgotPassword();
            }
        });
    }

    private void onClickForgotPassword() {
        String email = edtInputEmail.getText().toString();
        if (checkNetwork.isNetworkAvailable(getContext())) {
            customProgress.showProgress(getContext(), getString(R.string.checking), true);
            forgotPassword(email);
        } else {
            FancyToast.makeText(getContext(), getString(R.string.forgot_network), Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
        }
    }

    private void forgotPassword(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    customProgress.hideProgress();
                    FancyToast.makeText(getContext(), getString(R.string.send_reset_password_success), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    navController.navigate(R.id.action_forgotPasswordFragment_to_signInFragment);
                } else {
                    customProgress.hideProgress();
                    String c = task.getException().getMessage();

                    if (c.equals(getString(R.string.exist_account_el))) {
                        c = getString(R.string.exist_account_vn);
                    } else if (c.equals(getString(R.string.format_email_el))) {
                        c = getString(R.string.format_email_vn);

                    } else if (c.equals(getString(R.string.block_request_el))) {
                        c = getString(R.string.block_request_vn);
                    }
                    FancyToast.makeText(getContext(), c, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });
    }

    private final TextWatcher checkEmail = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String email = edtInputEmail.getText().toString().trim();
            String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+.+[a-z]+";

            if (email.matches(emailPattern2) && s.length() > 0) {
                btnSend.setEnabled(true);
            } else {
                btnSend.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_forgotPasswordFragment_to_signInFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
