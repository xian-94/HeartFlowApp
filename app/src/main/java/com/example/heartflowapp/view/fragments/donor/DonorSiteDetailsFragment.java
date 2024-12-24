package com.example.heartflowapp.view.fragments.donor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.model.Site;

import java.util.Map;

public class DonorSiteDetailsFragment extends Fragment {

    private static final String ARG_SITE = "SITE";
    private static final String ARG_USER = "USER";
    private Site site;
    private String user;

    public static DonorSiteDetailsFragment newInstance(Site site, String user) {
        DonorSiteDetailsFragment fragment = new DonorSiteDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SITE, site);
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_site_details, container, false);

        TextView siteName = view.findViewById(R.id.site_name);
        TextView siteAddress = view.findViewById(R.id.site_address);
        TextView donationDate = view.findViewById(R.id.site_date);
        Button registerButton = view.findViewById(R.id.register_button);
        ImageButton closeBtn = view.findViewById(R.id.close_button);
        Button volunteerBtn = view.findViewById(R.id.volunteer_button);
        LinearLayout bloodTypesContainer = view.findViewById(R.id.blood_types_container);

        if (getArguments() != null) {
            site = (Site) getArguments().getSerializable(ARG_SITE);
            user = getArguments().getString(ARG_USER);
            siteName.setText(site.getName());
            siteAddress.setText(site.getAddress());
            donationDate.setText(site.getDate());
            populateBloodTypes(bloodTypesContainer, site.getRequiredBloodTypes());

        }

        registerButton.setOnClickListener(v ->
                registerForDonation(registerButton)
        );

        closeBtn.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        volunteerBtn.setOnClickListener(v -> volunteer(volunteerBtn));

        return view;
    }

    private void registerForDonation(Button registerBtn) {
        DatabaseManager db = new DatabaseManager();
        ProgressManager.showProgress(getChildFragmentManager());
        db.addToArray("site", site.getSiteId(), "donors", user, new DatabaseManager.NormalCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireActivity(), "User added to donors successfully", Toast.LENGTH_SHORT).show();
                ProgressManager.dismissProgress();
                registerBtn.setBackgroundColor(Color.parseColor("#A1D6CB"));
                registerBtn.setTextColor(Color.parseColor("507687"));
                registerBtn.setClickable(false);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(requireActivity(), "Failed to add user: " + message, Toast.LENGTH_SHORT).show();
                ProgressManager.dismissProgress();
            }
        });

    }

    private void populateBloodTypes(LinearLayout container, Map<String, Double> requiredBloodTypes) {
        for (String bloodType : requiredBloodTypes.keySet()) {
            TextView bloodTypeBox = new TextView(getContext());

            // Set text and style for each blood type
            bloodTypeBox.setText(bloodType);
            bloodTypeBox.setPadding(16, 8, 16, 8);
            bloodTypeBox.setTextSize(14);
            bloodTypeBox.setTextColor(Color.parseColor("#FCAEAE"));
            bloodTypeBox.setGravity(Gravity.CENTER);

            // Set rounded background
            bloodTypeBox.setBackgroundResource(R.drawable.box_normal);

            // Add margin between boxes
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            bloodTypeBox.setLayoutParams(params);

            // Add blood type box to container
            container.addView(bloodTypeBox);
        }
    }

    private void volunteer(Button volunteerBtn) {
        DatabaseManager db = new DatabaseManager();
        ProgressManager.showProgress(getChildFragmentManager());
        db.addToArray("site", site.getSiteId(), "volunteers", user, new DatabaseManager.NormalCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireActivity(), "User added to volunteers successfully", Toast.LENGTH_SHORT).show();
                ProgressManager.dismissProgress();
                // Disable the button after clicked
                volunteerBtn.setClickable(false);
                volunteerBtn.setBackgroundColor(Color.parseColor("#A1D6CB"));
                volunteerBtn.setTextColor(Color.parseColor("#507687"));
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(requireActivity(), "Failed to add user: " + message, Toast.LENGTH_SHORT).show();
                ProgressManager.dismissProgress();
            }
        });
    }
}
