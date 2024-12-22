package com.example.heartflowapp.view.fragments.manager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;

import java.util.Objects;


public class SiteFragment extends Fragment {

    private static final String ARG_USER = "USER";

    private String user;
    private LinearLayout dashboard;
    private RelativeLayout noSite;

    public SiteFragment() {
        // Required empty public constructor
    }

    public static SiteFragment newInstance(String user) {
        SiteFragment fragment = new SiteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site, container, false);
        dashboard = view.findViewById(R.id.site_dashboard);
        noSite = view.findViewById(R.id.no_site);
        Button toSiteForm = view.findViewById(R.id.to_site_form);

        DatabaseManager db = new DatabaseManager();
        checkManagerSite(db);
        toSiteForm.setOnClickListener(v -> {
            showSiteForm();
        });

        return view;
    }

    private void checkManagerSite(DatabaseManager db) {
        final boolean[] haveSite = {false};
        // Query the manager id in the site collection
        db.getRef("site")
                .whereEqualTo("createdBy", user)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().getDocuments().isEmpty()) {
                            haveSite[0] = true;
                        }
                    } else {
                        haveSite[0] = false;
                    }
                })
                .addOnFailureListener(e -> {
                    haveSite[0] = false;
                });

        // Toggle visibility
        if (haveSite[0]) {
            dashboard.setVisibility(View.VISIBLE);
            noSite.setVisibility(View.GONE);
        } else {
            dashboard.setVisibility(View.GONE);
            noSite.setVisibility(View.VISIBLE);
        }
    }

    private void showSiteForm() {
        Fragment siteFormFragment = new SiteFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER", String.valueOf(user));
        siteFormFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.no_site, siteFormFragment)
                .commit();
    }
}