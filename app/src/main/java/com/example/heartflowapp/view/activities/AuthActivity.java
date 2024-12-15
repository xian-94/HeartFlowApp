package com.example.heartflowapp.view.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.heartflowapp.R;
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.model.User;
import com.example.heartflowapp.model.UserRole;
import com.example.heartflowapp.utils.AuthPagerAdapter;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.controller.AuthListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements AuthListener {

    // Render fragments
    private TabLayout authTabs;
    private ViewPager2 viewPager2;

    // Authentication
    private FirebaseAuth auth;
    private DatabaseManager db;

    // Check if the user is current signed in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // TODO: Go to main page
//            currentUser.reload();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instantiation
        auth = FirebaseAuth.getInstance();
        db = new DatabaseManager();

        // Set adapter to render auth form
        authTabs = findViewById(R.id.tabs);
        viewPager2 = findViewById(R.id.auth_container);
        AuthPagerAdapter authPagerAdapter = new AuthPagerAdapter(this);
        viewPager2.setAdapter(authPagerAdapter);
        authTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(authTabs.getTabAt(position)).select();
            }
        });


    }


    // Handle sign up process
    @Override
    public void onSignUp(String role, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Save user document
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                User newUser;
                                if (role.equalsIgnoreCase(String.valueOf(UserRole.DONOR))) {
                                    newUser = new Donor(user.getUid(), email, password);
                                    handleSave("donor", user.getUid(), newUser);
                                } else {
                                    newUser = new SiteManager(user.getUid(), email, password);
                                    handleSave("manager", user.getUid(), newUser);
                                }
                            }
                            // TODO: Navigate to next activity
                            Toast.makeText(AuthActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            displayError(String.valueOf(task.getException()));
                        }
                        ProgressManager.dismissProgress();
                    }
                });
    }

    // Display error message
    private void displayError(String errorMessage) {
        TextView errorView = findViewById(R.id.error_message);
        errorView.setText(errorMessage);
    }


    @Override
    public void onLogin(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(AuthActivity.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            // TODO: Navigate to main
                        } else {
                            // If sign in fails, display a message to the user.
                            displayError(String.valueOf(task.getException()));
                        }
                    }
                });

    }

    // Save user to database
    private void handleSave(String collection, String uid, User newUser) {
        db.add(collection, uid, newUser, new DatabaseManager.NormalCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(AuthActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                displayError(message);
            }
        });
    }


}