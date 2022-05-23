package com.example.appchatdemo.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appchatdemo.R;
import com.example.appchatdemo.databinding.ActivityDashBoardBinding;
import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.MemberFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashBoardActivity extends AppCompatActivity {

    private ActivityDashBoardBinding binding;
    public static final int FRAGMENT_PROFILE = 0;
    public static final int FRAGMENT_HOME = 1;
    public static final int FRAGMENT_MEMBER = 2;

    FirebaseFirestore fireStore;
    private String userId;
    private int mCurrentFragment = FRAGMENT_HOME;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fireStore = FirebaseFirestore.getInstance();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    openProfileFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
                    break;
                case R.id.nav_member:
                    openMemberFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_member).setChecked(true);
                    break;
                case R.id.nav_home:
                    openHomeFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
                    break;
            }
            return true;
        });

        replaceFragment(new HomePageFragment());
        binding.bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    private void openProfileFragment() {
        if (mCurrentFragment != FRAGMENT_PROFILE) {
            replaceFragment(new ProfileFragment());
            mCurrentFragment = FRAGMENT_PROFILE;
        }
    }

    private void openHomeFragment() {
        if (mCurrentFragment != FRAGMENT_HOME) {
            replaceFragment(new HomePageFragment());
            mCurrentFragment = FRAGMENT_HOME;
        }
    }


    private void openMemberFragment() {
        if (mCurrentFragment != FRAGMENT_MEMBER) {
            replaceFragment(new MemberFragment());
            mCurrentFragment = FRAGMENT_MEMBER;
        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.exit_title_dialog));
        alertDialogBuilder
                .setMessage(getString(R.string.exit_ok))
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("Ở lại", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setIsOnline(String isOnline) {
        FirebaseUser userOfFirebase = FirebaseAuth.getInstance().getCurrentUser();

        if (userOfFirebase != null) {
            userId = userOfFirebase.getUid();
        }

        fireStore.collection("Users").document(userId).update("activeStatus", isOnline).addOnCompleteListener(new OnCompleteListener<Void>() {
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
}