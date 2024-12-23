package com.example.heartflowapp.view.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.heartflowapp.view.fragments.manager.DonorListFragment;

public class EventDetailsPagerAdapter extends FragmentStateAdapter {

    private final String eventId;

    public EventDetailsPagerAdapter(@NonNull Fragment fragment, String eventId) {
        super(fragment);
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return DonorListFragment.newInstance(eventId);
        } else {
//            return VolunteerListFragment.newInstance(eventId);
            return DonorListFragment.newInstance(eventId);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Donors and Volunteers
    }
}

