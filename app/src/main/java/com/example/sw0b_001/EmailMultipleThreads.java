package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sw0b_001.Providers.Emails.EmailCustomThreads;

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

//        EmailCustomThreads thread = new EmailCustomThreads()
//                .setSubject("Introduction Thread")
//                .setSubjectSub("info@smswithoutborders.com")
//                .setId(1)
//                .setTopRightText("2021-01-02");
//
//        EmailCustomThreads thread1 = new EmailCustomThreads()
//                .setSubject("Introduction Thread 2")
//                .setSubjectSub("wisdom@smswithoutborders.com")
//                .setId(1)
//                .setTopRightText("2021-01-01");
//        ArrayList<EmailCustomThreads> threads = new ArrayList<EmailCustomThreads>();
//        threads.add(thread);
//        threads.add(thread1);
//
//        Intent intent = new Intent(this, EmailSingleThreads.class);
////        intent.putExtra("platform_name", getIntent().getStringExtra("text1"));
//        EmailRecyclerViewAdapter adapter = new EmailRecyclerViewAdapter(this, threads, intent, R.layout.activity_cardlist);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void composeEmail(View view) {
       startActivity(new Intent(this, EmailCompose.class));
       finish();
    }
}