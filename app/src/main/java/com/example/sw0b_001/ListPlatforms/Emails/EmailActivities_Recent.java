package com.example.sw0b_001.ListPlatforms.Emails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sw0b_001.PlatformsAdapter;
import com.example.sw0b_001.R;
import com.example.sw0b_001.SecurityLayer;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EmailActivities_Recent extends AppCompatActivity {

    ArrayList<String> items = new ArrayList<>();
    ArrayAdapter<String> itemsAdapter;
    KeyStore keyStore;
    ListView listView;
    RecyclerView recyclerView;
    SecurityLayer securityLayer;

    String SMS_SENT = "SENT";
    String SMS_DELIVERED = "DELIVERED";

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

        Intent intent = new Intent(this, EmailSendMessageActivity.class);
        intent.putExtra("platform_name", getIntent().getStringExtra("text1"));
        PlatformsAdapter platformsAdapter = new PlatformsAdapter(this, subjects, emails, images, intent);
        recyclerView.setAdapter(platformsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void composeEmail(View view) {
       startActivity(new Intent(this, EmailCompose.class));
    }


    public void sendMessage(View view) {

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


//        String number = eNumber.getText().toString();
//        String plainText = eText.getText().toString();
        String number = "";
        String plainText = "";
        if(plainText.isEmpty()) {
            Toast.makeText(this, "Text Cannot be empty!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            if(checkPermission(Manifest.permission.SEND_SMS)) {
                String IV = new String(securityLayer.getIV(), "UTF-8");
                String transmissionText = IV + ":" + plainText;
                byte[] encryptedText = securityLayer.encrypt_AES(transmissionText);

                System.out.println("Transmission String: " + transmissionText);
                System.out.println("[+] Decrypted: " + new String(securityLayer.decrypt_AES(encryptedText), "UTF-8"));


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
}