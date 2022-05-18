package com.example.sw0b_001;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentHandler;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformsHandler;
import com.example.sw0b_001.Models.PublisherHandler;
import com.example.sw0b_001.Models.SMSHandler;

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
    long emailId;
    private List<GatewayClient> phonenumbers = new ArrayList<>();
    private Platform platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_compose);

        Toolbar composeToolbar = (Toolbar) findViewById(R.id.tweet_toolbar);
        setSupportActionBar(composeToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_compose_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText toEditText = findViewById(R.id.email_to);
        EditText ccEditText = findViewById(R.id.email_cc);
        EditText bccEditText = findViewById(R.id.email_bcc);
        EditText subjectEditText = findViewById(R.id.email_subject);
        EditText bodyEditText = findViewById(R.id.email_body);

        switch (item.getItemId()) {
            case R.id.action_send:
                String to = toEditText.getText().toString();
                String cc = ccEditText.getText().toString();
                String bcc = bccEditText.getText().toString();
                String body = bodyEditText.getText().toString();
                String subject = subjectEditText.getText().toString();

                if(to.isEmpty()) {
                    toEditText.setError("Recipient cannot be empty!");
                    return false;
                }
                if(body.isEmpty()) {
                    bodyEditText.setError("Body should not be empty!");
                    return false;
                }

                try {

                    long platformId = getIntent().getLongExtra("platform_id", -1);
                    Platform platform = PlatformsHandler.getPlatform(getApplicationContext(), platformId);
                    String formattedContent = processEmailForEncryption(platform.getLetter(), to, cc, bcc, subject, body);
                    String encryptedContentBase64 = PublisherHandler.formatForPublishing(getApplicationContext(), formattedContent);
                    String gatewayClientMSISDN = GatewayClientsHandler.getDefaultGatewayClientMSISDN(getApplicationContext());


                    Intent defaultSMSAppIntent = SMSHandler.transferToDefaultSMSApp(gatewayClientMSISDN, encryptedContentBase64);
                    if(defaultSMSAppIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(defaultSMSAppIntent);
                        setResult(Activity.RESULT_OK, new Intent());

                        EncryptedContentHandler.store(getApplicationContext(), encryptedContentBase64, gatewayClientMSISDN, platform.getName());
                        finish();
                    }

                    return true;

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
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return false;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private String processEmailForEncryption(String platformLetter, String to, String cc, String bcc, String subject, String body) throws Throwable {
        String emailContent = platformLetter + ":" + to + ":" + cc + ":" + bcc + ":" + subject + ":" + body;
        return emailContent;
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
}
