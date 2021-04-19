package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class platforms extends AppCompatActivity {

    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;


    ListView listView;
    Button addItemButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms);


        listView = findViewById(R.id.item_list);
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter((itemsAdapter));

        itemsAdapter.add("Hello world 0");
        itemsAdapter.add("Hello world 1");
        itemsAdapter.add("Hello world 2");
        itemsAdapter.add("Hello world 3");
        itemsAdapter.add("Hello world 4");
    }

//    public void addItem(View view) {
//        EditText text = findViewById(R.id.edit_item);
//        String input = text.getText().toString();
//
//        if(!input.equals("")) {
//            itemsAdapter.add(input);
//            text.setText("");
//        }
//    }
}