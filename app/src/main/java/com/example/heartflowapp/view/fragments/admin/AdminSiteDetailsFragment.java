package com.example.heartflowapp.view.fragments.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.adapters.SiteDetailsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Map;

public class AdminSiteDetailsFragment extends Fragment {

    private static final String ARG_SITE = "SITE";
    private static final String ARG_USER = "USER";
    private Site site;
    private String user;

    public static AdminSiteDetailsFragment newInstance(Site site, String user) {
        AdminSiteDetailsFragment fragment = new AdminSiteDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SITE, site);
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_site_details, container, false);

        TextView siteName = view.findViewById(R.id.site_name);
        TextView siteAddress = view.findViewById(R.id.site_address);
        TextView donationDate = view.findViewById(R.id.site_date);
        ImageButton closeBtn = view.findViewById(R.id.close_button);
        LinearLayout bloodTypesContainer = view.findViewById(R.id.blood_types_container);
        TextView siteManager = view.findViewById(R.id.site_manager);
        TabLayout tabLayout = view.findViewById(R.id.site_details_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.site_details_view_pager);

        if (getArguments() != null) {
            site = (Site) getArguments().getSerializable(ARG_SITE);
            user = getArguments().getString(ARG_USER);
            siteName.setText(site.getName());
            siteAddress.setText(site.getAddress());
            donationDate.setText(site.getDate());
            getUser(siteManager);
            populateBloodTypes(bloodTypesContainer, site.getRequiredBloodTypes());

        }
        SiteDetailsPagerAdapter adapter = new SiteDetailsPagerAdapter(this, site);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Donors");
            else tab.setText("Volunteers");
        }).attach();


        closeBtn.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

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

    private void getUser(TextView siteManager) {
        DatabaseManager db = new DatabaseManager();
        db.getRef("users").document(user).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        SiteManager fetchedUser = task.getResult().toObject(SiteManager.class);
                        if (fetchedUser != null) {
                            siteManager.setText(fetchedUser.getFullName());
                        }
                    }
                })
                .addOnFailureListener(e ->
                    siteManager.setText("Error loading manager"));
    }

}
