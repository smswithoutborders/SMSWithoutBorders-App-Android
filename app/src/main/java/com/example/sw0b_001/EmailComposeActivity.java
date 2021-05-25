package com.example.sw0b_001;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.sw0b_001.Helpers.CustomHelpers;
import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Helpers.SecurityLayer;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Gateway.GatewayDao;
import com.example.sw0b_001.Providers.Gateway.GatewayPhonenumber;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class EmailComposeActivity extends AppCompatActivity {

    String SMS_SENT = "SENT";
    String SMS_DELIVERED = "DELIVERED";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    SecurityLayer securityLayer;
    long emailId;
    private List<GatewayPhonenumber> phonenumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_compose);

        Toolbar composeToolbar = (Toolbar) findViewById(R.id.compose_toolbar);
        setSupportActionBar(composeToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        if(!checkPermission(Manifest.permission.SEND_SMS)) {
//                String IV = new String(securityLayer.getIV(), "UTF-8");
//                String transmissionText = IV + ":" + body;
//                byte[] encryptedText = securityLayer.encrypt_AES(transmissionText);
//
//                System.out.println("Transmission String: " + transmissionText);
//                System.out.println("[+] Decrypted: " + new String(securityLayer.decrypt_AES(encryptedText), "UTF-8"));
//
//
//                String strEncryptedText = Base64.encodeToString(encryptedText, Base64.URL_SAFE);
//                System.out.println("Transmission message: " + strEncryptedText);
//
//            Snackbar.make(getWindow().getDecorView().getRootView(), "Sending SMS", Snackbar.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);

            Thread getPhonenumber = new Thread(new Runnable() {
                @Override
                public void run() {
                    Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                            Datastore.class, Datastore.DBName).build();
                    GatewayDao gatewayDao = platformDb.gatewayDao();
                    phonenumbers = gatewayDao.getAll();
                }
            });
        }

        try {
            securityLayer = new SecurityLayer();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView emailTo = findViewById(R.id.email_to);
        TextView emailSubject = findViewById(R.id.email_subject);
        if(getIntent().hasExtra("recipient") ) {
            emailTo.setText(getIntent().getStringExtra("recipient"));
        }
        if(getIntent().hasExtra("subject") ) {
            emailSubject.setText(getIntent().getStringExtra("subject"));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_compose_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText to = findViewById(R.id.email_to);
        EditText subject = findViewById(R.id.email_subject);
        EditText body = findViewById(R.id.email_body);
        switch (item.getItemId()) {
            case R.id.discard:
                startActivity(new Intent(this, EmailThreadsActivity.class));
                to.setText("");
                subject.setText("");
                body.setText("");
                finish();
                return true;

            case R.id.action_send:
                if(to.getText().toString().isEmpty()) {
                    to.setError("Recipient cannot be empty!");
                    return false;
                }
                if(subject.getText().toString().isEmpty()) {
                    subject.setError("Subject should not be empty!");
                    return false;
                }
                if(body.getText().toString().isEmpty()) {
                    body.setError("Body should not be empty!");
                    return false;
                }

                final long[] threadId = {getIntent().getLongExtra("thread_id", -1)};
                if(threadId[0] == -1) {
                    Thread storeEmailThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmailThreads emailThread = new EmailThreads()
                                    .setRecipient(to.getText().toString())
                                    .setSubject(subject.getText().toString())
                                    .setPlatformId(getIntent().getLongExtra("platform_id", -1));
                            Datastore emailStoreDb = Room.databaseBuilder(getApplicationContext(),
                                    Datastore.class, Datastore.DBName).build();

                            EmailThreadsDao platformsDao = emailStoreDb.emailThreadDao();
                            threadId[0] = platformsDao.insert(emailThread);
                        }
                    });

                    storeEmailThread.start();
                    try {
                        storeEmailThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Thread storeEmailMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EmailMessage emailMessage = new EmailMessage()
                                .setBody(body.getText().toString())
                                .setDatetime(CustomHelpers.getDateTime())
                                .setThreadId(threadId[0])
                                .setRecipient(to.getText().toString())
                                .setSubject(subject.getText().toString())
                                .setStatus("pending");
                        Datastore emailStoreDb = Room.databaseBuilder(getApplicationContext(),
                                Datastore.class, Datastore.DBName).build();

                        EmailMessageDao platformsDao = emailStoreDb.emailDao();
                        emailId = platformsDao.insertAll(emailMessage);
                    }
                });
                storeEmailMessage.start();
                try {
                    storeEmailMessage.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMessage();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void sendMessage() {
//        Toast.makeText(getBaseContext(), "SMS sending...", Toast.LENGTH_LONG).show();


        final List<EmailMessage>[] pendingMessagesList = new List[]{new ArrayList<>()};
        Thread storeEmailMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore emailStoreDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();

                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                pendingMessagesList[0] = platformsDao.getForStatus("pending");
            }
        });
        storeEmailMessage.start();
        try {
            storeEmailMessage.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        List<EmailMessage> pendingMessages = pendingMessagesList[0];
        // TODO: iterate and send every pending message
        long emailId = pendingMessages.get(0).getId();
        String recipient = pendingMessages.get(0).getRecipient();
        String body = pendingMessages.get(0).getBody();
        String subject = pendingMessages.get(0).getSubject();
        String phonenumber = "";
        for(GatewayPhonenumber number : phonenumbers) {
            if(number.isDefault())
                phonenumber = number.getNumber();
        }
        if(phonenumber.length() < 1 ) {
            Toast.makeText(this, "Default number could not be determined", Toast.LENGTH_LONG).show();
            return;
        }

        if(body.isEmpty()) {
            Toast.makeText(this, "Text Cannot be empty!", Toast.LENGTH_LONG).show();
            return;
        }

        CustomHelpers.sendEmailSMS(getBaseContext(), body, phonenumber, emailId);
        finish();
        // TODO: work out how the IV gets encrypted before sending

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);

        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
