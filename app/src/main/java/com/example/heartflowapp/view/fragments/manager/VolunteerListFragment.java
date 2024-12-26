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
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.model.User;
import com.example.heartflowapp.view.adapters.VolunteerAdapter;

import java.util.ArrayList;
import java.util.List;

public class VolunteerListFragment extends Fragment {

    private static final String ARG_SITE = "SITE";
    private String siteId;
    private VolunteerAdapter volunteerAdapter;
    private final List<User> volunteerList = new ArrayList<>();

    public static VolunteerListFragment newInstance(String siteId) {
        VolunteerListFragment fragment = new VolunteerListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SITE, siteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.volunteer_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        volunteerAdapter = new VolunteerAdapter(volunteerList);
        recyclerView.setAdapter(volunteerAdapter);

        if (getArguments() != null) {
            siteId = getArguments().getString(ARG_SITE);
        }

        fetchVolunteers();

        return view;
    }

    private void fetchVolunteers() {
        DatabaseManager db = new DatabaseManager();

        // Fetch donor IDs from the site document
        db.getRef("site").document(siteId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> volunteerIds = (List<String>) documentSnapshot.get("volunteers");
                        if (volunteerIds != null && !volunteerIds.isEmpty()) {
                            fetchDonorObjs(volunteerIds);
                        } else {
                            Toast.makeText(getContext(), "No volunteers found for this site", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch site data", Toast.LENGTH_SHORT).show());
    }

    private void fetchDonorObjs(List<String> volunteersIds) {
        DatabaseManager db = new DatabaseManager();
        volunteerList.clear();

        for (String id : volunteersIds) {
            db.getRef("user").document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            if (documentSnapshot.get("role").toString().equalsIgnoreCase("donor")) {
                                Donor donor = documentSnapshot.toObject(Donor.class);
                                if (donor != null) {
                                    volunteerList.add(donor);
                                    volunteerAdapter.notifyDataSetChanged();
                                }
                            } else {
                                SiteManager manager = documentSnapshot.toObject(SiteManager.class);
                                if (manager != null) {
                                    volunteerList.add(manager);
                                    volunteerAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to fetch donor: " + id, Toast.LENGTH_SHORT).show());
        }


    }
}
