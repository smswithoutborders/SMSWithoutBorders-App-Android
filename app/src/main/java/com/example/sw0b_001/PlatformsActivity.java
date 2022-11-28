package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformsHandler;
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter;
import com.example.sw0b_001.databinding.ActivityPlatformsBinding;

import java.util.List;

public class PlatformsActivity extends AppCompactActivityCustomized {
    RecyclerView recyclerView;
    PlatformsRecyclerAdapter platformsRecyclerAdapter;

    private ActivityPlatformsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlatformsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        List<Platform> platforms = PlatformsHandler.getAllPlatforms(getApplicationContext());

        Toolbar gatewayClientToolbar = (Toolbar) findViewById(R.id.platforms_toolbar);
        setSupportActionBar(gatewayClientToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

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