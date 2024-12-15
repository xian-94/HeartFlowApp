package com.example.heartflowapp.utils;

import androidx.fragment.app.FragmentManager;

import com.example.heartflowapp.view.general.ProgressFragment;

public class ProgressManager {
    private static ProgressFragment progressFragment;

    public static void showProgress(FragmentManager fragmentManager) {
        if (progressFragment == null) {
            progressFragment = new ProgressFragment();
            progressFragment.show(fragmentManager, "progress");
        }
    }

    public static void dismissProgress() {
        if (progressFragment != null && progressFragment.isAdded()) {
            progressFragment.dismiss();
            progressFragment = null;
        }
    }
}


