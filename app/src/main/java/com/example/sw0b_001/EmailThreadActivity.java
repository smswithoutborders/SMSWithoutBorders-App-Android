package com.example.sw0b_001;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadRecyclerAdapter;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EmailThreadActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<EmailMessage> emailMessage;
    List<EmailThreads> emailThreads;
    EmailThreadRecyclerAdapter emailCustomMessageAdapter;
    FloatingActionButton composeBtn;

    ActionBar ab;
    private SMSSateViewModel model;
    long threadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_single_threads);
        Toolbar composeToolbar = (Toolbar) findViewById(R.id.single_thread_toolbar);
        setSupportActionBar(composeToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        emailMessage = new ArrayList<>();
        recyclerView = findViewById(R.id.email_single_thread);
        refresh();

        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("sms_state_changed"));
    }
    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i(getLocalClassName(), ">> BROADCAST RECEIVED - REFRESHING");
            refresh();
        }
    };

    private void refresh() {
        threadId = getIntent().getLongExtra("thread_id", -1);
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
        String recipient = emailThreads.get(0).getRecipient();
        String subject = emailThreads.get(0).getSubject();

        emailCustomMessageAdapter = new EmailThreadRecyclerAdapter(this, emailMessage, R.layout.layout_cardlist_thread);
        recyclerView.setAdapter(emailCustomMessageAdapter);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setStackFromEnd(true);
        ln.setReverseLayout(true);
        recyclerView.setLayoutManager(ln);

        composeBtn = findViewById(R.id.floating_compose_body);
        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail(recipient, subject);
            }
        });
    }

    public void composeEmail(String recipient, String subject) {
        Intent intent = new Intent(this, EmailComposeActivity.class);
        intent.putExtra("thread_id", threadId);
        intent.putExtra("platform_id", getIntent().getLongExtra("platform_id", -1));
        intent.putExtra("recipient", recipient);
        intent.putExtra("subject", subject);
        startActivityForResult(intent, 1);
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
            case R.id.action_delete:
                startActivity(new Intent(this, EmailThreadsActivity.class));
                to.setText("");
                subject.setText("");
                body.setText("");
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            // Write your code here...
            refresh();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        // remove all highlighted
        boolean removeSelection = false;
        int views = emailCustomMessageAdapter.getItemCount();
        for(int i=0;i<views;++i) {
            View view = recyclerView.getLayoutManager().findViewByPosition(i);
            if(view.isSelected()) {
                removeSelection = true;
                view.setSelected(false);
                view.setBackgroundColor(getResources().getColor(R.color.default_dark, getApplicationContext().getTheme()));
            }
        }
        if(!removeSelection) {
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
        return true;
    }
}