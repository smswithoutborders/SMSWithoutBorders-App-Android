package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Helpers.GatewayValues;
import com.example.sw0b_001.Models.GatewayServers.GatewayServers;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Providers.Emails.EmailMessage;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PasswordActivity extends AppCompatActivity {
    SecurityHandler securityLayer;
    private static final int REQUEST_CAMERA_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            securityLayer = new SecurityHandler();
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

    public boolean cloudValidatePassword(byte[] encryptedPassword) {
       return false;
    }

    public void validateUsersCloudPassword(View view) throws IllegalBlockSizeException, InvalidKeyException, NoSuchAlgorithmException, BadPaddingException, IOException, CertificateException, KeyStoreException, InterruptedException, InvalidAlgorithmParameterException, UnrecoverableKeyException, NoSuchPaddingException {
        EditText passwordField = findViewById(R.id.user_password);
        SecurityHandler securityHandler = new SecurityHandler(getApplicationContext());

        if(passwordField.getText().toString().isEmpty()) {
            passwordField.setError("Password cannot be empty!");
            return;
        }

        if(getIntent().hasExtra("gatewayServerID")) {
            long gatewayServerId = getIntent().getLongExtra("gatewayServerID", -1);
            if(gatewayServerId == -1) {
                Log.e(getLocalClassName(), "GatewayServer ID is incorrect currently = -1");
            }
            else {
                Thread extractGatewayInformationThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Datastore databaseConnector = Room.databaseBuilder(getApplicationContext(),
                                Datastore.class, Datastore.DatabaseName).build();
                        GatewayServersDAO gatewayServersDAO = databaseConnector.gatewayServersDAO();
                        GatewayServers gatewayServer = gatewayServersDAO.getById(gatewayServerId);

                        byte[] passwordEncoded = passwordField.getText().toString().getBytes(StandardCharsets.UTF_8);
                        try {
                            byte[] RSAEncryptedPassword = securityHandler.encrypt_RSA(passwordEncoded, gatewayServer.getPublicKey());
                            Log.d(getLocalClassName(), "RSAEncryptedPassword: " + RSAEncryptedPassword);
                            if (cloudValidatePassword(RSAEncryptedPassword)) {
                                // TODO: return to sender
                                if (getIntent().hasExtra("callbackIntent")) {
                                    Object callbackIntent = getIntent().getExtras().get("callbackIntent");
                                    if (callbackIntent.getClass() == Intent.class) {
                                        startActivity((Intent) callbackIntent);
                                    }
                                }
                            } else {
                                passwordField.setError("Authentication Failed!");
                            }
                            return;
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                extractGatewayInformationThread.start();
                extractGatewayInformationThread.join();
                finish();
            }
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