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

        // TODO: populate from database
//        subjects = new String[]{"Subject1", "Subject2", "Subject3"};
//        emails = new String[]{"info@smswithoutborders.com", "afkanerd@gmail.com", "wisdom@smswithoutborders.com"};
//        images = new int[]{R.mipmap.letter_a, R.drawable.roundgmail, R.drawable.roundgmail};

        ArrayList<EmailThreads> threads = new EmailThreads().getAll();
        for(EmailThreads thread : threads ) {
            subjects[subjects.length + 1] = thread.getSubject();
            emails[emails.length + 1] = thread.getEmail();
            images[images.length + 1] = CustomHelpers.getLetterImage(thread.getEmail().charAt(0));
        }

        Intent intent = new Intent(this, EmailSingleThreads.class);
        intent.putExtra("platform_name", getIntent().getStringExtra("text1"));
        PlatformsAdapter platformsAdapter = new PlatformsAdapter(this, subjects, emails, images, intent);
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void composeEmail(View view) {
       startActivity(new Intent(this, EmailCompose.class));
       finish();
    }
}