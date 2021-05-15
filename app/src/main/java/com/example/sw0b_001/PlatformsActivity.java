package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.os.Bundle;
import android.util.Log;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;
import com.example.sw0b_001.Providers.Platforms.PlatformsAdapter;


import java.util.ArrayList;
import java.util.List;

public class PlatformsActivity extends AppCompatActivity{
    // TODO: Check if user credentials are stored else log them out
    // TODO: Fill in bottomBar actions (dashboard, settings, logs, exit)
    // TODO: Include loader when message is sending...

    RecyclerView recyclerView;
    List<Platforms> platforms;
    PlatformsAdapter platformsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms);
        platforms = new ArrayList<>();
        recyclerView = findViewById(R.id.platforms_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                PlatformDao platformsDao = platformDb.platformDao();
                platforms = platformsDao.getAll();
                Log.d(this.getClass().getSimpleName(), ": size>> " + platforms.size());
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        platformsAdapter = new PlatformsAdapter(this, platforms, R.layout.recycler_view_list_platform);
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}