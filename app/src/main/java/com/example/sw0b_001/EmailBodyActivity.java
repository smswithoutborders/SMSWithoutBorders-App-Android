package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadRecyclerAdapter;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;

import java.util.ArrayList;
import java.util.List;

public class EmailBodyActivity extends AppCompatActivity {
    List<EmailMessage> emailMessage;
    List<EmailThreads> emailThreads;
    EmailThreadRecyclerAdapter emailCustomMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_body);
        emailMessage = new ArrayList<>();
        emailThreads = new ArrayList<>();

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
        long messageId = getIntent().getLongExtra("message_id", -1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DatabaseName).build();
                EmailMessageDao emailMessageDao = platformDb.emailDao();
                EmailThreadsDao emailThreadsDao = platformDb.emailThreadDao();
                emailMessage = emailMessageDao.loadAllByEmailId(messageId);
                emailThreads = emailThreadsDao.loadAllByIds(new long[]{emailMessage.get(0).getThreadId()});
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTitle(emailThreads.get(0).getSubject());

        TextView emailAddress = findViewById(R.id.email_address);
        emailAddress.setText(emailThreads.get(0).getRecipient());

        ImageView emailImage = findViewById(R.id.image);
        emailImage.setImageResource(emailThreads.get(0).getImage());

        TextView dateTime = findViewById(R.id.subjectSub);
        dateTime.setText(emailMessage.get(0).getDatetime());

        TextView status = findViewById(R.id.bottomRight);
        if(emailMessage.get(0).getStatus().equals("requested") || emailMessage.get(0).getStatus().equals("sent"))
            status.setTextColor(getApplication().getResources().getColor(R.color.success_blue, getApplication().getTheme()));
        else if(emailMessage.get(0).getStatus().equals("pending"))
            status.setTextColor(getApplication().getResources().getColor(R.color.pending_gray, getApplication().getTheme()));
        status.setText(emailMessage.get(0).getStatus());

        TextView body = findViewById(R.id.body);
        body.setText(emailMessage.get(0).getBody());
    }

    @Override
    public boolean onSupportNavigateUp(){
        setResult(Activity.RESULT_OK, new Intent());
        finish();
        return true;
    }
}