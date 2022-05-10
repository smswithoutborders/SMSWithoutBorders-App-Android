package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class HomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        SearchView searchView = (SearchView) findViewById(R.id.search_bar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_navbar);
        bottomNavBar(bottomNavigationView);
    }

    public void bottomNavBar(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.recents);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.settings: {
                        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsIntent);
                        overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
                        finish();
                    }
                }
                return false;
            }
        });
    }

    public void onClickPlatformSelect(View view) {
        Intent platformIntent = new Intent(getApplicationContext(), PlatformsActivity.class);
        startActivity(platformIntent);
    }
}
