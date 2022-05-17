package com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomePageViewPagerAdapter extends FragmentStateAdapter {
    public HomePageViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ContactFragment();
            case 1:
                return new GroupChatFragment();
            default:
                return new GroupChatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
