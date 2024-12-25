package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heartflowapp.MainActivity;
import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.databinding.ActivityManagerBinding;
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.fragments.manager.ManagerProfileFragment;
import com.example.heartflowapp.view.fragments.manager.SiteFormFragment;
import com.example.heartflowapp.view.fragments.manager.ManagerDashboardFragment;
import com.example.heartflowapp.view.ui.MapsFragment;

import java.util.List;

public class ManagerActivity extends AppCompatActivity implements SiteFormFragment.SiteListener {
    ActivityManagerBinding binding;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.manager_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        SiteManager currentUser = (SiteManager) intent.getSerializableExtra("USER");
        if (currentUser != null) {
            currentUserId = currentUser.getUserId();
        }

        binding.managerBottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.site) {
                updateSiteFragment();
                return true;
            } else if (item.getItemId() == R.id.manager_map) {
                Bundle bundle = new Bundle();
                bundle.putString("USER", currentUserId);
                MapsFragment map = new MapsFragment();
                map.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.manager_container, map)
                        .commit();
                return true;
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putString("USER", currentUserId);
                ManagerProfileFragment map = new ManagerProfileFragment();
                map.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.manager_container, map)
                        .commit();
                return true;
            }
        });

        binding.managerBottomNavigation.setSelectedItemId(R.id.site);

    }


    // Site management logic
    @Override
    public void onSaveSite(String name, String address, double lat, double lng, String date, List<String> selectedBloodTypes) {
        DatabaseManager db = new DatabaseManager();
        Site site = new Site(name, address, lat, lng, date, selectedBloodTypes);
        site.setCreatedBy(currentUserId);
        ProgressManager.showProgress(this.getSupportFragmentManager());
        db.getRef("site")
                .add(site)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    documentReference.update("siteId", documentId)
                            .addOnSuccessListener(aVoid -> {
                                ProgressManager.dismissProgress();
                                Toast.makeText(ManagerActivity.this, "Save site successfully", Toast.LENGTH_SHORT).show();
                                updateSiteFragment();
                            })
                            .addOnFailureListener(e -> {
                                ProgressManager.dismissProgress();
                                Toast.makeText(ManagerActivity.this, "Failed to update site with ID", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    ProgressManager.dismissProgress();
                    Toast.makeText(ManagerActivity.this, "Failed to save site", Toast.LENGTH_SHORT).show();
                });
    }


    private void updateSiteFragment() {
        ManagerDashboardFragment siteFragment = new ManagerDashboardFragment();
        Bundle args = new Bundle();
        args.putString("USER", currentUserId);
        siteFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.manager_container, siteFragment)
                .commit();
    }

    public void logout() {
        // Navigate back to main
        Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
        // Clear back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
