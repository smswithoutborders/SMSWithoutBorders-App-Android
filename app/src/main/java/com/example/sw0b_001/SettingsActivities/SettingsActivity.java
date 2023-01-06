package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.sw0b_001.HomepageActivity;
import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.R;
import com.example.sw0b_001.databinding.ActivitySettingsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompactActivityCustomized {

    public static String LANGUAGE_SETTINGS = "";
    public static String STORED_ACCESS_SETTINGS = "";
    public static String SECURITY_SETTINGS = "";
    public static String GATEWAY_CLIENT_SETTINGS = "";

    public static final Map<String, Integer> SETTINGS_ICON_MAPPER = new HashMap<String, Integer>();

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_navbar);
        bottomNavBar(bottomNavigationView);
        bottomNavBar(bottomNavigationView);
    }

    public void bottomNavBar(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.recents: {
                        Intent recentsIntent = new Intent(getApplicationContext(), HomepageActivity.class);
                        startActivity(recentsIntent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }
                return false;
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this, HomepageActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
