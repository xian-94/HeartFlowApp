package com.example.heartflowapp.view.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.view.fragments.manager.DonorListFragment;
import com.example.heartflowapp.view.fragments.manager.VolunteerListFragment;

public class SiteDetailsPagerAdapter extends FragmentStateAdapter {
    private final Site site;

    public SiteDetailsPagerAdapter(@NonNull Fragment fragment, Site site) {
        super(fragment);
        this.site = site;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return DonorListFragment.newInstance(site.getSiteId());
        } else {
            return VolunteerListFragment.newInstance(site.getSiteId());
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
