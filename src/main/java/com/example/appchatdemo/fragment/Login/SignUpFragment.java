package com.example.appchatdemo.fragment.Login;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.appchatdemo.model.DefaultInformationModel;
import com.example.appchatdemo.viewmodel.AuthViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SignUpFragment extends Fragment {

    private EditText edtEmail, edtPassword, edtFullName, edtConfirmPassword;
    private Button btnCreateAccount;
    private ImageView imgBack;
    private TextView tvSignIn;
    private AuthViewModel authViewModel;
    private NavController navController;
    private TextInputLayout textInputName, textInputEmail, textInputPassword, textInputRePassword;
    CustomProgress customProgress = CustomProgress.getInstance();
    private Boolean isAllFieldsChecked = false;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();


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

        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.
                getInstance(requireActivity().getApplication())).get(AuthViewModel.class);

        authViewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
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

        imgBack = view.findViewById(R.id.img_back_sign_up);
        edtEmail = view.findViewById(R.id.edt_email_sign_up);
        edtPassword = view.findViewById(R.id.edt_password_sign_up);
        edtConfirmPassword = view.findViewById(R.id.edt_confirm_password_sign_up);
        tvSignIn = view.findViewById(R.id.tv_sign_in);
        btnCreateAccount = view.findViewById(R.id.btn_create_acc);
        textInputEmail = view.findViewById(R.id.text_input_email_sign_up);
        textInputPassword = view.findViewById(R.id.text_input_password_sign_up);
        textInputRePassword = view.findViewById(R.id.text_input_confirm_password_sign_up);

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

                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                customProgress.showProgress(getContext(), getString(R.string.signing_up), true);
                isAllFieldsChecked = CheckAllFields();

                if (isAllFieldsChecked) {
                    fireStore.collection("DefaultInformation").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            boolean check = false;
                            String name = "";
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                DefaultInformationModel defaultInformationModel = snapshot.toObject(DefaultInformationModel.class);
                                if (defaultInformationModel.getEmailInfo().equals(email)) {
                                    check = true;
                                    name = defaultInformationModel.getNameInfo();
                                    break;
                                } else {
                                    check = false;
                                }
                            }
                            if (check) {
                                authViewModel.register(name, email, password);
                            } else {
                                FancyToast.makeText(getContext(), "Địa chỉ email không khớp với bất kỳ nhân viên nào. Vui lòng kiểm tra lại", Toast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                                customProgress.hideProgress();
                            }
                        }
                    });

                } else {
                    customProgress.hideProgress();
                }
            }

        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });

    }

    private boolean CheckAllFields() {
        String email = edtEmail.getText().toString().trim();
        String emailPattern2 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+.+[a-z]+";
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        //Check empty
        if (
                !validateEmpty(email, textInputEmail, getString(R.string.validate_email), getString(R.string.invalid_information)) ||
                        !validateEmpty(password, textInputPassword, getString(R.string.validate_password), getString(R.string.invalid_information)) ||
                        !validateEmpty(confirmPassword, textInputRePassword, getString(R.string.validate_repassword), getString(R.string.invalid_information))
        )
            return false;

        if (!email.matches(emailPattern2)) {
            showMessageErrorScreen(textInputEmail, getString(R.string.format_email_vn), getString(R.string.invalid_information));
            return false;
        } else textInputEmail.setErrorEnabled(false);

        if (password.length() < 6 || password.length() > 29 || !password.matches(getString(R.string.regex_password))) {
            showMessageErrorScreen(textInputPassword, getString(R.string.regex_password_error), getString(R.string.invalid_information));
            return false;
        } else textInputPassword.setErrorEnabled(false);

        if (!password.equals(confirmPassword)) {
            showMessageErrorScreen(textInputRePassword, getString(R.string.duplicate_password), getString(R.string.invalid_information));
            return false;
        } else textInputRePassword.setErrorEnabled(false);

        return true;
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
        FancyToast.makeText(getContext(), messageInfo, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
    }

}
