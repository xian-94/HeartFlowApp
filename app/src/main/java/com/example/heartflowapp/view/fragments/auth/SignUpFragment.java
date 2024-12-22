package com.example.heartflowapp.view.fragments.auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.utils.Utils;
import com.example.heartflowapp.controller.AuthListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SignUpFragment extends Fragment {
    private final String[] roles = {"Donor", "Site Manager"};
    private AuthListener signupListener;
    private MaterialTextView errorView;
    private List<String> errorMessages;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthListener) {
            signupListener = (AuthListener) context;
        } else {
            throw new RuntimeException(context + "must implement the listener");
        }
    }


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // User attribute
        final String[] role = {null};
        errorMessages = new ArrayList<>();

        // Placeholder
        TextInputEditText eEmail = view.findViewById(R.id.eEmail);
        TextInputEditText ePassword = view.findViewById(R.id.ePassword);
        Button signUpBtn = view.findViewById(R.id.signup);
        errorView = view.findViewById(R.id.error_message);

        // Display role dropdown menu
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.auto_complete_txt);
        ArrayAdapter<String> rolesAdapter = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, roles);
        autoCompleteTextView.setAdapter(rolesAdapter);
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            role[0] = item;
            Toast.makeText(requireActivity(), "Item: " + item, Toast.LENGTH_SHORT).show();
        });

        // Sign up process
        signUpBtn.setOnClickListener(v -> {
            ProgressManager.showProgress(getParentFragmentManager());
            errorMessages.clear();
            String email = Objects.requireNonNull(eEmail.getText()).toString();
            String password = Objects.requireNonNull(ePassword.getText()).toString();

            validateInput(role[0], email, password);
            if (errorMessages.isEmpty()) {
                handleSignUp(role[0], email, password);
            } else {
                StringBuilder errorText = new StringBuilder();
                for (String m : errorMessages) {
                    errorText.append(m).append("\n");
                }
                errorView.setText(errorText.toString().trim());
            }

        });

        return view;
    }

    /**
     * Validate the input values
     *
     * @param role     user role
     * @param email    email
     * @param password password
     */
    private void validateInput(String role, String email, String password) {
        if (role == null) {
            this.errorMessages.add("Please select a role");
        }
        if (email.isEmpty() || password.isEmpty()) {
            this.errorMessages.add("Please enter all fields");
        }
        if (!Utils.isValidEmail(email)) {
            this.errorMessages.add("Please enter a valid email");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        signupListener = null;
    }

    private void handleSignUp(String role, String email, String password) {
        if (signupListener != null) {
            signupListener.onSignUp(role, email, password);
        }
    }

}
