package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

public class RecentChats extends AppCompatActivity {

    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;
    KeyStore keyStore;


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);

        listView = findViewById(R.id.item_list);
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        try {
            listView.setAdapter(itemsAdapter);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        clickListener();
        TextView email_search = findViewById(R.id.manual_send_email);
        String email = email_search.getText().toString();
        String subject;
//        email_search.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(keyCode == 66) {
//                    String receipientEmailAddress = email_search.getText().toString();
//                    accessMessages(receipientEmailAddress);
////                    email_search.setText("");
//
//                    return true;
//                }
//                return false;
//            }
//        });

        itemsAdapter.add("info@smswithoutwithoutborders.com");
    }


    public void clickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = getApplicationContext();
//                String clickedString = "Item just got clicked: [" + id + ":<"+ items.get(position)  + ">]";
//                Toast.makeText(context, clickedString, Toast.LENGTH_SHORT).show();
                String receipientEmailAddress = items.get(position);
                accessMessages(receipientEmailAddress);
            }
        });
    }

    private void accessMessages(String receipientEmailAddress) {
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra("receipientEmailAddress", receipientEmailAddress);
        startActivity(intent);
    }
}