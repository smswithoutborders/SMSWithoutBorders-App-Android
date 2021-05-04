package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

public class Platforms extends AppCompatActivity {
    // TODO: Check if user credentials are stored else log them out
    // TODO: Fill in bottomBar actions (dashboard, settings, logs, exit)
    // TODO: Include loader when message is sending...

    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;
    KeyStore keyStore;

    String activityLabel = "Activity Platforms";

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms);



        listView = findViewById(R.id.item_list);
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        try {
            listView.setAdapter(itemsAdapter);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        itemsAdapter.add("[+] GOOGLE: gmail");
        clickListener();



//        this.getActionBar().setTitle(activityLabel);
//        this.getSupportActionBar().setTitle(activityLabel);  // provide compatibility to all the versions

    }

    public void clickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                String clickedString = "Item just got clicked: [" + id + ":<"+ items.get(position)  + ">]";
                Toast.makeText(context, clickedString, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(parent.getContext(), RecentChats.class);
                intent.putExtra("platform_name", items.get(position));
                startActivity(intent);
            }
        });
    }
}