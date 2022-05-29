package com.example.appchatdemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment.ContactFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment.ListChatFragment;
import com.example.appchatdemo.fragment.bottomnavigationmain.HomePageChildFragment.ListGroupChatFragment;

public class HomePageViewPagerAdapter extends FragmentStateAdapter {
    public HomePageViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ListChatFragment();
            case 1:
                return new ListGroupChatFragment();
            default:
                return new ListGroupChatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
