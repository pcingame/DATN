package com.example.appchatdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appchatdemo.R;
import com.example.appchatdemo.viewmodel.AuthViewModel;

import java.util.Objects;

public class ForgotPasswordFragment extends Fragment {
    private ImageView imgBack;
    private EditText edtInputEmail;
    private Button btnSend;
    private NavController navController;
    private AuthViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(Objects.requireNonNull(getActivity()).getApplication())).get(AuthViewModel.class);

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
        edtInputEmail = view.findViewById(R.id.edt_input_email_forget);
        btnSend = view.findViewById(R.id.btn_send_forgot_password);
        navController = Navigation.findNavController(view);

        initListener();
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
        if(!email.isEmpty()){
            viewModel.forgotPassword(email);
        }else {
            Toast.makeText(getContext(), "Email không được để trống", Toast.LENGTH_SHORT).show();
        }
    }
}
