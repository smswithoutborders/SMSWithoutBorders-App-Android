package com.example.sw0b_001.ListPlatforms.Emails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sw0b_001.PlatformsAdapter;
import com.example.sw0b_001.R;

import java.security.KeyStore;
import java.util.ArrayList;

public class EmailActivities_Recent extends AppCompatActivity {

    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;
    KeyStore keyStore;
    ListView listView;
    RecyclerView recyclerView;

    String subjects[], emails[];
    int images[] = {R.drawable.roundgmail, R.drawable.roundgmail, R.drawable.roundgmail};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailactivities_recent);

        recyclerView = findViewById(R.id.email_subject_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        subjects = new String[]{"Subject1", "Subject2", "Subject3"};
        emails = new String[]{"info@smswithoutborders.com", "afkanerd@gmail.com", "wisdom@smswithoutborders.com"};

        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra("platform_name", getIntent().getStringExtra("text1"));
        PlatformsAdapter platformsAdapter = new PlatformsAdapter(this, subjects, emails, images, intent);
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void composeEmail(View view) {
        EditText email = findViewById(R.id.manual_send_email);
        EditText subject = findViewById(R.id.email_subject);

        String receipientEmailAddress = email.getText().toString();
        String emailSubject = subject.getText().toString();

        if(receipientEmailAddress.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(receipientEmailAddress).matches()) {
            email.setError("Invalid Email Address");
            return;
        }
        if( emailSubject.isEmpty()) {
            subject.setError("No subject provided!");
        }
    }
}