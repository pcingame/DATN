package com.example.appchatdemo.fragment.bottomnavigationmain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appchatdemo.R;
import com.example.appchatdemo.adapter.HomePageViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class HomePageFragment extends Fragment {

    private TabLayout tabLayoutHome;
    private ViewPager2 viewPager;
    private HomePageViewPagerAdapter homePageViewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayoutHome = view.findViewById(R.id.tabLayoutHome);
        homePageViewPagerAdapter = new HomePageViewPagerAdapter(requireActivity());
        viewPager.setAdapter(homePageViewPagerAdapter);
        viewPager.setUserInputEnabled(false);

        new TabLayoutMediator(tabLayoutHome, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.private_chat));
                    break;
                case 1:
                    tab.setText(getString(R.string.group_chat));
                    break;
            }
        }).attach();

    }
}