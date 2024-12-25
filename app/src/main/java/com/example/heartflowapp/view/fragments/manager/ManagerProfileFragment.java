package com.example.heartflowapp.view.fragments.manager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.activities.ManagerActivity;

public class ManagerProfileFragment extends Fragment {

    private static final String ARG_USER = "USER";

    private String userId;
    private TextView fullNameTextView;
    private TextView genderTextView;
    private TextView phoneTextView;

    public ManagerProfileFragment() {
        // Required empty public constructor
    }

    public static ManagerProfileFragment newInstance(String userId) {
        ManagerProfileFragment fragment = new ManagerProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_manager_profile, container, false);

        // Bind views
        fullNameTextView = view.findViewById(R.id.full_name);
        genderTextView = view.findViewById(R.id.gender);
        phoneTextView = view.findViewById(R.id.phone);

        // Fetch user data
        fetchUserData();

        // Handle Logout button
        Button logOutButton = view.findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(v -> handleLogout());

        return view;
    }

    private void fetchUserData() {
        if (userId == null) return;

        DatabaseManager db = new DatabaseManager();
        db.get("manager", userId, SiteManager.class, new DatabaseManager.FetchCallBack<>() {
            @Override
            public void onSuccess(SiteManager user) {
                if (user != null) {
                    // Populate UI with user details
                    fullNameTextView.setText(user.getFullName());
                    genderTextView.setText(user.getGender());
                    phoneTextView.setText(user.getPhone());
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "Failed to fetch user: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLogout() {
        if (getActivity() instanceof ManagerActivity) {
            ((ManagerActivity) getActivity()).logout();
        } else {
            Toast.makeText(getContext(), "Unable to logout", Toast.LENGTH_SHORT).show();
        }
    }

}

