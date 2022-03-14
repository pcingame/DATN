package com.example.appchatdemo.fragment.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.Objects;


public class SignInFragment extends Fragment {

    private EditText edtEmail, edtPassword;
    private AuthViewModel viewModel;
    private NavController navController;
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
                getInstance(Objects.requireNonNull(getActivity()).getApplication())).get(AuthViewModel.class);

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
        TextView tvSignUp = view.findViewById(R.id.tv_sign_up);
        Button btnSignIn = view.findViewById(R.id.btn_sign_in);
        TextView tvForgotPassword = view.findViewById(R.id.tv_forgot_password);

        navController = Navigation.findNavController(view);

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

                customProgress.showProgress(getContext(), "Loading...", true);
                if(!email.isEmpty() && !password.isEmpty()){

                    viewModel.signIn(email, password);

                }
            }
        });
    }

    private void exitApplication() {

    }

}