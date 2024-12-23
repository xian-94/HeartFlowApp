package com.example.heartflowapp.view.fragments.manager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heartflowapp.R;
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.view.adapters.DonorAdapter;

import java.util.ArrayList;
import java.util.List;

public class DonorListFragment extends Fragment {

    private static final String ARG_EVENT = "EVENT";


    private String event;

    public DonorListFragment() {
        // Required empty public constructor
    }

    public static DonorListFragment newInstance(String event) {
        DonorListFragment fragment = new DonorListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = getArguments().getString(ARG_EVENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.donor_recycler_view);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DonorAdapter adapter = new DonorAdapter(getDonorList(event)); // Implement getDonorList to fetch data
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Donor> getDonorList(String eventId) {
        // Fetch donors for the event from Firestore or mock data
        return new ArrayList<>();
    }
}