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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EmailThreadsActivity extends AppCompatActivity {
    public static int recyclerView = R.id.email_threads_recycler_view;
    List<EmailThreads> emailThreads;
    EmailThreadsRecyclerViewAdapter emailThreadsAdapter;
    long[] platformId;

    FloatingActionButton composeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_threads);

        emailThreads = new ArrayList<>();
        RecyclerView cardlist = findViewById(recyclerView);
//        cardlist.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        platformId = new long[]{getIntent().getLongExtra("platform_id", -1)};
        System.out.println(">> platformId: " + platformId[0]);

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

        emailThreadsAdapter = new EmailThreadsRecyclerViewAdapter(this, emailThreads, R.layout.layout_cardlist_threads, platformId[0]);
        cardlist.setAdapter(emailThreadsAdapter);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setStackFromEnd(true);
        ln.setReverseLayout(true);
        cardlist.setLayoutManager(ln);
        emailThreadsAdapter.notifyDataSetChanged();

        composeBtn = findViewById(R.id.floating_compose_body);
        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });
    }

    public void composeEmail() {
        Intent intent = new Intent(this, EmailComposeActivity.class);
        intent.putExtra("platform_id", platformId[0]);
        startActivity(intent);
    }
}