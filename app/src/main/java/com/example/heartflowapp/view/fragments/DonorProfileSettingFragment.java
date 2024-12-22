package com.example.heartflowapp.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.model.BloodType;
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.model.Gender;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.model.User;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DonorProfileSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DonorProfileSettingFragment extends Fragment {

    private static final String USER_ID = "USER_ID";
    private static final String ROLE = "ROLE";

    private EditText eFullName, eDOB;
    private TextView selectedBloodType;
    private TextView selectedGender;
    private String userId;
    private String role;

    public DonorProfileSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DonorProfileSettingFragment newInstance(String param1, String param2) {
        DonorProfileSettingFragment fragment = new DonorProfileSettingFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, param1);
        args.putString(ROLE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
            role = getArguments().getString(ROLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_setting_donor, container, false);
        eFullName = view.findViewById(R.id.etFullName);
        eDOB = view.findViewById(R.id.eDOB);
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

        for (TextView btn : bloodTypeBtns) {
            btn.setOnClickListener(v -> selectBloodType(btn));
        }

        TextView maleBtn = view.findViewById(R.id.male);
        TextView femaleBtn = view.findViewById(R.id.female);
        maleBtn.setOnClickListener(v -> selectGender(maleBtn));
        femaleBtn.setOnClickListener(v -> selectGender(femaleBtn));
        TextView datePick = view.findViewById(R.id.date_picker_btn);
        datePick.setOnClickListener(v -> pickDOB());

        Button saveBtn = view.findViewById(R.id.save_profile_btn);
        saveBtn.setOnClickListener(v -> save());

        return view;
    }

    private void selectGender(TextView selected) {
        if (selectedGender != null) {
            selectedGender.setSelected(false);
        }
        selected.setSelected(true);
        selectedGender = selected;
    }

    private void selectBloodType(TextView selected) {
        if (selectedBloodType != null) {
            selectedBloodType.setSelected(false);
        }

        selected.setSelected(true);
        selectedBloodType = selected;
    }

    private void pickDOB() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String date = formatter.format(new Date(selection));
            eDOB.setText(date);
        });

        datePicker.show(requireActivity().getSupportFragmentManager(), "tag");
    }

    private void save() {
        String fullName = eFullName.getText().toString().trim();
        String dob = eDOB.getText().toString().trim();
        String gender = selectedGender != null ? selectedGender.getText().toString() : "";
        String bloodType = selectedBloodType != null ? selectedBloodType.getText().toString() : "";

        if (fullName.isEmpty() || dob.isEmpty() || gender.isEmpty() || bloodType.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null || role == null) {
            return;
        }

        DatabaseManager db = new DatabaseManager();
        if (role.equalsIgnoreCase("donor")) {
            db.get("donor", userId, Donor.class, new DatabaseManager.FetchCallBack<>() {
                @Override
                public void onSuccess(Donor donor) {
                    donor.setFullName(fullName);
                    donor.setGender(Gender.getGender(gender));
                    donor.setDob(dob);
                    donor.setType(BloodType.getType(bloodType));
                    saveUser(donor, "donor", userId);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getContext(), "Failed to fetch user: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
        // TODO: Add else for SiteManager


    }

    private <T> void saveUser(T user, String collection, String userId) {
        DatabaseManager db = new DatabaseManager();
        db.add(collection, userId, user, new DatabaseManager.NormalCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
//                navigateToMainActivity(); // Redirect to main activity or next screen
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "Failed to update profile: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}