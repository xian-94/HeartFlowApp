package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.databinding.ActivityManagerBinding;
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.fragments.manager.SiteFormFragment;
import com.example.heartflowapp.view.fragments.manager.SiteFragment;
import com.example.heartflowapp.view.ui.MapsFragment;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ManagerActivity extends AppCompatActivity implements SiteFormFragment.SiteListener {
    ActivityManagerBinding binding;
    private String currentUserId;
    private String siteId;


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

        DatabaseManager db = new DatabaseManager();


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
            return false;
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

    public void checkManagerSite(DatabaseManager db) {
        db.getRef("site")
                .whereArrayContains("managerIds", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        siteId = document.getId();
                    } else {
                        siteId = null;
                    }
                    updateSiteFragment();
                })
                .addOnFailureListener(e -> {
                    siteId = null;
                    updateSiteFragment();
                });
    }

    private void updateSiteFragment() {
        SiteFragment siteFragment = new SiteFragment();
        Bundle args = new Bundle();
        args.putString("USER", currentUserId);
        args.putString("SITE", siteId);
        siteFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.manager_container, siteFragment)
                .commit();
    }


}
