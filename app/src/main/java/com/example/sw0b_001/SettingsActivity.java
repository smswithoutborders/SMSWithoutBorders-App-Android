package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigationView = findViewById(R.id.settings_bottomnav);

        bottomNavigationView.setSelectedItemId(R.id.settings);
        findViewById(R.id.settings).setEnabled(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Log.i(this.getClass().getSimpleName(), item.getTitle().toString());
                switch(item.getItemId()) {
                    case R.id.platform:
                        startActivity(new Intent(getApplicationContext(), PlatformsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                }
                return false;
            }
        });
    }
}