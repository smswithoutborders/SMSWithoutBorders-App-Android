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
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    public static String GATEWAY_CLIENT_SETTINGS = "";
    public static String STORED_ACCESS_SETTINGS = "";

    public static final Map<String, Integer> SETTINGS_ICON_MAPPER = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        GATEWAY_CLIENT_SETTINGS = getString(R.string.settings_gateway_clients_text);
        STORED_ACCESS_SETTINGS = getString(R.string.settings_store_access_text);

        SETTINGS_ICON_MAPPER.put(GATEWAY_CLIENT_SETTINGS, R.drawable.ic_round_router_24);
        SETTINGS_ICON_MAPPER.put(STORED_ACCESS_SETTINGS, R.drawable.ic_round_sync_24);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
        finish();
    }
}
