package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.PlatformFetchWorker;
import com.example.sw0b_001.Providers.Platforms.Platforms;
import com.example.sw0b_001.Providers.Platforms.PlatformsAdapter;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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
//        Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
//                Datastore.class, Datastore.DBName).allowMainThreadQueries().build();
//        PlatformDao platformsDao = platformDb.platformDao();
//        platforms = platformsDao.getAll();
//        for(Platforms platform : platforms)
//            Log.d(this.getClass().getSimpleName(), ">> " + platform.getName());
//        platforms.add(new Platforms().setProvider("google").setName("gmail").setImage(R.drawable.settings).setDescription("by google"));
        platformsAdapter = new PlatformsAdapter(this, platforms, R.layout.recycler_view_list_platform);
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Log.d(this.getClass().getSimpleName(), ": size>> " + platforms.size());
//        platformsAdapter.update(platforms);
    }

    private void fetchPlaforms() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                PlatformDao platformsDao = platformDb.platformDao();
                platforms = platformsDao.getAll();
                Log.d(this.getClass().getSimpleName(), ": size>> " + platforms.size());
            }
        });
    }
}