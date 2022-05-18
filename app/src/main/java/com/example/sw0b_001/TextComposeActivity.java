package com.example.sw0b_001;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

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
import com.example.sw0b_001.Security.SecurityHandler;

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
        setContentView(R.layout.activity_tweet_compose);

        Toolbar composeToolbar = (Toolbar) findViewById(R.id.tweet_toolbar);
        setSupportActionBar(composeToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        autoFocusKeyboard();
    }

    private void autoFocusKeyboard() {

        // Focus
        EditText tweetComposeEditText = findViewById(R.id.tweet_compose_text);
        tweetComposeEditText.postDelayed(new Runnable() {
            public void run() {
                tweetComposeEditText.requestFocus();

                tweetComposeEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                tweetComposeEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
            }
        }, 200);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.email_compose_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String processTextForEncryption(String platformLetter, String body) {
        return platformLetter + ":" + body;
    }

    public void onTweetButtonClick(View view) {
        EditText bodyEditText = findViewById(R.id.email_body);
        String body = bodyEditText.getText().toString();

        try {

            long platformId = getIntent().getLongExtra("platform_id", -1);
            Platform platform = PlatformsHandler.getPlatform(getApplicationContext(), platformId);
            String formattedContent = processTextForEncryption(platform.getLetter(), body);
            String encryptedContentBase64 = PublisherHandler.formatForPublishing(getApplicationContext(), formattedContent);
            String gatewayClientMSISDN = GatewayClientsHandler.getDefaultGatewayClientMSISDN(getApplicationContext());


            Intent defaultSMSAppIntent = SMSHandler.transferToDefaultSMSApp(gatewayClientMSISDN, encryptedContentBase64);
            if(defaultSMSAppIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(defaultSMSAppIntent);
                setResult(Activity.RESULT_OK, new Intent());

                EncryptedContentHandler.store(getApplicationContext(), encryptedContentBase64, gatewayClientMSISDN, platform.getName());
                finish();
            }

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
    }

}
