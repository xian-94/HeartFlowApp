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
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.model.User;
import com.example.heartflowapp.view.adapters.VolunteerAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;

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

        // Listen for changes in the volunteers subcollection
        db.getRef("site").document(siteId).collection("volunteers")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Failed to fetch volunteers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            String userId = change.getDocument().getId(); // Get user ID

                            switch (change.getType()) {
                                case ADDED:
                                    fetchVolunteerDetails(userId, "ADDED");
                                    break;
                                case MODIFIED:
                                    fetchVolunteerDetails(userId, "MODIFIED");
                                    break;
                                case REMOVED:
                                    int indexToRemove = findVolunteerIndex(userId);
                                    if (indexToRemove != -1) {
                                        volunteerList.remove(indexToRemove);
                                        volunteerAdapter.notifyItemRemoved(indexToRemove);
                                    }
                                    break;
                            }
                        }
                    }
                });
    }

    private void fetchVolunteerDetails(String userId, String changeType) {
        DatabaseManager db = new DatabaseManager();
        // Fetch the full volunteer details from the users collection
        db.getRef("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User volunteer = documentSnapshot.toObject(User.class);
                        if ("ADDED".equals(changeType)) {
                            volunteerList.add(volunteer);
                            volunteerAdapter.notifyItemInserted(volunteerList.size() - 1);
                        } else if ("MODIFIED".equals(changeType)) {
                            int indexToModify = findVolunteerIndex(userId);
                            if (indexToModify != -1) {
                                volunteerList.set(indexToModify, volunteer);
                                volunteerAdapter.notifyItemChanged(indexToModify);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch volunteer details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private int findVolunteerIndex(String userId) {
        for (int i = 0; i < volunteerList.size(); i++) {
            if (volunteerList.get(i).getUserId().equals(userId)) {
                return i;
            }
        }
        return -1;
    }


}
