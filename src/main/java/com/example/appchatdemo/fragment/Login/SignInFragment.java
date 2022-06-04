package com.example.appchatdemo.fragment.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appchatdemo.CustomProgress;
import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.DashBoardActivity;
import com.example.appchatdemo.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;


public class SignInFragment extends Fragment {

    private EditText edtEmail, edtPassword;
    private AuthViewModel viewModel;
    private NavController navController;
    private Button btnSignIn;
    private TextView tvSignUp, tvForgotPassword;
    CustomProgress customProgress = CustomProgress.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitApplication();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(AuthViewModel.class);

        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser != null){
                    Intent intent = new Intent(getActivity(), DashBoardActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtEmail = view.findViewById(R.id.edt_email_sign_in);
        edtPassword = view.findViewById(R.id.edt_password_sign_in);
        tvSignUp = view.findViewById(R.id.tv_sign_up);
        btnSignIn = view.findViewById(R.id.btn_sign_in);
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);

        navController = Navigation.findNavController(view);

        edtPassword.addTextChangedListener(loginTextWatcher);
        edtEmail.addTextChangedListener(loginTextWatcher);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+.+[a-z]+";

                if ( !email.matches(emailPattern2) || email.isEmpty()){
                    FancyToast.makeText(getContext(), getString(R.string.invalid_information), Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }else if(password.length() < 6 || password.length() > 29 || !password.matches(getString(R.string.regex_password))){
                    FancyToast.makeText(getContext(), getString(R.string.exist_account_login_vn), Toast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
                else {
                    customProgress.showProgress(getContext(), getString(R.string.loading), true);
                    viewModel.signIn(email, password);
                }

            }
        });
    }

    private void exitApplication() {

    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            btnSignIn.setEnabled(!email.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}