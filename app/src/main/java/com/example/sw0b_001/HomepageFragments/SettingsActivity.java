package com.example.sw0b_001.HomepageFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.sw0b_001.HomepageOldActivity;
import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.R;
import com.example.sw0b_001.databinding.ActivitySettingsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

public class SettingsActivity extends AppCompactActivityCustomized {

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
                        Intent recentsIntent = new Intent(getApplicationContext(), HomepageOldActivity.class);
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
