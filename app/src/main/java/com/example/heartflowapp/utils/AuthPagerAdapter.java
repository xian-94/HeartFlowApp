package com.example.heartflowapp.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.heartflowapp.view.authentication.SignUpFragment;

public class AuthPagerAdapter extends FragmentStateAdapter {
    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new SignUpFragment();
            case 2:
                //TODO: Add Login fragment
            default:
                return new SignUpFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
