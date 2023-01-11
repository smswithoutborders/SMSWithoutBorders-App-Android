package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.sw0b_001.HomepageFragments.RecentsFragment;
import com.example.sw0b_001.HomepageFragments.SettingsActivity;
import com.example.sw0b_001.HomepageFragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.recents);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.recents: {
                        fragmentTransaction.replace(R.id.homepage_fragment_container_view, RecentsFragment.class, null)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commitNow();
                    }
                    case R.id.settings: {
                        fragmentTransaction.replace(R.id.homepage_fragment_container_view, SettingsFragment.class, null)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commitNow();
                    }

                    case R.id.messages: {
//                        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
//                        startActivity(settingsIntent);
//                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
                return false;
            }
        });
    }
}