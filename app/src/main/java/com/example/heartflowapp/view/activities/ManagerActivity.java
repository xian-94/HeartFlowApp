package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.heartflowapp.R;
import com.example.heartflowapp.databinding.ActivityManagerBinding;
import com.example.heartflowapp.model.SiteManager;
import com.example.heartflowapp.view.fragments.manager.SiteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerActivity extends AppCompatActivity {
    ActivityManagerBinding binding;

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

        BottomNavigationView navView = findViewById(R.id.manager_bottom_navigation);
        binding.managerBottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.site) {
                SiteFragment siteFragment = new SiteFragment();
                Bundle bundle = new Bundle();
                bundle.putString("USER", String.valueOf(currentUser));
                siteFragment.setArguments(bundle);
                replaceFragment(siteFragment);
                return true;
            } else {

            }

            return false;
        });

    }

    private void replaceFragment(Fragment fm) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.manager_container, fm);
        transaction.commit();

    }

}