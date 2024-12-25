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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SiteFormFragment extends Fragment {

    private static final String ARG_ADDRESS = "ADDRESS";
    private static final String ARG_LAT = "LAT";
    private static final String ARG_LONG = "LONG";
    private SiteListener siteListener;

    private double latitude, longitude;
    private String address;
    private List<String> selectedBloodTypes;

    public interface SiteListener {
        void onSaveSite(String name, String address, double lat, double lng, String date, List<String> bloodTypes);
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

    public static SiteFormFragment newInstance(double lat, double lng, String address) {
        SiteFormFragment fragment = new SiteFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ADDRESS, address);
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LONG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble(ARG_LAT);
            longitude = getArguments().getDouble(ARG_LONG);
            address = getArguments().getString(ARG_ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_site_form, container, false);
        TextView locationDisplay = view.findViewById(R.id.site_address);
        Button createBtn = view.findViewById(R.id.create_site);
        Button cancelBtn = view.findViewById(R.id.cancel_site);
        EditText eSiteName = view.findViewById(R.id.site_name);
        MaterialTextView donationDate = view.findViewById(R.id.donation_date);
        locationDisplay.setText(address);

        // Get required blood types

        selectedBloodTypes = new ArrayList<>();

        // Blood types button
        List<TextView> bloodTypeBtns = new ArrayList<>();
        TextView aPlus = view.findViewById(R.id.a_plus);
        bloodTypeBtns.add(aPlus);
        TextView bPlus = view.findViewById(R.id.b_plus);
        bloodTypeBtns.add(bPlus);
        TextView oPlus = view.findViewById(R.id.o_plus);
        bloodTypeBtns.add(oPlus);
        TextView aBPlus = view.findViewById(R.id.ab_plus);
        bloodTypeBtns.add(aBPlus);
        TextView aMinus = view.findViewById(R.id.a_minus);
        bloodTypeBtns.add(aMinus);
        TextView bMinus = view.findViewById(R.id.b_minus);
        bloodTypeBtns.add(bMinus);
        TextView oMinus = view.findViewById(R.id.o_minus);
        bloodTypeBtns.add(oMinus);
        TextView aBMinus = view.findViewById(R.id.ab_minus);
        bloodTypeBtns.add(aBMinus);

        // Set click listeners for each button
        for (TextView btn : bloodTypeBtns) {
            btn.setOnClickListener(v -> toggleBloodTypeSelection(btn));
        }


        cancelBtn.setOnClickListener(v ->
            requireActivity().getSupportFragmentManager().popBackStack());

        donationDate.setOnClickListener(v -> pickDate(donationDate));

        // Get site data
        createBtn.setOnClickListener(v -> {
            String name = eSiteName.getText().toString();
            String date = donationDate.getText().toString();


            if (name.isEmpty() || date.isEmpty() || selectedBloodTypes.isEmpty()) {
                Toast.makeText(getContext(), "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                siteListener.onSaveSite(name, address, latitude, longitude, date, selectedBloodTypes);
            }
        });

        return view;
    }

    private void toggleBloodTypeSelection(TextView btn) {
        String bloodType = btn.getText().toString();

        if (selectedBloodTypes.contains(bloodType)) {
            // Deselect blood type
            selectedBloodTypes.remove(bloodType);
            btn.setSelected(false);
        } else {
            // Select blood type
            selectedBloodTypes.add(bloodType);
            btn.setSelected(true);
        }
    }

    private void pickDate(MaterialTextView donationDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Donation Date")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            donationDate.setText(sdf.format(new Date(selection)));
        });

        datePicker.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }


}