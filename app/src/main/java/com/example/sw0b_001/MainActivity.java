package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.sw0b_001.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setEnabled(false);

        if( checkPermission(Manifest.permission.SEND_SMS)) {
            sendBtn.setEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            sendBtn.setEnabled(true);
        }
    }

    public void sendMessage(View view) {
        EditText eNumber = findViewById(R.id.editPhonenumber);
        EditText eText = findViewById(R.id.editMessage);

        String number = eNumber.getText().toString();
        String text = eText.getText().toString();

        // TODO: check if valid input

        if(checkPermission(Manifest.permission.SEND_SMS)) {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(number, null, text, null, null);
            Toast.makeText(this, "Message sent!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);

        return (check == PackageManager.PERMISSION_GRANTED);
    }

}