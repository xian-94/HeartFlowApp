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
import com.example.heartflowapp.databinding.ActivitySuperUserBinding;
import com.example.heartflowapp.view.fragments.admin.AdminMapFragment;
import com.example.heartflowapp.view.fragments.admin.SuperUserDashboardFragment;
import com.google.firebase.auth.FirebaseAuth;

public class SuperUserActivity extends AppCompatActivity {
    ActivitySuperUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySuperUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.superUserBottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dashboard) {
                SuperUserDashboardFragment dashboard = new SuperUserDashboardFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_container, dashboard)
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.admin_map) {
                AdminMapFragment map = new AdminMapFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_container, map)
                        .commit();
                return true;
            }
            return false;

        });
        binding.superUserBottomNavigation.setSelectedItemId(R.id.dashboard);

    }

    public void logout() {
        // Navigate back to main
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SuperUserActivity.this, MainActivity.class);
        // Clear back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}