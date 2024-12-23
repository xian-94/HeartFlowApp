package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.heartflowapp.R;
import com.example.heartflowapp.controller.DatabaseManager;
import com.example.heartflowapp.controller.ProgressManager;
import com.example.heartflowapp.databinding.ActivityManagerBinding;
import com.example.heartflowapp.model.Site;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.fragments.manager.EventFragment;
import com.example.heartflowapp.view.fragments.manager.SiteFormFragment;
import com.example.heartflowapp.view.fragments.manager.SiteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

public class ManagerActivity extends AppCompatActivity implements SiteFormFragment.SiteListener {
    ActivityManagerBinding binding;
    private String currentUserId;
    private String siteId;
    private DatabaseManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        SiteManager currentUser = (SiteManager) intent.getSerializableExtra("USER");
        if (currentUser != null) {
            currentUserId = currentUser.getUserId();
        }

        db = new DatabaseManager();
        checkManagerSite(db);


        binding.managerBottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.site) {
                updateSiteFragment();
                return true;
            } else if (item.getItemId() == R.id.events) {
                EventFragment eventFragment = new EventFragment();
                Bundle bundle = new Bundle();


            }
            return false;
        });

        binding.managerBottomNavigation.setSelectedItemId(R.id.site);

    }


    // Site management logic
    @Override
    public void onSaveSite(String name, String address, double lat, double lng, String userId) {
        Log.d("data", name + address + lat + lng + userId);
        DatabaseManager db = new DatabaseManager();
        Site site = new Site(name, address, lat, lng, userId);
        site.add(userId);
        ProgressManager.showProgress(this.getSupportFragmentManager());
        db.add("site", site, new DatabaseManager.NormalCallBack() {
            @Override
            public void onSuccess() {
                ProgressManager.dismissProgress();
                Toast.makeText(ManagerActivity.this, "Save site successfully", Toast.LENGTH_SHORT).show();
                SiteFragment siteFragment = new SiteFragment();
                Bundle bundle = new Bundle();
                bundle.putString("USER", currentUserId);
                siteFragment.setArguments(bundle);
                updateSiteFragment();
            }

            @Override
            public void onFailure(String message) {
                ProgressManager.dismissProgress();
                Toast.makeText(ManagerActivity.this, "Failed to save site", Toast.LENGTH_SHORT).show();
            }
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
