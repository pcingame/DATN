package com.example.appchatdemo.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appchatdemo.R;
import com.example.appchatdemo.activities.DashBoardActivity;
import com.example.appchatdemo.viewmodel.AuthViewModel;

import java.util.Objects;


public class SplashFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (viewModel.getCurrentUser() != null){
                    navController.navigate(R.id.action_splashFragment_to_signOutFragment);
//                    Intent intent = new Intent(getActivity(), DashBoardActivity.class);
//                    startActivity(intent);
                }else{
                    navController.navigate(R.id.action_splashFragment_to_signInFragment);

                }
            }
        }, 4000);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication())).get(AuthViewModel.class);
        navController = Navigation.findNavController(view);
    }
}