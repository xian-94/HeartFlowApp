package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heartflowapp.R;
import com.example.heartflowapp.databinding.ActivityManagerBinding;
import com.example.heartflowapp.model.Donor;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.fragments.donor.DonorMapFragment;
import com.example.heartflowapp.view.ui.MapsFragment;

public class DonorActivity extends AppCompatActivity {
    ActivityManagerBinding binding;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.donor_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        currentUserId =   intent.getStringExtra("USER");
        binding.managerBottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
//                updateSiteFragment();
                return true;
            } else if (item.getItemId() == R.id.donor_map) {
                Bundle bundle = new Bundle();
                bundle.putString("USER", currentUserId);
                DonorMapFragment  map = new DonorMapFragment();
                map.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.donor_container, map)
                        .commit();
                return true;
            }
            return false;
        });

    }

}