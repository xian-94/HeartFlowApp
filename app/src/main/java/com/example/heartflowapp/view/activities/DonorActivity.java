package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heartflowapp.MainActivity;
import com.example.heartflowapp.R;
import com.example.heartflowapp.databinding.ActivityDonorBinding;
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.view.fragments.donor.DonorDashboardFragment;
import com.example.heartflowapp.view.fragments.donor.DonorMapFragment;
import com.example.heartflowapp.view.fragments.donor.DonorProfileFragment;
import com.google.firebase.auth.FirebaseAuth;

public class DonorActivity extends AppCompatActivity {
    ActivityDonorBinding binding;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDonorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.donor_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        Donor currentUser = (Donor) intent.getSerializableExtra("USER");
        if (currentUser != null) {
            currentUserId = currentUser.getUserId();
        }
        binding.donorBottomNavigation.setOnItemSelectedListener(item -> {
            Bundle bundle = new Bundle();
            bundle.putString("USER", currentUserId);
            if (item.getItemId() == R.id.home) {
                DonorDashboardFragment dashboard = new DonorDashboardFragment();
                dashboard.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.donor_container, dashboard)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.donor_map) {
                DonorMapFragment map = new DonorMapFragment();
                map.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.donor_container, map)
                        .commit();
                return true;
            } else {
                DonorProfileFragment profile = new DonorProfileFragment();
                profile.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.donor_container, profile)
                        .commit();
                return true;
            }
        });
        binding.donorBottomNavigation.setSelectedItemId(R.id.home);

    }

    public void logout() {
        // Navigate back to main
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DonorActivity.this, MainActivity.class);
        // Clear back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}