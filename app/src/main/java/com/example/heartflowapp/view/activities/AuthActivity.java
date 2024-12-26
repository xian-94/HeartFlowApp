package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.heartflowapp.R;
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.model.User;
import com.example.heartflowapp.model.UserRole;
import com.example.heartflowapp.view.adapters.AuthPagerAdapter;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.controller.AuthListener;
import com.example.heartflowapp.view.fragments.auth.DonorProfileSettingFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
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
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.reload();
            db = new DatabaseManager();
            // Get the user's role
            db.getOneField("user", user.getUid(), new DatabaseManager.FetchCallBack<>() {
                @Override
                public void onSuccess(Map<String, Object> data) {
                    String role = (String) data.get("role");

                    if (role == null) {
                        Toast.makeText(AuthActivity.this, "Role not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Fetch the correct object based on the role
                    navigateToHome(role, user.getUid());
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(AuthActivity.this, "Failed to fetch user role: " + message, Toast.LENGTH_SHORT).show();
                }
            });
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
        TextView errorView = findViewById(R.id.error_message);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Save user document
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            User newUser;
                            if (role.equalsIgnoreCase(String.valueOf(UserRole.DONOR))) {
                                newUser = new Donor(user.getUid(), email, password);
                                // Save to both user and donor collection
                                handleSave("donor", user.getUid(), newUser);
                                handleSave("user", user.getUid(), newUser);
                                navigateToProfileSetup(role, user.getUid());
                            } else {
                                newUser = new SiteManager(user.getUid(), email, password);
                                handleSave("manager", user.getUid(), newUser);
                                handleSave("user", user.getUid(), newUser);
                            }
                        }
                        Toast.makeText(AuthActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        displayError(errorView, String.valueOf(task.getException()));
                    }
                    ProgressManager.dismissProgress();
                });
    }

    // Display error message
    private void displayError(TextView errorView, String errorMessage) {
        errorView.clearComposingText();
        errorView.setText(errorMessage);
    }


    @Override
    public void onLogin(String email, String password) {
        TextView errorView = findViewById(R.id.error_message_login);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Toast.makeText(AuthActivity.this, "Sign in successfully", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            db = new DatabaseManager();
                            // Get the user's role
                            db.getOneField("user", user.getUid(), new DatabaseManager.FetchCallBack<>() {

                                @Override
                                public void onSuccess(Map<String, Object> data) {
                                    String role = (String) data.get("role");

                                    if (role == null) {
                                        Toast.makeText(AuthActivity.this, "Role not found!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    // Fetch the correct object based on the role
                                    navigateToHome(role, user.getUid());
                                }

                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(AuthActivity.this, "User " + user.getUid() , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        displayError(errorView, String.valueOf(task.getException()));
                    }
                    ProgressManager.dismissProgress();
                });

    }

    // Save user to database
    private void handleSave(String collection, String uid, User newUser) {
        db.update(collection, uid, newUser, new DatabaseManager.NormalCallBack() {
            @Override
            public void onSuccess() {
                Toast.makeText(AuthActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Log.e("Error", "Failed to save");
            }
        });
    }

    // Navigate to setup profile after signing up
    private void navigateToProfileSetup(String role, String userId) {
        Fragment fm = null;
        if ("donor".equalsIgnoreCase(role)) {
            fm = new DonorProfileSettingFragment();
        }

        Bundle args = new Bundle();
        args.putString("USER_ID", userId);
        args.putString("ROLE", role);
        if (fm != null) {
            fm.setArguments(args);
            showDetails();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_setup_container, fm)
                    .commit();
        }
    }

    private void showDetails() {
        RelativeLayout authSection = findViewById(R.id.auth_section);
        LinearLayout detailsSection = findViewById(R.id.details_section);
        authSection.setVisibility(View.GONE);
        detailsSection.setVisibility(View.VISIBLE);
    }

    /**
     * Navigate user to home screen after logging in
     *
     * @param role user role
     * @param uid  user id
     */
    private void navigateToHome(String role, String uid) {
        if (role.equalsIgnoreCase("donor")) {
            db.get("user", uid, Donor.class, new DatabaseManager.FetchCallBack<>() {
                @Override
                public void onSuccess(Donor donor) {
                    Intent intent = new Intent(AuthActivity.this, DonorActivity.class);
                    intent.putExtra("USER", donor);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(AuthActivity.this, "Failed to fetch donor: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (role.equalsIgnoreCase("manager")) {
            db.get("user", uid, SiteManager.class, new DatabaseManager.FetchCallBack<>() {
                @Override
                public void onSuccess(SiteManager manager) {
                    Intent intent = new Intent(AuthActivity.this, ManagerActivity.class);
                    intent.putExtra("USER", manager);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(AuthActivity.this, "Failed to fetch manager: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (role.equalsIgnoreCase("admin")){
            db.get("user", uid, User.class, new DatabaseManager.FetchCallBack<>() {
                @Override
                public void onSuccess(User admin) {
                    Intent intent = new Intent(AuthActivity.this, SuperUserActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(AuthActivity.this, "Failed to fetch admin: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}