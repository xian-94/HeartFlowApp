package com.example.heartflowapp.view.fragments.manager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.heartflowapp.R;
import com.example.heartflowapp.view.ui.MapsFragment;


public class SiteFormFragment extends Fragment {

    private static final String ARG_USER = "USER";

    private MapsFragment mapsFragment;
    private TextView locationDisplay;
    private String userId;
    private SiteListener siteListener;

    public interface SiteListener {
        void onSaveSite(String name, String address, double lat, double lng, String userId);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SiteListener) {
            siteListener = (SiteListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnSaveSiteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        siteListener = null;
    }

    public SiteFormFragment() {
        // Required empty public constructor
    }

    public static SiteFormFragment newInstance(String userId) {
        SiteFormFragment fragment = new SiteFormFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site_form, container, false);
        locationDisplay = view.findViewById(R.id.address_display);
        Button createBtn = view.findViewById(R.id.create_site);
        EditText eSiteName = view.findViewById(R.id.eSiteName);
        mapsFragment = new MapsFragment();
        mapsFragment.setLocationDisplay(locationDisplay);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapsFragment).commit();

        // Get site data
        createBtn.setOnClickListener(v -> {
            String name = eSiteName.getText().toString();
            double lat = mapsFragment.getSelectedLat();
            double lng = mapsFragment.getSelectedLong();
            String address = locationDisplay.getText().toString();
            String createdBy = userId;


            if (name.isEmpty() || lat == 0 || lng == 0 || address.equalsIgnoreCase("Unknown address")) {
                Toast.makeText(getContext(), "Please select a valid location", Toast.LENGTH_SHORT).show();
            } else {
                siteListener.onSaveSite(name, address, lat, lng, createdBy);
            }
        });

        return view;
    }






}