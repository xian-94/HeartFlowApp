package com.example.heartflowapp.view.authentication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.heartflowapp.R;
import com.example.heartflowapp.utils.AuthPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity implements SignUpFragment.SignupListener {

    TabLayout authTabs;
    ViewPager2 viewPager2;
    AuthPagerAdapter authPagerAdapter;


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

        // Set adapter to render auth form
        authTabs = findViewById(R.id.tabs);
        viewPager2 = findViewById(R.id.auth_container);
        authPagerAdapter = new AuthPagerAdapter(this);
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

    @Override
    public void onSignup(String role, String email, String password) {

    }
}