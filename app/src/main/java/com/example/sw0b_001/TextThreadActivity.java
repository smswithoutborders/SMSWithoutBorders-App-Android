package com.example.sw0b_001;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadRecyclerAdapter;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Text.TextMessage;
import com.example.sw0b_001.Providers.Text.TextMessageDao;
import com.example.sw0b_001.Providers.Text.TextMessageRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TextThreadActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<TextMessage> textMessages;
    TextMessageRecyclerAdapter textMessageRecyclerAdapter;
    FloatingActionButton composeBtn;

    ActionBar ab;
    long platform_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Toolbar composeToolbar = (Toolbar) findViewById(R.id.single_thread_toolbar);
        setSupportActionBar(composeToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        textMessages = new ArrayList<>();
        recyclerView = findViewById(R.id.single_list_text);
        refresh();
    }

    private void refresh() {
        platform_id = getIntent().getLongExtra("platform_id", -1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                TextMessageDao textMessageDao = platformDb.textMessageDao();
                textMessages = textMessageDao.loadAllByPlatformId(platform_id);
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        TextView subjectView = findViewById(R.id.subject);
//        subjectView.setText(textMessages.get(0).getSubject());
//        for(EmailMessage email : emailMessage)
//            email.setRecipient(emailThreads.get(0).getRecipient());
//        String recipient = emailThreads.get(0).getRecipient();
//        String subject = emailThreads.get(0).getSubject();

        textMessageRecyclerAdapter = new TextMessageRecyclerAdapter(this, textMessages, R.layout.layout_cardlist_thread, ab);
        recyclerView.setAdapter(textMessageRecyclerAdapter);
        LinearLayoutManager ln = new LinearLayoutManager(this);
        ln.setStackFromEnd(true);
        ln.setReverseLayout(true);
        recyclerView.setLayoutManager(ln);

        composeBtn = findViewById(R.id.floating_compose_body);
        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });
    }

    public void composeEmail() {
        Intent intent = new Intent(this, TextComposeActivity.class);
        intent.putExtra("platform_id", getIntent().getLongExtra("platform_id", -1));
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_single_thread_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                int views = textMessageRecyclerAdapter.getItemCount();
                for(int i=0;i<views;++i) {
                    View view = recyclerView.getLayoutManager().findViewByPosition(i);
                    if(view.isSelected()) {
                        TextMessage message = textMessageRecyclerAdapter.textMessages.get(i);
                        textMessageRecyclerAdapter.textMessages.remove(i);

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                                        Datastore.class, Datastore.DBName).build();
                                TextMessageDao textMessageDao = platformDb.textMessageDao();
                                textMessageDao.delete(message);

                            }
                        };
                        Thread dbFetchThread = new Thread(runnable);
                        dbFetchThread.start();
                        textMessageRecyclerAdapter.notifyItemRemoved(i);
                        textMessageRecyclerAdapter.notifyItemRangeChanged(i, textMessageRecyclerAdapter.getItemCount());
                        try {
                            dbFetchThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
        int views = textMessageRecyclerAdapter.getItemCount();
        for(int i=0;i<views;++i) {
            View view = recyclerView.getLayoutManager().findViewByPosition(i);
            if(view.isSelected()) {
                removeSelection = true;
//                view.setSelected(false);
//                view.setBackgroundColor(getResources().getColor(R.color.default_dark, getApplicationContext().getTheme()));
                textMessageRecyclerAdapter.deselected(view);
            }
        }
        if(!removeSelection) {
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
        return true;
    }
}