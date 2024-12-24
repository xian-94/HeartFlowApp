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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DonorListFragment extends Fragment {

    private static final String ARG_SITE = "SITE";
    private String siteId;
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

        DonorAdapter donorAdapter = new DonorAdapter(donorList);
        recyclerView.setAdapter(donorAdapter);

        if (getArguments() != null) {
            siteId = getArguments().getString(ARG_SITE);
        }

        fetchDonors();
        return view;
    }

    private void fetchDonors() {
        DatabaseManager db = new DatabaseManager();
        db.getRef("site").document(siteId).collection("donors")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    donorList.clear();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Donor donor = document.toObject(Donor.class);
                        donorList.add(donor);
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch donors", Toast.LENGTH_SHORT).show();
                });
    }
}
