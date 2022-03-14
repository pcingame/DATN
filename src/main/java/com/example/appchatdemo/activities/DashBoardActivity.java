package com.example.appchatdemo.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appchatdemo.R;
import com.example.appchatdemo.databinding.ActivityDashBoardBinding;
import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.ProfileFragment;

public class DashBoardActivity extends AppCompatActivity {

    private ActivityDashBoardBinding binding;
    public static final int FRAGMENT_PROFILE = 0;
    public static final int FRAGMENT_HOME = 1;


    private int mCurrentFragment = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.nav_profile:
                    openProfileFragment();
                    binding.bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
                    break;
                case R.id.nav_np:
                case R.id.nav_project:
                case R.id.nav_untilitied:
                    Toast.makeText(getApplicationContext(), "Out of order", Toast.LENGTH_SHORT).show();
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

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}