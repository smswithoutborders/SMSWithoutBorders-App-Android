package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.sw0b_001.R;

public class EmailBody extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_body);

        TextView tv = findViewById(R.id.single_subject_view);
        tv.setText(getIntent().getStringExtra("thread_subject"));
    }
}