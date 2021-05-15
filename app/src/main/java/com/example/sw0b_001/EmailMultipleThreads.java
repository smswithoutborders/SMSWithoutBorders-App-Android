package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Emails.EmailCustomThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadDao;
import com.google.android.gms.common.server.converter.StringToIntConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongToIntFunction;

public class EmailMultipleThreads extends AppCompatActivity {
    RecyclerView recyclerView;
    List<EmailCustomThreads> emailThreads;
    EmailRecyclerViewAdapter emailThreadsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailactivities_recent);

        emailThreads = new ArrayList<>();
        recyclerView = findViewById(R.id.email_subject_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        long platformId = getIntent().getLongExtra("platformId", -1);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                EmailThreadDao emailThreadDao = platformDb.emailThreadDao();
                emailThreads = emailThreadDao.getAllForPlatform(platformId);
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        emailThreadsAdapter = new EmailRecyclerViewAdapter(this, emailThreads, R.layout.recycler_view_list_platform);
        recyclerView.setAdapter(emailThreadsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void composeEmail(View view) {
       startActivity(new Intent(this, EmailCompose.class));
       finish();
    }
}