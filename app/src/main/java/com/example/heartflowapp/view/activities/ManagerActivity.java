package com.example.heartflowapp.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heartflowapp.R;
import com.example.heartflowapp.model.SiteManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManagerActivity extends AppCompatActivity {
    private SiteManager currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        Intent intent = getIntent();
        currentUser = (SiteManager) intent.getSerializableExtra("USER");

        BottomNavigationView navView = findViewById(R.id.manager_bottom_navigation);

    }
}