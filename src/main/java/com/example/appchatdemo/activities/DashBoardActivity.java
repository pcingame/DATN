package com.example.appchatdemo.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appchatdemo.R;
//import com.example.appchatdemo.databinding.ActivityDashBoardBinding;
import com.example.appchatdemo.databinding.ActivityDashBoardBinding;
import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.NPFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.OptionFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.ProfileFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.ProjectFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

public class DashBoardActivity extends AppCompatActivity {

    private ActivityDashBoardBinding binding;
    public static final int FRAGMENT_PROFILE = 0;
    public static final int FRAGMENT_HOME = 1;
    public static final int FRAGMENT_PROJECT = 2;
    public static final int FRAGMENT_OPTION = 3;
    public static final int FRAGMENT_NP = 4;

    FirebaseFirestore fireStore;
    private String userId;


    private int mCurrentFragment = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fireStore = FirebaseFirestore.getInstance();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_profile:
                    openProfileFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);

                    break;
                case R.id.nav_np:
                    openNPFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_np).setChecked(true);
                    FancyToast.makeText(this, getString(R.string.no_data), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    break;
                case R.id.nav_project:
                    openProjectFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_project).setChecked(true);
                    FancyToast.makeText(this, getString(R.string.no_data), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    break;
                case R.id.nav_option:
                    openOptionFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_option).setChecked(true);
                    FancyToast.makeText(this, getString(R.string.no_data), FancyToast.LENGTH_SHORT, FancyToast.DEFAULT, false).show();
                    break;
                case R.id.nav_home:
                    openChatFragment();
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

    private void openChatFragment() {
        if (mCurrentFragment != FRAGMENT_HOME) {
            replaceFragment(new HomePageFragment());
            mCurrentFragment = FRAGMENT_HOME;
        }
    }

    private void openProjectFragment() {
        if (mCurrentFragment != FRAGMENT_PROJECT) {
            replaceFragment(new ProjectFragment());
            mCurrentFragment = FRAGMENT_PROJECT;
        }
    }

    private void openNPFragment() {
        if (mCurrentFragment != FRAGMENT_NP) {
            replaceFragment(new NPFragment());
            mCurrentFragment = FRAGMENT_NP;
        }
    }

    private void openOptionFragment() {
        if (mCurrentFragment != FRAGMENT_OPTION) {
            replaceFragment(new OptionFragment());
            mCurrentFragment = FRAGMENT_OPTION;
        }
    }


    private void replaceFragment(Fragment fragment){
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

    public void setIsOnline(String isOnline){
        FirebaseUser userOfFirebase = FirebaseAuth.getInstance().getCurrentUser();

        if (userOfFirebase!=null) {
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