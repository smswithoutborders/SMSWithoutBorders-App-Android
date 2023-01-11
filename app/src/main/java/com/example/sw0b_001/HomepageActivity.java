package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.sw0b_001.HomepageFragments.AvailablePlatformsFragment;
import com.example.sw0b_001.HomepageFragments.RecentsFragment;
import com.example.sw0b_001.HomepageFragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

public class HomepageActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.recents);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                findViewById(R.id.fragment_title).setVisibility(View.GONE);
                switch(item.getItemId()) {
                    case R.id.recents: {
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                RecentsFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        break;
                    }
                    case R.id.settings: {
                        findViewById(R.id.fragment_title).setVisibility(View.VISIBLE);
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                SettingsFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        break;
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

    public void onComposePlatformClick(View view) {
        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                        AvailablePlatformsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .commit();
    }
}