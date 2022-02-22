package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Helpers.GatewayValues;
import com.example.sw0b_001.Helpers.SecurityLayer;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class LoginActivity extends AppCompatActivity {
    SecurityLayer securityLayer;
    private static final int REQUEST_CAMERA_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
    }

    public void populateDB() throws InterruptedException {
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
                Datastore.class, Datastore.DatabaseName).build();

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
    }

    public void clear_rsa() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(SecurityLayer.DEFAULT_KEYSTORE_PROVIDER);
        keyStore.load(null);
        keyStore.deleteEntry(SecurityLayer.DEFAULT_KEYSTORE_ALIAS);
    }


    public void validateInput(View view) throws IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, BadPaddingException, IOException, CertificateException, KeyStoreException, InterruptedException {
        EditText password = findViewById(R.id.user_password);
        String sharedKey = getIntent().getStringExtra("shared_key");
        String publicKey = getIntent().getStringExtra("public_key");
        String passwdHash = getIntent().getStringExtra("password_hash");
        Map<Integer, List<String>>[] extractedInformation = (Map<Integer, List<String>>[]) getIntent().getSerializableExtra("platforms");

        SecurityLayer securityLayer = new SecurityLayer(getApplicationContext());
        if(password.getText().toString().isEmpty()) {
            password.setError("Password cannot be empty!");
            return;
        }
        if(!securityLayer.authenticate(password.getText().toString(), securityLayer.decrypt_RSA(passwdHash.getBytes()))) {
            password.setError("Failed to authenticate!");
        }
        else {
            if(sharedKey != null && !sharedKey.isEmpty() && publicKey != null && !publicKey.isEmpty()) {
                storeGatewayInformation(extractedInformation, passwdHash, publicKey, sharedKey);
            }

            startActivity(new Intent(this, PlatformsActivity.class));
            finish();
        }

    }

    private void storeGatewayInformation(Map<Integer, List<String>>[] extractedInformation, String passwdHash, String publicKey, String sharedKey) throws InterruptedException {
        Map<Integer, List<String>> providers = extractedInformation[0];
        Map<Integer, List<String>> provider_platforms_map = extractedInformation[1];

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = app_preferences.edit();
        editor.putString(GatewayValues.VAR_PUBLICKEY, publicKey);
        editor.putString(GatewayValues.SHARED_KEY, sharedKey);
//                editor.putString(GatewayValues.VAR_PASSWDHASH, passwdHash);
        storePlatformFromGateway(providers, provider_platforms_map);
        editor.putString(GatewayValues.VAR_PASSWDHASH, passwdHash);
        editor.commit();
    }

    public void storePlatformFromGateway(Map<Integer, List<String>> providers, Map<Integer, List<String>> platforms) throws InterruptedException {
        Thread storeProviders = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore dbConnector = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DatabaseName).build();
                PlatformDao providerDao = dbConnector.platformDao();
                EmailMessageDao emailMessageDao = dbConnector.emailDao();
                providerDao.deleteAll();
                emailMessageDao.deleteAll();
                for(int i=0;i<providers.size();++i) {
                    Platforms provider = new Platforms()
                            .setName(platforms.get(i).get(0))
                            .setDescription(providers.get(i).get(1))
                            .setProvider(providers.get(i).get(0))
                            .setType(platforms.get(i).get(1));
                    if(provider.getName().toLowerCase().equals("gmail") && provider.getProvider().toLowerCase().equals("google"))
                        provider.setImage(R.drawable.roundgmail);
                    else if(provider.getName().toLowerCase().equals("twitter") && provider.getProvider().toLowerCase().equals("twitter"))
                        provider.setImage(R.drawable.roundtwitter);
                    providerDao.insert(provider);
                }
            }
        });
        storeProviders.start();
        storeProviders.join();
    }
}