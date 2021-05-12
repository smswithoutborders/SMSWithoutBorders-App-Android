package com.example.sw0b_001.ListPlatforms.Emails;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.sw0b_001.PlatformsAdapter;
import com.example.sw0b_001.R;

public class EmailSingleThreads extends AppCompatActivity {
    RecyclerView recyclerView;

    String status[], emails[], datetime[];
    int images[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_single_threads);

        Toolbar composeToolbar = (Toolbar) findViewById(R.id.single_thread_toolbar);
        setSupportActionBar(composeToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.email_single_thread);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        emails = new String[]{"info@smswithoutborders.com", "afkanerd@gmail.com", "wisdom@smswithoutborders.com"};
        datetime = new String[]{"2021-01-01", "2021-01-02", "2021-01-02"};
        status = new String[]{"delivered", "delivered", "failed"};
        images = new int[]{R.mipmap.letter_a, R.drawable.roundgmail, R.drawable.roundgmail};

        Intent intent = new Intent(this, EmailCompose.class);
        intent.putExtra("platform_name", getIntent().getStringExtra("text1"));
        PlatformsAdapter platformsAdapter = new PlatformsAdapter(this, emails, datetime, status, images, intent, R.layout.activity_cardlist_single);
        recyclerView.setAdapter(platformsAdapter);
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
                startActivity(new Intent(this, EmailMultipleThreads.class));
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