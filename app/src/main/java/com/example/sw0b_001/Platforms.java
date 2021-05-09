package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
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

        PlatformsAdapter platformsAdapter = new PlatformsAdapter(this, platforms, descriptions, images);
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}