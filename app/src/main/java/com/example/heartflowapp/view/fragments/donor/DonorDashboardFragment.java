package com.example.heartflowapp.view.fragments.donor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.view.adapters.DonorSiteAdapter;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DonorDashboardFragment extends Fragment {

    private static final String ARG_USER = "USER";

    private String userId;

    public DonorDashboardFragment() {
        // Required empty public constructor
    }

    public static DonorDashboardFragment newInstance(String userId) {
        DonorDashboardFragment fragment = new DonorDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donor_dashboard, container, false);
        MaterialTextView totalDonation = view.findViewById(R.id.total_donation);
        MaterialTextView totalVolunteer = view.findViewById(R.id.total_volunteer);
        getDonationDrives((totalDonationDrives) ->
                totalDonation.setText(String.valueOf(totalDonationDrives)));
        getVolunteerDrives(totalVolunteerDrives -> totalVolunteer.setText(String.valueOf(totalVolunteerDrives)));


        fetchDrives();
        return view;

    }

    private void fetchDrives() {
        DatabaseManager db = new DatabaseManager();
        // Fetch drives where the user is a volunteer
        ProgressManager.showProgress(getChildFragmentManager());
        db.getRef("site")
                .whereArrayContains("volunteers", userId)
                .get()
                .addOnCompleteListener(task1 -> {
                    ProgressManager.dismissProgress();
                    if (task1.isSuccessful()) {
                        List<Site> volunteerDrives = new ArrayList<>();
                        for (DocumentSnapshot document : task1.getResult()) {
                            Site event = document.toObject(Site.class);
                            if (event != null && "COMPLETE".equalsIgnoreCase(event.getStatus())) {
                                volunteerDrives.add(event);
                            }
                        }
                        // Fetch drives where the user is a donor
                        db.getRef("site")
                                .whereArrayContains("donors", userId)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        List<Site> donorDrives = new ArrayList<>();
                                        for (DocumentSnapshot document : task2.getResult()) {
                                            Site event = document.toObject(Site.class);
                                            if (event != null && "COMPLETE".equalsIgnoreCase(event.getStatus())) {
                                                donorDrives.add(event);
                                            }
                                        }

                                        // Combine results from both queries
                                        Set<Site> combinedDrives = new HashSet<>(volunteerDrives);
                                        combinedDrives.addAll(donorDrives);
                                        List<Site> finalDrives = new ArrayList<>(combinedDrives);

                                        // Update RecyclerView
                                        updateRecyclerView(finalDrives);
                                    } else {
                                        Toast.makeText(requireContext(), "Failed to fetch volunteer drives", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Failed to fetch donation drives: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to fetch volunteer drives: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void updateRecyclerView(List<Site> drives) {
        RecyclerView recyclerView = requireView().findViewById(R.id.past_events_container);
        DonorSiteAdapter adapter = new DonorSiteAdapter(drives);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

    }

    // Interface to handle async total fetching
    private interface TotalDonationCallback {
        void onDataFetched(int totalDonation);
    }

    private interface TotalVolunteerCallback {
        void onDataFetched(int totalVolunteer);
    }

    private void getDonationDrives(TotalDonationCallback cb) {
        DatabaseManager db = new DatabaseManager();
        db.getRef("site")
                .whereArrayContains("donors", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cb.onDataFetched(task.getResult().size());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Cannot get total donation", Toast.LENGTH_SHORT).show());
    }

    private void getVolunteerDrives(TotalVolunteerCallback cb) {
        DatabaseManager db = new DatabaseManager();
        db.getRef("site")
                .whereArrayContains("volunteers", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cb.onDataFetched(task.getResult().size());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Cannot get total volunteer events", Toast.LENGTH_SHORT).show());
    }


}