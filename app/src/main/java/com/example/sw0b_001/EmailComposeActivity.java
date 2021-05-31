package com.example.sw0b_001;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.sw0b_001.Helpers.GatewayValues;
import com.example.sw0b_001.Helpers.SecurityLayer;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Gateway.GatewayDao;
import com.example.sw0b_001.Providers.Gateway.GatewayPhonenumber;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EmailComposeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    SecurityLayer securityLayer;
    long emailId;
    private List<GatewayPhonenumber> phonenumbers = new ArrayList<>();
    Intent returnIntent;

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        try {
            Thread getPhonenumber = new Thread(new Runnable() {
                @Override
                public void run() {
                    Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                            Datastore.class, Datastore.DBName).build();
                    GatewayDao gatewayDao = platformDb.gatewayDao();
                    phonenumbers = gatewayDao.getAll();
                }
            });
            getPhonenumber.start();
            try {
                getPhonenumber.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            securityLayer = new SecurityLayer(getApplicationContext());
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
        if(getIntent().hasExtra("thread_id")) {
            returnIntent = new Intent(this, EmailThreadActivity.class);
            returnIntent.putExtra("thread_id", getIntent().getLongExtra("thread_id", -1));
        }
        else {
            returnIntent = new Intent(this, EmailThreadsActivity.class);
            returnIntent.putExtra("platform_id", getIntent().getLongExtra("platform_id", -1));
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
                finished_thread();
                return true;

            case R.id.action_send:
                item.setEnabled(false);
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
                try {
                    sendMessage();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnrecoverableEntryException e) {
                    e.printStackTrace();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void sendMessage() throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, UnrecoverableEntryException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, CertificateException, IOException {
//        Toast.makeText(getBaseContext(), "SMS sending...", Toast.LENGTH_LONG).show();
        String phonenumber = "";
        for(GatewayPhonenumber number : phonenumbers) {
            Log.i(this.getLocalClassName(), "[+] Number: " + number.getNumber());
            if(number.isDefault())
                phonenumber = number.getNumber();
        }

        if(phonenumber.length() < 1 ) {
            Toast.makeText(this, "Default number could not be determined", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(this.getLocalClassName(), "[+] Phonenumber: " + phonenumber);

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String passwdHash = preferences.getString(GatewayValues.VAR_PASSWDHASH, null);
        passwdHash = new String(securityLayer.decrypt_RSA(passwdHash.getBytes())).substring(0, 16);
        List<EmailMessage> pendingMessages = pendingMessagesList[0];
        Log.i(this.getLocalClassName(), "# PENDING: " + pendingMessages.size());
        for(EmailMessage emailMessage : pendingMessages) {
            long emailId = emailMessage.getId();
            String recipient = emailMessage.getRecipient();
            String body = emailMessage.getBody();
            String subject = emailMessage.getSubject();

            body = formatForEmail(recipient, subject, body);
            Log.i(this.getLocalClassName(), ">> Body: " + body);
            body = getEncryptedSMS(body);
//            Log.i(this.getLocalClassName(), ">> decrypted: " + new String(securityLayer.decrypt_AES(Base64.decode(body, Base64.DEFAULT))));
//            Log.i(this.getLocalClassName(), ">> iv: " + Base64.encodeToString(securityLayer.getIV(), Base64.DEFAULT));
            byte[] byte_encryptedIv = securityLayer.encrypt_AES(securityLayer.getIV(), passwdHash.getBytes());
            String encryptedIv = Base64.encodeToString(byte_encryptedIv, Base64.DEFAULT).trim();
            body = encryptedIv + "_" + body;
            Log.i(this.getLocalClassName(), "[+] Transmission data: " + body);
            CustomHelpers.sendEmailSMS(getBaseContext(), body, phonenumber, emailId);
        }
        finished_thread();
        // TODO: work out how the IV gets encrypted before sending

    }

    private String formatForEmail(String to, String subject, String body) {
       // Gmail = to:subject:body
        // TODO: put platform and protocol
        return to + ":" + subject + ":" + body;
    }

    private void finished_thread() {
       startActivity(returnIntent);
       finish();
    }

    private String getEncryptedSMS(String data) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnrecoverableEntryException, KeyStoreException, CertificateException, IOException {
        String randString = securityLayer.generateRandom(16);
//        Log.i(this.getLocalClassName(), ">> Rand string: " + randString);
        byte[] encryptedData = securityLayer.encrypt_AES(data, randString.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
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
