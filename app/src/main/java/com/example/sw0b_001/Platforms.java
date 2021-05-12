package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.sw0b_001.ListPlatforms.Emails.EmailMultipleThreads;

import java.security.KeyStore;
import java.util.ArrayList;

public class Platforms extends AppCompatActivity {
    // TODO: Check if user credentials are stored else log them out
    // TODO: Fill in bottomBar actions (dashboard, settings, logs, exit)
    // TODO: Include loader when message is sending...

    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;
    KeyStore keyStore;
    String activityLabel = "Activity Platforms";
    ListView listView;
    RecyclerView recyclerView;

    String platforms[], descriptions[];
    int images[] = {R.drawable.roundgmail, R.drawable.roundtwitter};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        platforms = getResources().getStringArray(R.array.platforms);
        descriptions = getResources().getStringArray(R.array.description);

        PlatformsAdapter platformsAdapter = new PlatformsAdapter(this, platforms, descriptions, images, new Intent(this, EmailMultipleThreads.class));
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}