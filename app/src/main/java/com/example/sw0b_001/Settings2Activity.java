package com.example.sw0b_001;


import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);


        Toolbar composeToolbar = (Toolbar) findViewById(R.id.compose_toolbar);
        setSupportActionBar(composeToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        // Get reference of widgets from XML layout
        final ListView ListView = (ListView) findViewById(R.id.ListViewID);
        final Button AddItem = (Button) findViewById(R.id.AddItemBtn);
        // Initializing a new String Array
        String[] values = new String[]{
                "Gateway settings",
                "Stored Access",
                "Languages",
                "Help and support",
                "About",
                "Update"
        };

        // Create a List from String Array elements
        final List<String> values_list = new ArrayList<String>(Arrays.asList(values));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, values_list);

        // DataBind ListView with items from ArrayAdapter
        ListView.setAdapter(arrayAdapter);


    }
}

