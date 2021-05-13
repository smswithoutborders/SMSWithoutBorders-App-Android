package com.example.sw0b_001.ListPlatforms.Emails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sw0b_001.CustomHelpers;
import com.example.sw0b_001.PlatformsAdapter;
import com.example.sw0b_001.R;

import java.util.ArrayList;

public class EmailMultipleThreads extends AppCompatActivity {
    RecyclerView recyclerView;

    String subjects[] = {};
    String emails[] = {};
    int images[] = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailactivities_recent);

        recyclerView = findViewById(R.id.email_subject_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        EmailThreads thread = new EmailThreads()
                .setSubject("Introduction Thread")
                .setSubjectSub("info@smswithoutborders.com")
                .setId(1)
                .setTopRightText("2021-01-02");
        ArrayList<EmailThreads> threads = new ArrayList<EmailThreads>();
        threads.add(thread);

        Intent intent = new Intent(this, EmailSingleThreads.class);
//        intent.putExtra("platform_name", getIntent().getStringExtra("text1"));
        EmailRecyclerViewAdapter adapter = new EmailRecyclerViewAdapter(this, threads, intent, R.layout.activity_cardlist);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void composeEmail(View view) {
       startActivity(new Intent(this, EmailCompose.class));
       finish();
    }
}