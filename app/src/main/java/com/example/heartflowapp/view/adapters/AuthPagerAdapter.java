package com.example.heartflowapp.view.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.heartflowapp.view.fragments.LoginFragment;
import com.example.heartflowapp.view.fragments.SignUpFragment;

public class AuthPagerAdapter extends FragmentStateAdapter {
    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SignUpFragment();
            case 1:
                return new LoginFragment();
            default:
                return new SignUpFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
