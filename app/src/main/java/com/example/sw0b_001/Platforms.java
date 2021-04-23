package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.util.Hex;

import java.security.PublicKey;
import java.util.ArrayList;

public class Platforms extends AppCompatActivity {
    // TODO: Check if user credentials are stored else log them out
    // TODO: Fill in bottomBar actions (dashboard, settings, logs, exit)
    // TODO: Include loader when message is sending...

    ArrayList<String> items = new ArrayList<>();
    SecurityLayer securityLayer = new SecurityLayer();
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

        itemsAdapter.add("[+] GOOGLE: gmail");

        PublicKey publicKey = securityLayer.generateKeys();

//        String s = "42Gears Mobility Systems";
        String strPublicKey = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
        String strPrivateKey = Base64.encodeToString(securityLayer.getPrivateKey().getEncoded(), Base64.DEFAULT);

        System.out.println("[-] PublicKey: " + strPublicKey);
        System.out.println("[-] PrivateKey: " + strPrivateKey);

        clickListener();
    }

    public void clickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
                String clickedString = "Item just got clicked: [" + id + ":<"+ items.get(position)  + ">]";
                Toast.makeText(context, clickedString, Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(parent.getContext(), SendMessageActivity.class);
                startActivity(intent);
            }
        });
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