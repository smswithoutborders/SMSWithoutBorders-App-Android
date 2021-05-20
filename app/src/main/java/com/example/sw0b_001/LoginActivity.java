package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.sw0b_001.Helpers.CustomHelpers;
import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Helpers.SecurityLayer;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class LoginActivity extends AppCompatActivity {
    SecurityLayer securityLayer;
    private static final int REQUEST_CAMERA_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void validateInput(View view) {
        try {
            securityLayer = new SecurityLayer();
            EditText password = findViewById(R.id.user_password);
            if(!securityLayer.hasRSAKeys()) {
//                System.out.println("[+] Does not have RSA keys");
//                Log.d(MainActivity.class.getSimpleName(), )

                AccessPermissions();
            }
            else {
//                System.out.println(securityLayer.init());
                Platforms gmail = new Platforms()
                        .setName("Gmail")
                        .setProvider("google")
                        .setDescription("Made By Google")
                        .setImage(R.drawable.roundgmail)
                        .setType("email");

                EmailThreads emailThreads = new EmailThreads()
//                        .setImage(CustomHelpers.getLetterImage('i'))
                        .setRecipient("info@smswithoutborders.com")
                        .setSubject("Initial test")
                        .setMdate("2021-01-01");

                EmailMessage emailMessage = new EmailMessage()
                        .setBody("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. ")
                        .setDatetime("2020-01-01")
//                        .setImage(CustomHelpers.getLetterImage('i'))
                        .setStatus("delivered");

                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();

                PlatformDao platformsDao = platformDb.platformDao();
                EmailThreadsDao emailThreadsDao = platformDb.emailThreadDao();
                EmailMessageDao emailMessageDao = platformDb.emailDao();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        platformsDao.deleteAll();
                        emailThreadsDao.deleteAll();
                        emailMessageDao.deleteAll();

                        long platformId = platformsDao.insert(gmail);
                        emailThreads.setPlatformId(platformId);
                        long threadId = emailThreadsDao.insert(emailThreads);

                        emailThreads.setSubject("Second Initial Message");
                        emailThreads.setRecipient("sherlock@gmail.com");
                        long threadId2 = emailThreadsDao.insert(emailThreads);

                        emailMessage.setThreadId(threadId);
                        emailMessageDao.insertAll(emailMessage);
                        emailMessage.setThreadId(threadId2);
                        emailMessageDao.insertAll(emailMessage);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                thread.join();
                if(password.getText().toString().isEmpty()) {
                    password.setError("Password cannot be empty!");
                    return;
                }

                if(!securityLayer.authenticate(getApplicationContext(), password.getText().toString())) {
                    password.setError("Failed to authenticate!");
                }
                else {
//                    System.out.println("[+] Has RSA Keys....");
                    // TODO remove this when done
                    try {
                        KeyStore keyStore = KeyStore.getInstance(SecurityLayer.DEFAULT_KEYSTORE_PROVIDER);
                        keyStore.load(null);
                        keyStore.deleteEntry(SecurityLayer.DEFAULT_KEYSTORE_ALIAS);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AccessPlatforms();
                    finish();
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void AccessPlatforms() {
        Intent intent = new Intent(this, PlatformsActivity.class);
        startActivity(intent);
    }


    public void AccessPermissions() {
        Intent intent = new Intent(this, PermissionsActivity.class);
        startActivity(intent);
    }
}