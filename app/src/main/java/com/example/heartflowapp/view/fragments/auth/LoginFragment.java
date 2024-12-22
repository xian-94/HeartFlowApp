package com.example.heartflowapp.view.fragments.auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.AuthListener;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment {

    private AuthListener loginListener;
    private MaterialTextView errorView;
    private List<String> errorMessages;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthListener) {
            loginListener = (AuthListener) context;
        } else {
            throw new RuntimeException(context + "must implement the listener");
        }
    }

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        errorMessages = new ArrayList<>();
        // Placeholder
        TextInputEditText eEmail = view.findViewById(R.id.eEmail_login);
        TextInputEditText ePassword = view.findViewById(R.id.ePassword_login);
        Button loginBtn = view.findViewById(R.id.login);
        errorView = view.findViewById(R.id.error_message_login);

        loginBtn.setOnClickListener(v -> {
            ProgressManager.showProgress(getParentFragmentManager());
            errorMessages.clear();
            String email = Objects.requireNonNull(eEmail.getText()).toString();
            String password = Objects.requireNonNull(ePassword.getText()).toString();

            validateInput(email, password);
            if (errorMessages.isEmpty()) {
                handleSignIn(email, password);
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
     * @param email    email
     * @param password password
     */
    private void validateInput(String email, String password) {
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
        loginListener = null;
    }

    private void handleSignIn(String email, String password) {
        if (loginListener != null) {
            loginListener.onLogin(email, password);
        }
    }
}