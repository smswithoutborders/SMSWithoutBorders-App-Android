package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.HomepageActivity;
import com.example.sw0b_001.Models.SettingsRecyclerAdapter;
import com.example.sw0b_001.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    public static final String GATEWAY_CLIENT_SETTINGS = "Gateway Clients";
    public static final String STORED_ACCESS_SETTINGS = "Stored Access";

    public static final Map<String, Integer> SETTINGS_ICON_MAPPER = new HashMap<String, Integer>() {
        {
            put(GATEWAY_CLIENT_SETTINGS, R.drawable.ic_round_router_24);
            put(STORED_ACCESS_SETTINGS, R.drawable.ic_round_sync_24);
        }};

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
        listOfSettings.add(GATEWAY_CLIENT_SETTINGS);
        listOfSettings.add(STORED_ACCESS_SETTINGS);

        RecyclerView settingsRecyclerView = findViewById(R.id.settings_recycler_view);
        // settingsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        SettingsRecyclerAdapter settingsRecyclerAdapter = new SettingsRecyclerAdapter(this, listOfSettings, R.layout.layout_cardlist_settings);
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
                        overridePendingTransition(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
                        finish();
                    }
                }
                return false;
            }
        });
    }
}
