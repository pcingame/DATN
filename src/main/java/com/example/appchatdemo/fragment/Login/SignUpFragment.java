package com.example.appchatdemo.fragment.Login;

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
import com.example.appchatdemo.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpFragment extends Fragment {

    private EditText edtEmail, edtPassword, edtFullName, edtConfirmPassword;
    private Button btnCreateAccount;
    private TextView tvSignIn;
    private AuthViewModel viewModel;
    private NavController navController;
    CustomProgress customProgress = CustomProgress.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(Objects.requireNonNull(getActivity()).getApplication())).get(AuthViewModel.class);

        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    navController.navigate(R.id.action_signUpFragment_to_signInFragment);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtFullName = view.findViewById(R.id.edt_name_sign_up);
        edtEmail = view.findViewById(R.id.edt_email_sign_up);
        edtPassword = view.findViewById(R.id.edt_password_sign_up);
        edtConfirmPassword = view.findViewById(R.id.edt_confirm_password_sign_up);
        tvSignIn = view.findViewById(R.id.tv_sign_in);
        btnCreateAccount = view.findViewById(R.id.btn_create_acc);

        navController = Navigation.findNavController(view);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtFullName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();

                if (name.isEmpty()) {
                    edtFullName.setError("Enter a name");
                }

                else if (email.isEmpty()) {
                    edtFullName.setError("Enter a email");
                }

                else if (password.isEmpty()) {
                    edtFullName.setError("Enter a password");
                }

                else if (confirmPassword.isEmpty()) {
                    edtFullName.setError("Enter a password");
                }

                else if (password.length() < 6) {
                    edtPassword.setError("Password length must be 6 or more characters");
                }

                else if (!password.equals(confirmPassword)){
                    edtConfirmPassword.setError("Incorrect password");
                }

                else {
                    customProgress.showProgress(getContext(), "Signing up...", true);

                    viewModel.register(name, email, password);
                }


            }
        });

    }


}
