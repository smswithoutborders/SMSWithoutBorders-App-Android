package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.SettingsRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_navbar);
        bottomNavBar(bottomNavigationView);

        populateSettings();

    }

    public void populateSettings() {
        List<String> listOfSettings = new ArrayList<>();
        listOfSettings.add("Gateway Clients");

        RecyclerView settingsRecyclerView = findViewById(R.id.settings_recycler_view);
        // settingsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SettingsRecyclerAdapter settingsRecyclerAdapter = new SettingsRecyclerAdapter(this, listOfSettings, R.layout.layout_cardlist_settings, this);
        settingsRecyclerView.setAdapter(settingsRecyclerAdapter);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void bottomNavBar(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.recents: {
                        Intent recentsIntent = new Intent(getApplicationContext(), HomepageActivity.class);
                        startActivity(recentsIntent);
                        finish();
                    }
                }
                return false;
            }
        });
    }
}
