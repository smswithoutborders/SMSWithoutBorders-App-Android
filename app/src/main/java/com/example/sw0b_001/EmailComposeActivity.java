package com.example.sw0b_001;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.sw0b_001.Helpers.CustomHelpers;
import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Gateway.GatewayDao;
import com.example.sw0b_001.Providers.Gateway.GatewayPhonenumber;
import com.example.sw0b_001.Models.Platforms.PlatformDao;
import com.example.sw0b_001.Models.Platforms.Platforms;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    SecurityHandler securityLayer;
    long emailId;
    private List<GatewayPhonenumber> phonenumbers = new ArrayList<>();
    private Platforms platforms;
    private long threadId;


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
//        if(!checkPermission(Manifest.permission.SEND_SMS)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
//        }
        long platformId = getIntent().getLongExtra("platform_id", -1);

        try {
            Thread getPhonenumber = new Thread(new Runnable() {
                @Override
                public void run() {
                    Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                            Datastore.class, Datastore.DatabaseName).build();
                    GatewayDao gatewayDao = platformDb.gatewayDao();
                    phonenumbers = gatewayDao.getAll();

                    PlatformDao platformDao = platformDb.platformDao();
                    platforms = platformDao.get(platformId);
                }
            });
            getPhonenumber.start();
            try {
                getPhonenumber.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            securityLayer = new SecurityHandler(getApplicationContext());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView emailTo = findViewById(R.id.email_cc);
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
        EditText to = findViewById(R.id.email_cc);
        EditText subject = findViewById(R.id.email_subject);
        EditText body = findViewById(R.id.email_body);
        switch (item.getItemId()) {
            case R.id.discard:
                startActivity(new Intent(this, EmailThreadsActivity.class));
                to.setText("");
                subject.setText("");
                body.setText("");
                finished_thread(null);
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
//                item.setEnabled(false);

                threadId = getIntent().getLongExtra("thread_id", -1);
                if(threadId == -1) {
                    Thread storeEmailThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmailThreads emailThread = new EmailThreads()
                                    .setRecipient(to.getText().toString())
                                    .setSubject(subject.getText().toString())
                                    .setPlatformId(getIntent().getLongExtra("platform_id", -1));
                            Datastore emailStoreDb = Room.databaseBuilder(getApplicationContext(),
                                    Datastore.class, Datastore.DatabaseName).build();

                            EmailThreadsDao platformsDao = emailStoreDb.emailThreadDao();
                            threadId = platformsDao.insert(emailThread);
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
                                .setThreadId(threadId)
                                .setRecipient(to.getText().toString())
                                .setSubject(subject.getText().toString())
                                .setStatus("requested");
                        Datastore emailStoreDb = Room.databaseBuilder(getApplicationContext(),
                                Datastore.class, Datastore.DatabaseName).build();

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
                    sendMessage(to.getText().toString(), subject.getText().toString(), body.getText().toString());
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


    private void sendMessage(String recipient, String subject, String body) throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, UnrecoverableEntryException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, CertificateException, IOException {
//        Toast.makeText(getBaseContext(), "SMS sending...",yy Toast.LENGTH_LONG).show();
        String phonenumber = "";
        for(GatewayPhonenumber number : phonenumbers) {
//            Log.i(this.getLocalClassName(), "[+] Number: " + number.getNumber());
            if(number.isDefault())
                phonenumber = number.getCountryCode() + number.getNumber();
        }

        if(phonenumber.length() < 1 ) {
            Toast.makeText(this, "Default number could not be determined", Toast.LENGTH_LONG).show();
            return;
        }

//        body = formatForEmail(platforms.getProvider().toLowerCase(), platforms.getName().toLowerCase(), "send", recipient, subject, body);
//            Log.i(this.getLocalClassName(), ">> Body: " + body);
        body = getEncryptedSMS(body);
//            Log.i(this.getLocalClassName(), ">> decrypted: " + new String(securityLayer.decrypt_AES(Base64.decode(body.getBytes(), Base64.DEFAULT))));
//            Log.i(this.getLocalClassName(), ">> iv: " + new String(securityLayer.getIV()));
//            byte[] byte_encryptedIv = securityLayer.encrypt_AES(securityLayer.getIV(), passwdHash.getBytes());
//            byte[] fullmessage = securityLayer.encrypt_AES((new String(securityLayer.getIV()) + "_" + body), passwdHash.getBytes("UTF-8"));
        body = new String(securityLayer.getIV()) + body;
//            body = Base64.encodeToString(fullmessage, Base64.DEFAULT);
//            Log.i(this.getLocalClassName(), "[+] Transmission data: " + body);
//            CustomHelpers.sendEmailSMS(getBaseContext(), body, phonenumber, emailId);
        Intent intent = sendSMSMessageIntent(body, phonenumber);
        finished_thread(intent);
        // TODO: work out how the IV gets encrypted before sending

    }

    public Intent sendSMSMessageIntent(String text, String phonenumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+phonenumber));
        intent.putExtra("sms_body", text);

        return intent;
    }

    private String formatForEmail(String provider, String platform, String protocol, String to, String subject, String body) throws UnsupportedEncodingException {
       // Gmail = to:subject:body
        // TODO: put platform and protocol
        return provider + ":" + platform + ":" + protocol + ":" + to + ":" + subject + ":" + body;
    }

    private void finished_thread(Intent intent) {
        if(threadId != -1) {
            boolean intentCameNull = false;
            if( intent == null ) {
                intentCameNull = true;
                intent = new Intent(this, EmailThreadActivity.class);
            }
            intent.putExtra("thread_id", threadId);
            intent.putExtra("platform_id", threadId);
            if (!intentCameNull && intent.resolveActivity(getPackageManager()) != null ) {
                startActivity(intent);
                setResult(Activity.RESULT_OK, new Intent());
                finish();
            }
            else {
                Toast.makeText(this, "Could not transfer to default app", Toast.LENGTH_SHORT).show();
                Log.i(this.getLocalClassName(), "IntentCameNull= " + intentCameNull);
                Log.i(this.getLocalClassName(), "isPackageManager= " + intent.resolveActivity(getPackageManager()));
            }
        }
        else {
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
    }

    private String getEncryptedSMS(String data) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnrecoverableEntryException, KeyStoreException, CertificateException, IOException {
        String randString = securityLayer.generateRandom(16);
//        Log.i(this.getLocalClassName(), ">> Rand string: " + randString);
        byte[] encryptedData = securityLayer.encrypt_AES(data, randString.getBytes());
        return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
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
