package com.example.sw0b_001.ListPlatforms.Emails;

import androidx.annotation.RequiresApi;
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
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sw0b_001.R;
import com.example.sw0b_001.SecurityLayer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SendMessageActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.sw0b_001.MESSAGE";

    String SMS_SENT = "SENT";
    String SMS_DELIVERED = "DELIVERED";

    SecurityLayer securityLayer;
    KeyStore keyStore;

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);

        loadComposeInformation();
        loadPreviousMessages();
    }

    private void loadComposeInformation() {
        String email = getIntent().getStringExtra("receipientEmailAddress");
        String subject = getIntent().getStringExtra("emailSubject");
        String platform = getIntent().getStringExtra("platform_name");

        TextView tvEmail = findViewById(R.id.static_compose_to);
        TextView tvSubject = findViewById(R.id.static_compose_subject);
        TextView tvPlatform = findViewById(R.id.static_compose_platform);

        tvEmail.setText("Receipient's Email Address: " + email);
        tvSubject.setText("Email's Subject: " + subject);
        tvPlatform.setText("Sending via: " + platform);
    }

    private void loadPreviousMessages() {
        listView = findViewById(R.id.message_list);
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        try {
            listView.setAdapter(itemsAdapter);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickSend(View view) {
        TextView message = findViewById(R.id.edit_message);
        String text = message.getText().toString();

        itemsAdapter.add(text);
        itemsAdapter.notifyDataSetChanged();

        message.setText("");
    }

    public void smsFailed() {

    }


    /*
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

                //TODO: Research what to do in case of a double sim phone
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
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);

        return (check == PackageManager.PERMISSION_GRANTED);
    }
     */

}