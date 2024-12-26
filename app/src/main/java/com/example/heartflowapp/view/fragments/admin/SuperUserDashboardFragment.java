package com.example.heartflowapp.view.fragments.admin;

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
import com.example.heartflowapp.view.activities.DonorActivity;
import com.example.heartflowapp.view.activities.SuperUserActivity;


public class SuperUserDashboardFragment extends Fragment {
    private TextView totalDonors, totalManagers, totalEvents, totalSuccessful;

    public SuperUserDashboardFragment() {
        // Required empty public constructor
    }

    public static SuperUserDashboardFragment newInstance() {
        return new SuperUserDashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_super_user_dashboard, container, false);
        totalDonors = view.findViewById(R.id.total_donors);
        totalManagers = view.findViewById(R.id.total_managers);
        totalEvents = view.findViewById(R.id.total_events);
        totalSuccessful = view.findViewById(R.id.total_successful);
        Button logOut = view.findViewById(R.id.log_out_button);
        logOut.setOnClickListener(v -> handleLogout());

        fetchData();

        return view;
    }

    private void fetchData() {
        DatabaseManager db = new DatabaseManager();

        // Fetch total donors
        db.getRef("user")
                .whereEqualTo("role", "DONOR")
                .get()
                .addOnSuccessListener(querySnapshot ->
                        totalDonors.setText(String.valueOf(querySnapshot.size())))
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Cannot fetch donors", Toast.LENGTH_SHORT).show());

        // Fetch total managers
        db.getRef("user")
                .whereEqualTo("role", "MANAGER")
                .get()
                .addOnSuccessListener(querySnapshot -> totalManagers.setText(String.valueOf(querySnapshot.size())))
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Cannot fetch managers", Toast.LENGTH_SHORT).show());

        // Fetch total sites
        db.getRef("site")
                .get()
                .addOnSuccessListener(querySnapshot -> totalEvents.setText(String.valueOf(querySnapshot.size())))
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Cannot fetch sites", Toast.LENGTH_SHORT).show());

        // Fetch completed sites
        db.getRef("site")
                .whereEqualTo("status", "COMPLETE")
                .get()
                .addOnSuccessListener(querySnapshot -> totalSuccessful.setText(String.valueOf(querySnapshot.size())))
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Cannot fetch completed sites", Toast.LENGTH_SHORT).show());
    }

    private void handleLogout() {
        if (getActivity() instanceof SuperUserActivity) {
            ((SuperUserActivity) getActivity()).logout();
        } else {
            Toast.makeText(getContext(), "Unable to logout", Toast.LENGTH_SHORT).show();
        }
    }


}