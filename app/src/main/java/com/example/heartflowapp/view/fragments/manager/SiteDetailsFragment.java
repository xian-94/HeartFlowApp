package com.example.heartflowapp.view.fragments.manager;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.view.adapters.SiteDetailsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.Map;


public class SiteDetailsFragment extends Fragment {
    private Site site;
    private String user;
    private static final String ARG_SITE = "SITE";
    private static final String ARG_USER = "USER";


    public SiteDetailsFragment() {
        // Required empty public constructor
    }


    public static SiteDetailsFragment newInstance(Site site, String user) {
        SiteDetailsFragment fragment = new SiteDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SITE, site);
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            site = (Site) getArguments().getSerializable(ARG_SITE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_details, container, false);

        TextView siteName = view.findViewById(R.id.site_name);
        TextView siteAddress = view.findViewById(R.id.site_address);
        TabLayout tabLayout = view.findViewById(R.id.site_details_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.site_details_view_pager);
        LinearLayout bloodTypeContainer = view.findViewById(R.id.blood_types_container);
        ImageButton closeBtn = view.findViewById(R.id.close_button);
        Button volunteerBtn = view.findViewById(R.id.volunteer_button);
        Button endDriveBtn = view.findViewById(R.id.end_drive_button);
        TextView siteStatus = view.findViewById(R.id.site_status);

        if (getArguments() != null) {
            site = (Site) getArguments().getSerializable(ARG_SITE);
            user = getArguments().getString(ARG_USER);
            siteName.setText(site.getName());
            siteAddress.setText(site.getAddress());
            populateBloodTypes(bloodTypeContainer, site.getRequiredBloodTypes());
            siteStatus.setText(site.getStatus());
            if (site.getStatus().equalsIgnoreCase("COMPLETE")) {
                siteStatus.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.text_complete));
                siteStatus.setTextColor(Color.parseColor("#507687"));
            }

        }

        SiteDetailsPagerAdapter adapter = new SiteDetailsPagerAdapter(this, site);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Donors");
            else tab.setText("Volunteers");
        }).attach();

        closeBtn.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        volunteerBtn.setOnClickListener(v -> volunteer(volunteerBtn));
        endDriveBtn.setOnClickListener(v -> endDrive(endDriveBtn));


        return view;
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

    private void endDrive(Button saveBtn) {

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_end_drive, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        LinearLayout bloodTypeContainer = dialogView.findViewById(R.id.blood_type_input_container);
        Button submitButton = dialogView.findViewById(R.id.submit_button);

        populateBloodTypeInputs(bloodTypeContainer);

        // Handle submission
        submitButton.setOnClickListener(v -> {
            Map<String, Double> collectedData = getCollectedData(bloodTypeContainer);
            if (collectedData.isEmpty()) {
                Toast.makeText(getContext(), "Please enter valid data", Toast.LENGTH_SHORT).show();
                return;
            }
            updateSiteStatusAndData(collectedData, saveBtn);
            dialog.dismiss();
        });
        dialog.show();

    }

    private void populateBloodTypeInputs(LinearLayout container) {
        for (String bloodType : site.getRequiredBloodTypes().keySet()) {
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);

            TextView bloodTypeLabel = new TextView(requireContext());
            bloodTypeLabel.setText(bloodType);
            bloodTypeLabel.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            bloodTypeLabel.setTextSize(16);

            EditText amountInput = new EditText(requireContext());
            amountInput.setHint("Amount (ml)");
            amountInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            amountInput.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            amountInput.setTag(bloodType); // Tag to identify the blood type

            row.addView(bloodTypeLabel);
            row.addView(amountInput);

            container.addView(row);
        }
    }

    private Map<String, Double> getCollectedData(LinearLayout container) {
        Map<String, Double> collectedData = new HashMap<>();

        for (int i = 0; i < container.getChildCount(); i++) {
            View row = container.getChildAt(i);
            if (row instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) row;

                for (int j = 0; j < rowLayout.getChildCount(); j++) {
                    View child = rowLayout.getChildAt(j);
                    if (child instanceof EditText) {
                        EditText input = (EditText) child;
                        String bloodType = (String) input.getTag();
                        String value = input.getText().toString();
                        if (!value.isEmpty()) {
                            collectedData.put(bloodType, Double.parseDouble(value));
                        }
                    }
                }
            }
        }
        return collectedData;
    }

    private void updateSiteStatusAndData(Map<String, Double> collectedData, Button saveBtn) {
        DatabaseManager db = new DatabaseManager();
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "complete");
        updates.put("requiredBloodTypes", collectedData);

        db.getRef("site").document(site.getSiteId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Drive ended and data saved successfully!", Toast.LENGTH_SHORT).show();
                    site.setStatus("complete");
                    site.setRequiredBloodTypes(collectedData);
                    // Disable button
                    saveBtn.setClickable(false);
                    saveBtn.setBackgroundColor(Color.parseColor("#A1D6CB"));
                    saveBtn.setTextColor(Color.parseColor("#507687"));

                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update site: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


}