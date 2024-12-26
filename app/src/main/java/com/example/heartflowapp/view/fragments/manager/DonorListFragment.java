package com.example.heartflowapp.view.fragments.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.view.adapters.DonorAdapter;

import java.util.ArrayList;
import java.util.List;

public class DonorListFragment extends Fragment {

    private static final String ARG_SITE = "SITE";
    private String siteId;
    private DonorAdapter donorAdapter;
    private final List<Donor> donorList = new ArrayList<>();

    public static DonorListFragment newInstance(String siteId) {
        DonorListFragment fragment = new DonorListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SITE, siteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.donor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        donorAdapter = new DonorAdapter(donorList);
        recyclerView.setAdapter(donorAdapter);

        if (getArguments() != null) {
            siteId = getArguments().getString(ARG_SITE);
        }

        fetchDonors();
        return view;
    }

    private void fetchDonors() {
        DatabaseManager db = new DatabaseManager();

        // Fetch donor IDs from the site document
        db.getRef("site").document(siteId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> donorIds = (List<String>) documentSnapshot.get("donors");
                        if (donorIds != null && !donorIds.isEmpty()) {
                            fetchDonorObjs(donorIds);
                        } else {
                            Toast.makeText(getContext(), "No donors found for this site", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch site data", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchDonorObjs(List<String> donorIds) {
        DatabaseManager db = new DatabaseManager();
        donorList.clear();

        for (String donorId : donorIds) {
            db.getRef("donor").document(donorId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Donor donor = documentSnapshot.toObject(Donor.class);
                            if (donor != null) {
                                donorList.add(donor);
                                donorAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to fetch donor: " + donorId, Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
