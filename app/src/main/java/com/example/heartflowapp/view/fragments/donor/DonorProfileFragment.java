package com.example.heartflowapp.view.fragments.donor;

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
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.view.activities.ManagerActivity;

public class DonorProfileFragment extends Fragment {

    private static final String ARG_USER = "USER";

    private String userId;
    private TextView fullName;
    private TextView gender;
    private TextView phone;
    private TextView dob;
    private TextView type;

    public DonorProfileFragment() {
        // Required empty public constructor
    }

    public static DonorProfileFragment newInstance(String userId) {
        DonorProfileFragment fragment = new DonorProfileFragment();
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
        fullName = view.findViewById(R.id.full_name);
        gender = view.findViewById(R.id.gender);
        phone = view.findViewById(R.id.phone);
        dob = view.findViewById(R.id.dob);
        type = view.findViewById(R.id.bloodtype);

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
        db.get("donor", userId, Donor.class, new DatabaseManager.FetchCallBack<>() {
            @Override
            public void onSuccess(Donor user) {
                if (user != null) {
                    // Populate UI with user details
                    fullName.setText(user.getFullName());
                    gender.setText(user.getGender());
                    phone.setText(user.getPhone());
                    dob.setText(user.getDob());
                    type.setText(user.getType().toString());
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

