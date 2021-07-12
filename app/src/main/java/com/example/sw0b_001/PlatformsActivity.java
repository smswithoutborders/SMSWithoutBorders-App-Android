package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;
import com.example.sw0b_001.Providers.Platforms.PlatformsRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlatformsActivity extends AppCompatActivity{
    // TODO: Check if user credentials are stored else log them out
    // TODO: Fill in bottomBar actions (dashboard, settings, logs, exit)
    // TODO: Include loader when message is sending...

    RecyclerView recyclerView;
    List<Platforms> platforms;
    PlatformsRecyclerAdapter platformsRecyclerAdapter;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms);
        platforms = new ArrayList<>();
        recyclerView = findViewById(R.id.settings_list_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        findViewById(R.id.no_platform_txt).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_store_tokens).setVisibility(View.INVISIBLE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName)
                        .fallbackToDestructiveMigration()
                        .build();
                PlatformDao platformsDao = platformDb.platformDao();
                platforms = platformsDao.getAll();
//                Log.d(this.getClass().getSimpleName(), ": size>> " + platforms.size());
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(platforms.size() < 1 ) {
//            findViewById(R.id.textView3).setVisibility(View.INVISIBLE);
            findViewById(R.id.no_platform_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_store_tokens).setVisibility(View.VISIBLE);
        }

        platformsRecyclerAdapter = new PlatformsRecyclerAdapter(this, platforms, R.layout.layout_cardlist_threads);
        recyclerView.setAdapter(platformsRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.platform);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
//                Log.i(this.getClass().getSimpleName(), item.getTitle().toString());
                switch(item.getItemId()) {
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                }
                return false;
            }
        });
    }

    public void linkPrivacyPolicy(View view) {
        Uri intentUri = Uri.parse(getResources().getString(R.string.store_tokens));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }
}