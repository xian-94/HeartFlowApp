package com.example.heartflowapp.view.fragments.manager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.example.heartflowapp.R;


public class SiteFragment extends Fragment {

    private static final String ARG_USER = "USER";
    private static final String ARG_SITE = "SITE";

    private String user;
    private String site;

    public SiteFragment() {
        // Required empty public constructor
    }

    public static SiteFragment newInstance(String user, String site) {
        SiteFragment fragment = new SiteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        args.putString(ARG_SITE, site);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString(ARG_USER);
            site = getArguments().getString(ARG_SITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site, container, false);
        LinearLayout dashboard = view.findViewById(R.id.site_dashboard);
        RelativeLayout noSite = view.findViewById(R.id.no_site);
        Button toSiteForm = view.findViewById(R.id.to_site_form);


        if (site != null) {
            dashboard.setVisibility(View.VISIBLE);
            noSite.setVisibility(View.INVISIBLE);
            Log.d("SiteFragment", "Site ID: " + site);
        } else {
            dashboard.setVisibility(View.INVISIBLE);
            noSite.setVisibility(View.VISIBLE);
        }
        toSiteForm.setOnClickListener(v -> {
            showSiteForm();
        });

        return view;
    }


    private void showSiteForm() {
        Fragment siteFormFragment = new SiteFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER", String.valueOf(user));
        siteFormFragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.manager_container, siteFormFragment)
                .commit();
    }
}