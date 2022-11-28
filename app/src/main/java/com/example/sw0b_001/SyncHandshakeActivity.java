package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentHandler;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformDao;
import com.example.sw0b_001.Models.Platforms.PlatformsHandler;
import com.example.sw0b_001.Models.User.UserHandler;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityHelpers;
import com.example.sw0b_001.Security.SecurityRSA;
import com.example.sw0b_001.databinding.ActivitySyncProcessingBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;

public class SyncHandshakeActivity extends AppCompactActivityCustomized {

    private ActivitySyncProcessingBinding binding;
    private final String SYNC_KEY = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyncProcessingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(!getIntent().hasExtra(SYNC_KEY)) {
            finish();
        }

        String state = getIntent().getStringExtra(SYNC_KEY);
        if(state.equals("complete_handshake")) {

            try {
                EncryptedContentHandler.clearedStoredEncryptedContents(getApplicationContext());
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            try {
                JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("payload"));
                long gatewayServerId = getIntent().getLongExtra("gatewayserver_id", -1);

                processHandshakePayload(jsonObject, gatewayServerId);

                Intent dashboardIntent = new Intent(getApplicationContext(), HomepageActivity.class);
                startActivity(dashboardIntent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        else {
            publicKeyExchange(state);
        }
    }

    private void remoteFetchAndStoreGatewayClients(String gatewayServerSeedsUrl) throws InterruptedException {
        GatewayClientsHandler.remoteFetchAndStoreGatewayClients(getApplicationContext(), gatewayServerSeedsUrl, null);
    }

    private void processAndStoreSharedKey(String sharedKey) throws GeneralSecurityException, IOException {
        SecurityHandler securityHandler = new SecurityHandler(getApplicationContext());
        securityHandler.storeSharedKey(sharedKey);
    }

    private void processAndUpdateGatewayServerSeedUrl(String gatewayServerSeedsUrl, long gatewayServerId) throws InterruptedException {
        GatewayServersHandler gatewayServersHandler = new GatewayServersHandler(getApplicationContext());
        gatewayServersHandler.updateSeedsUrl(gatewayServerSeedsUrl, gatewayServerId);
    }

    public void processHandshakePayload(JSONObject jsonObject, long gatewayServerId) throws Exception {
        try {
            String sharedKey = jsonObject.getString("shared_key");
            JSONArray platforms = jsonObject.getJSONArray("user_platforms");

            String gatewayServerSeedsUrl = jsonObject.getString("seeds_url");

            processAndUpdateGatewayServerSeedUrl(gatewayServerSeedsUrl, gatewayServerId);
            processAndStoreSharedKey(sharedKey);
            processAndStorePlatforms(platforms);
            remoteFetchAndStoreGatewayClients(gatewayServerSeedsUrl);

        } catch (JSONException | InterruptedException | CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
            throw new Exception(e);
        }
    }

    private void processAndStorePlatforms(JSONArray platforms) throws JSONException, InterruptedException {
        Thread insertPlatformThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnector = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DatabaseName)
                        .fallbackToDestructiveMigration()
                        .build();
                PlatformDao platformDao = databaseConnector.platformDao();
                platformDao.deleteAll();

                for(int i=0; i< platforms.length(); ++i ) {
                    try {
                        JSONObject JSONPlatform = platforms.getJSONObject(i);

                        Platform platform = new Platform();
                        platform.setName(JSONPlatform.getString("name"));
                        platform.setDescription(JSONPlatform.getString("description"));
                        platform.setType(JSONPlatform.getString("type"));
                        platform.setLetter(JSONPlatform.getString("letter"));

                        // long logoDownloadId = downloadLogoOnline(JSONPlatform.getString("logo"), JSONPlatform.getString("name"));

                        long logoDownloadId = PlatformsHandler.hardGetLogoByName(getApplicationContext(), platform.getName());
                        platform.setLogo(logoDownloadId);

                        platformDao.insert(platform);
                    }
                    catch(JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        insertPlatformThread.start();
        insertPlatformThread.join();
    }


    private void updateSharedKeyEncryption(PublicKey publicKey) throws Throwable {
        SecurityRSA securityRSA = new SecurityRSA(this);
        byte[] sharedKey = SecurityHelpers.getDecryptedSharedKey(this);
        byte[] encryptedSharedKey = securityRSA.encrypt( sharedKey, publicKey);

        SecurityHandler securityHandler = new SecurityHandler(this);
        securityHandler.storeSharedKey(String.valueOf(encryptedSharedKey));
    }

    public void publicKeyExchange(String gatewayServerHandshakeUrl) {
        try {
            SecurityRSA securityRSA = new SecurityRSA(this);
            SecurityHandler securityHandler = new SecurityHandler(this);

            URL gatewayServerUrl = new URL(gatewayServerHandshakeUrl);
            String gatewayServerUrlHost = gatewayServerUrl.getHost();

            // Extracting and storing userId from gatewayServerHandshake
            int userIdIndex =4;
            String userId = gatewayServerUrl.getPath().split("/")[userIdIndex];
            UserHandler userHandler = new UserHandler(getApplicationContext(), userId);
            userHandler.commitUser();

            String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServerUrlHost );
            PublicKey publicKeyEncoded = securityRSA.generateKeyPair(keystoreAlias)
                    .generateKeyPair()
                    .getPublic();

            // TODO: requires testing if re-encryption of key works
            if(securityHandler.hasSharedKey())
                updateSharedKeyEncryption(publicKeyEncoded);

            String PEMPublicKey = SecurityHelpers.convert_to_pem_format(publicKeyEncoded.getEncoded());
            Intent passwordActivityIntent = new Intent(getApplicationContext(), PasswordActivity.class);

            Intent syncHandshakeIntent = new Intent(getApplicationContext(), SyncHandshakeActivity.class);
            syncHandshakeIntent.putExtra("state", "complete_handshake");

            passwordActivityIntent.putExtra("callbackIntent", syncHandshakeIntent);
            passwordActivityIntent.putExtra("user_id", userId);
            passwordActivityIntent.putExtra("public_key", PEMPublicKey);

            startActivity(passwordActivityIntent);
            finish();
        } catch (KeyStoreException | NoSuchProviderException | CertificateException | NoSuchAlgorithmException | IOException | JSONException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}