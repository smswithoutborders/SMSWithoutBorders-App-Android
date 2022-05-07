package com.example.sw0b_001;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Helpers.CustomHelpers;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformDao;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayDao;
import com.example.sw0b_001.Providers.Text.TextMessage;
import com.example.sw0b_001.Providers.Text.TextMessageDao;
import com.example.sw0b_001.Security.SecurityHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
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

public class TextComposeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    SecurityHandler securityLayer;
    long textMessageId;
    private List<GatewayClient> phonenumbers = new ArrayList<>();
    private Platform platform;
    private long platformId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_compose);

        Toolbar composeToolbar = (Toolbar) findViewById(R.id.compose_toolbar);
        setSupportActionBar(composeToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
//        if(!checkPermission(Manifest.permission.SEND_SMS)) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
//        }
        platformId = getIntent().getLongExtra("platform_id", -1);

        try {
            Thread getPhonenumber = new Thread(new Runnable() {
                @Override
                public void run() {
                    Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                            Datastore.class, Datastore.DatabaseName).build();
                    GatewayDao gatewayDao = platformDb.gatewayDao();
                    phonenumbers = gatewayDao.getAll();

                    PlatformDao platformDao = platformDb.platformDao();
                    platform = platformDao.get(platformId);
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
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_compose_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText body = findViewById(R.id.email_body);

        switch (item.getItemId()) {
            /*
            case R.id.discard:
                startActivity(new Intent(this, EmailThreadsActivity.class));
//                to.setText("");
//                subject.setText("");
                body.setText("");
                finished_thread(null);
                return true;

             */

            case R.id.action_send:
                Thread storeEmailMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TextMessage textMessage = new TextMessage()
                                .setDatetime(CustomHelpers.getDateTime())
                                .setStatus("requested")
                                .setPlatformId(platformId)
                                .setImage(CustomHelpers.getLetterImage(body.getText().toString().charAt(0)))
                                .setBody(body.getText().toString());
                        Datastore textStoreDB = Room.databaseBuilder(getApplicationContext(),
                                Datastore.class, Datastore.DatabaseName).build();

                        TextMessageDao platformsDao = textStoreDB.textMessageDao();
                        textMessageId = platformsDao.insertAll(textMessage);
                    }
                });
                storeEmailMessage.start();
                try {
                    storeEmailMessage.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    sendMessage(body.getText().toString());
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


    private void sendMessage(String body) throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, UnrecoverableEntryException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, CertificateException, IOException {
//        Toast.makeText(getBaseContext(), "SMS sending...",yy Toast.LENGTH_LONG).show();
        String phonenumber = "";
        for(GatewayClient number : phonenumbers) {
//            Log.i(this.getLocalClassName(), "[+] Number: " + number.getNumber());
            if(number.isDefault())
                phonenumber = number.getMSISDN();
        }

        if(phonenumber.length() < 1 ) {
            Toast.makeText(this, "Default number could not be determined", Toast.LENGTH_LONG).show();
            return;
        }

//        body = formatForSMS(platforms.getProvider().toLowerCase(), platforms.getName().toLowerCase(), "send", body);
//            Log.i(this.getLocalClassName(), ">> Body: " + body);
        body = getEncryptedSMS(body);
//            Log.i(this.getLocalClassName(), ">> decrypted: " + new String(securityLayer.decrypt_AES(Base64.decode(body.getBytes(), Base64.DEFAULT))));
//            Log.i(this.getLocalClassName(), ">> iv: " + new String(securityLayer.getIV()));
//            byte[] byte_encryptedIv = securityLayer.encrypt_AES(securityLayer.getIV(), passwdHash.getBytes());
//            byte[] fullmessage = securityLayer.encrypt_AES((new String(securityLayer.getIV()) + "_" + body), passwdHash.getBytes("UTF-8"));
        // body = new String(securityLayer.getIV()) + body;
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

    private String formatForSMS(String provider, String platform, String protocol, String body) throws UnsupportedEncodingException {
       // Gmail = to:subject:body
        // TODO: put platform and protocol
        // return provider + ":" + platform + ":" + protocol + ":" + to + ":" + subject + ":" + body;
        return provider + ":" + platform + ":" + protocol + ":" + body;
    }

    private void finished_thread(Intent intent) {
         intent.putExtra("platform_id", platformId);
        if (intent.resolveActivity(getPackageManager()) != null ) {
            startActivity(intent);
            setResult(Activity.RESULT_OK, new Intent());
            finish();
        }
        else {
            Toast.makeText(this, "Could not transfer to default app", Toast.LENGTH_SHORT).show();
            Log.i(this.getLocalClassName(), "isPackageManager= " + intent.resolveActivity(getPackageManager()));
        }
    }

    private String getEncryptedSMS(String data) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnrecoverableEntryException, KeyStoreException, CertificateException, IOException {
        String randString = securityLayer.generateRandom(16);
//        Log.i(this.getLocalClassName(), ">> Rand string: " + randString);
        // byte[] encryptedData = securityLayer.encrypt_AES(data, randString.getBytes());
        return "";
        // return Base64.encodeToString(encryptedData, Base64.NO_WRAP);
    }

}
