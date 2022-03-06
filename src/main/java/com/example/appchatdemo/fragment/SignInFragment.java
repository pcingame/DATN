package com.example.appchatdemo.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.appchatdemo.R;
import com.example.appchatdemo.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class SignInFragment extends Fragment {


    private EditText edtEmail, edtPassword, edtFullName, edtRePassword;
    private Button btnSignIn;
    private TextView tvSignUp, tvForgotPassword;
    //private ProgressDialog progressDialogSignIn;
    private AuthViewModel viewModel;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(Objects.requireNonNull(getActivity()).getApplication())).get(AuthViewModel.class);

        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser != null){
                    navController.navigate(R.id.action_signInFragment_to_signOutFragment);
                }
            }
        });

        //observeLogin();
    }

//    private void observeLogin() {
//        viewModel.setProgressbarObservable().observe(this, new  Observer<Boolean>() {
//            @Override
//            public void onChanged(final Boolean progressObserve) {
//                if(progressObserve){
//                  // show your progress
//                }
//                else {
//                   // hide your progress
//                }
//            }
//        });
    //   }

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
        //progressDialogSignIn = new ProgressDialog(getContext());

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
//                progressDialogSignIn.setTitle("Loading...");
//                progressDialogSignIn.setCancelable(false);
//                progressDialogSignIn.show();

                if(!email.isEmpty() && !password.isEmpty()){
                    // Toast.makeText(getContext(), "a", Toast.LENGTH_SHORT).show();
                    //progressDialogSignIn.dismiss();
                    viewModel.signIn(email, password);
                    //  progressDialog.dismiss();
                }
            }
        });
    }
}