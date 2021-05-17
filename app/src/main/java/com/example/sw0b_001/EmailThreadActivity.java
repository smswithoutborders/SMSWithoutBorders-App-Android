package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadRecyclerAdapter;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreads;

import java.util.ArrayList;
import java.util.List;

public class EmailThreadActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<EmailMessage> emailMessage;
    List<EmailThreads> emailThreads;
    EmailThreadRecyclerAdapter emailCustomMessageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_single_threads);
        emailMessage = new ArrayList<>();
        recyclerView = findViewById(R.id.email_single_thread);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        long threadId = getIntent().getLongExtra("thread_id", -1);
        System.out.println(">> threadID: " + threadId);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                EmailMessageDao emailMessageDao = platformDb.emailDao();
                EmailThreadsDao emailThreadsDao = platformDb.emailThreadDao();
                emailMessage = emailMessageDao.loadAllByThreadId(new long[]{threadId});
                emailThreads = emailThreadsDao.loadAllByIds(new long[]{threadId});
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TextView subjectView = findViewById(R.id.subject);
        subjectView.setText(emailThreads.get(0).getSubject());
        for(EmailMessage email : emailMessage)
            email.setRecipient(emailThreads.get(0).getRecipient());
        emailCustomMessageAdapter = new EmailThreadRecyclerAdapter(this, emailMessage, R.layout.layout_cardlist_thread);
        recyclerView.setAdapter(emailCustomMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_single_thread_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText to = findViewById(R.id.email_to);
        EditText subject = findViewById(R.id.email_subject);
        EditText body = findViewById(R.id.email_body);
        switch (item.getItemId()) {
            case R.id.discard:
                startActivity(new Intent(this, EmailThreadsActivity.class));
                to.setText("");
                subject.setText("");
                body.setText("");
                finish();
                return true;

            case R.id.action_send:
                if(to.getText().toString().isEmpty()) {
                    to.setError("Recipient cannot be empty!");
                }
                if(subject.getText().toString().isEmpty()) {
                    subject.setError("Subject should not be empty!");
                }
                if(body.getText().toString().isEmpty()) {
                    body.setError("Body should not be empty!");
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}