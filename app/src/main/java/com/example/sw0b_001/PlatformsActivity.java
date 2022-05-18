package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformsHandler;
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class PlatformsActivity extends AppCompatActivity{
    // TODO: Check if user credentials are stored else log them out
    // TODO: Fill in bottomBar actions (dashboard, settings, logs, exit)
    // TODO: Include loader when message is sending...

    RecyclerView recyclerView;
    PlatformsRecyclerAdapter platformsRecyclerAdapter;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_platforms);
        List<Platform> platforms = PlatformsHandler.getAllPlatforms(getApplicationContext());

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.list_synced_platforms);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        platformsRecyclerAdapter = new PlatformsRecyclerAdapter(this, platforms, R.layout.layout_cardlist_platforms, this);
        recyclerView.setAdapter(platformsRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            Intent homepageIntent = new Intent(getApplicationContext(), HomepageActivity.class);
            startActivity(homepageIntent);
            finish();
        }
    }
}