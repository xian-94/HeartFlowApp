package com.example.heartflowapp.view.fragments.auth;

import android.content.Intent;
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
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.activities.ManagerActivity;

public class ManagerProfileSettingFragment extends Fragment {

    private static final String USER_ID = "USER_ID";
    private static final String ROLE = "ROLE";

    private EditText eFullName;
    private TextView selectedGender;
    private String userId;
    private String role;
    private EditText ePhone;

    public ManagerProfileSettingFragment() {
        // Required empty public constructor
    }

    public static ManagerProfileSettingFragment newInstance(String param1, String param2) {
        ManagerProfileSettingFragment fragment = new ManagerProfileSettingFragment();
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

        View view = inflater.inflate(R.layout.fragment_manager_profile_setting, container, false);
        eFullName = view.findViewById(R.id.etFullName);
        ePhone = view.findViewById(R.id.ePhone);


        TextView maleBtn = view.findViewById(R.id.male);
        TextView femaleBtn = view.findViewById(R.id.female);
        maleBtn.setOnClickListener(v -> selectGender(maleBtn));
        femaleBtn.setOnClickListener(v -> selectGender(femaleBtn));

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

    private void save() {
        String fullName = eFullName.getText().toString().trim();
        String gender = selectedGender != null ? selectedGender.getText().toString() : "";
        String phone = ePhone.getText().toString().trim();

        if (fullName.isEmpty() || gender.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null || role == null) {
            return;
        }

        DatabaseManager db = new DatabaseManager();
        if (role.equalsIgnoreCase("manager")) {
            db.get("manager", userId, SiteManager.class, new DatabaseManager.FetchCallBack<>() {
                @Override
                public void onSuccess(SiteManager manager) {
                    manager.setFullName(fullName);
                    manager.setGender(gender);
                    manager.setPhone(phone);
                    saveUser(manager, userId);
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getContext(), "Failed to fetch user: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private <T> void saveUser(T user, String userId) {
        DatabaseManager db = new DatabaseManager();
        db.update("manager", userId, user, new DatabaseManager.NormalCallBack() {
            @Override
            public void onSuccess() {
                // Navigate to manager screen
                Intent intent = new Intent(getActivity(), ManagerActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "Failed to update profile: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}