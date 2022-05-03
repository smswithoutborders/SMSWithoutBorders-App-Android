package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
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
        platformId = new long[]{getIntent().getLongExtra("platform_id", -1)};
//        System.out.println(">> platformId: " + platformId[0]);

        findViewById(R.id.no_emails_sent).setVisibility(View.INVISIBLE);
        composeBtn = findViewById(R.id.floating_compose_body);
        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });

        refresh();
    }

    private void refresh() {
        RecyclerView cardlist = findViewById(recyclerView);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DatabaseName).build();
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

        if(emailThreads.size() < 1 )
            findViewById(R.id.no_emails_sent).setVisibility(View.VISIBLE);
        /*
        emailThreadsAdapter = new EmailThreadsRecyclerViewAdapter(this, emailThreads, R.layout.layout_cardlist_threads, platformId[0], this);
        cardlist.setAdapter(emailThreadsAdapter);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setStackFromEnd(true);
        ln.setReverseLayout(true);
        cardlist.setLayoutManager(ln);
        emailThreadsAdapter.notifyDataSetChanged();

         */
    }

    public void composeEmail() {
        Intent intent = new Intent(this, EmailComposeActivity.class);
        intent.putExtra("platform_id", platformId[0]);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            // Write your code here...
            refresh();
        }
    }
}