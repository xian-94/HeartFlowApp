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
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.view.ui.MapsFragment;


public class SiteFormFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "USER";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private MapsFragment mapsFragment;
    private TextView locationDisplay;
    private String userId;

    public SiteFormFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static SiteFormFragment newInstance(String userId, String param2) {
        SiteFormFragment fragment = new SiteFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, userId);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site_form, container, false);
        locationDisplay = view.findViewById(R.id.address_display);
        Button createBtn = view.findViewById(R.id.create_site);
        mapsFragment = new MapsFragment();
        mapsFragment.setLocationDisplay(locationDisplay);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapsFragment).commit();

        // Get site data
        createBtn.setOnClickListener(v -> {
            double lat = mapsFragment.getSelectedLat();
            double lng = mapsFragment.getSelectedLong();
            String address = locationDisplay.getText().toString();
            String createdBy = userId;


            if (lat == 0 || lng == 0 || address.equalsIgnoreCase("Unknown address")) {
                Toast.makeText(getContext(), "Please select a valid location", Toast.LENGTH_SHORT).show();
            } else {
                saveSite(address, lat, lng, createdBy);
            }
        });

        return view;
    }

    private void saveSite(String address, double lat, double lng, String userId) {
        DatabaseManager db = new DatabaseManager();
        db.add("site", Site.class, new DatabaseManager.NormalCallBack() {

            @Override
            public void onSuccess() {
                Toast.makeText(requireActivity(), "Save site successfully", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
                requireActivity().findViewById(R.id.no_site).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(requireActivity(), "Failed to save site", Toast.LENGTH_SHORT).show();
            }
        });
    }


}