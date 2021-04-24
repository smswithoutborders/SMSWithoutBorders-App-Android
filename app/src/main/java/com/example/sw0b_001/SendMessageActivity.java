package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class SendMessageActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.sw0b_001.MESSAGE";

    String SMS_SENT = "SENT";
    String SMS_DELIVERED = "DELIVERED";

    SecurityLayer securityLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);

        Button sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setEnabled(false);

        if( checkPermission(Manifest.permission.SEND_SMS)) {
            sendBtn.setEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            sendBtn.setEnabled(true);
        }
        securityLayer = new SecurityLayer();
    }

    public void smsFailed() {

    }

    public void sendMessage(View view) {
        EditText eNumber = findViewById(R.id.editPhonenumber);
        EditText eText = findViewById(R.id.editMessage);

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("SENT"), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("DELIVERED"), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));


        String number = eNumber.getText().toString();
        String plainText = eText.getText().toString();
        if(plainText.isEmpty()) {
            Toast.makeText(this, "Text Cannot be empty!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            if(checkPermission(Manifest.permission.SEND_SMS)) {
                String IV = new String(securityLayer.getIV(), "UTF-8");
                String transmissionText = IV + ":" + plainText;
                byte[] encryptedText = securityLayer.encrypt(transmissionText);

                System.out.println("Transmission String: " + transmissionText);
                System.out.println("[+] Decrypted: " + new String(securityLayer.decrypt(encryptedText), "UTF-8"));


                String strEncryptedText = Base64.encodeToString(encryptedText, Base64.URL_SAFE);
                System.out.println("Transmission message: " + strEncryptedText);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, strEncryptedText, sentPendingIntent, deliveredPendingIntent);
                Toast.makeText(this, "Sending SMS....", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
            }
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Internal Error while encrypting...", Toast.LENGTH_LONG).show();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Toast.makeText(this, "Internal Error while encrypting...", Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);

        return (check == PackageManager.PERMISSION_GRANTED);
    }

    public void scanQR(View view) {
        Intent intent = new Intent(this, QRScanner.class);
        startActivity(intent);
    }
}