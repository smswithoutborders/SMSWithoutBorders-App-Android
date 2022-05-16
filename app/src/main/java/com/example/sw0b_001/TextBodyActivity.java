package com.example.sw0b_001;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Providers.Emails.EmailThreadRecyclerAdapter;
import com.example.sw0b_001.Models.Platforms.PlatformDao;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Providers.Text.TextMessage;
import com.example.sw0b_001.Providers.Text.TextMessageDao;

public class TextBodyActivity extends AppCompatActivity {
    TextMessage textMessage;
    Platform platform;
    EmailThreadRecyclerAdapter emailCustomMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_body);

        refresh();
    }

    private void refresh() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DatabaseName).build();
                long messageId = getIntent().getLongExtra("text_message_id", -1);
                TextMessageDao textMessageDao = platformDb.textMessageDao();
                textMessage = textMessageDao.get(messageId);

                PlatformDao platformDao = platformDb.platformDao();
                platform = platformDao.get(textMessage.getPlatformId());
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        setTitle(platform.getProvider());

        TextView subject = findViewById(R.id.text_provider);
        subject.setText(platform.getName());
//
        ImageView emailImage = findViewById(R.id.recents_platform_logo);
        emailImage.setImageResource(textMessage.getImage());

        TextView dateTime = findViewById(R.id.subjectSub);
        dateTime.setText(textMessage.getDatetime());

         TextView status = findViewById(R.id.bottomRight);
        if(textMessage.getStatus().equals("requested") || textMessage.getStatus().equals("sent"))
            status.setTextColor(getApplication().getResources().getColor(R.color.success_blue, getApplication().getTheme()));
        status.setText(textMessage.getStatus());

        TextView body = findViewById(R.id.body);
        body.setText(textMessage.getBody());
    }

    @Override
    public boolean onSupportNavigateUp(){
        setResult(Activity.RESULT_OK, new Intent());
        finish();
        return true;
    }
}