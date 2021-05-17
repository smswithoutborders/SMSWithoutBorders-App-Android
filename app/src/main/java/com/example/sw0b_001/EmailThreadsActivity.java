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
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class EmailThreadsActivity extends AppCompatActivity {
    public static int recyclerView = R.id.email_threads_recycler_view;
    List<EmailThreads> emailThreads;
    EmailThreadsRecyclerViewAdapter emailThreadsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_threads);

        emailThreads = new ArrayList<>();
        RecyclerView cardlist = findViewById(recyclerView);
        cardlist.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        long[] platformId = {getIntent().getLongExtra("platformId", -1)};
        System.out.println(">> platformId: " + platformId);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                EmailThreadsDao emailThreadsDao = platformDb.emailThreadDao();
                emailThreads = emailThreadsDao.loadAllByPlatformId(platformId);
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        emailThreadsAdapter = new EmailThreadsRecyclerViewAdapter(this, emailThreads, R.layout.layout_cardlist_threads);
        cardlist.setAdapter(emailThreadsAdapter);
        cardlist.setLayoutManager(new LinearLayoutManager(this));

    }

    public void composeEmail(View view) {
       startActivity(new Intent(this, EmailComposeActivity.class));
       finish();
    }
}