package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentHandler;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformDao;
import com.example.sw0b_001.Models.Platforms.PlatformsHandler;
import com.example.sw0b_001.Models.User.UserHandler;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityHelpers;
import com.example.sw0b_001.Security.SecurityRSA;
import com.example.sw0b_001.databinding.ActivitySyncInitBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;

public class SyncHandshakeActivity extends AppCompactActivityCustomized {

    private ActivitySyncInitBinding binding;
    private final String SYNC_KEY = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySyncInitBinding.inflate(getLayoutInflater());
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
                long gatewayServerId = getIntent().getLongExtra("gateway_server_id", -1);

                processHandshakePayload(jsonObject, gatewayServerId);

                Intent dashboardIntent = new Intent(getApplicationContext(), SplashActivity.class);
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
        GatewayClientsHandler.remoteFetchAndStoreGatewayClients(getApplicationContext());
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
            JSONArray platforms = jsonObject.getJSONObject("user_platforms")
                    .getJSONArray("saved_platforms");

//            String gatewayServerSeedsUrl = jsonObject.getString("seeds_url");

            processAndUpdateGatewayServerSeedUrl(getString(R.string.default_seeds_url), gatewayServerId);
            processAndStoreSharedKey(sharedKey);
            processAndStorePlatforms(platforms);
            remoteFetchAndStoreGatewayClients(getString(R.string.default_seeds_url));

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


    private PublicKey updateSharedKeyEncryption(String gatewayServerUrlHost) throws Throwable {
        SecurityRSA securityRSA = new SecurityRSA(this);
        byte[] sharedKey = SecurityHelpers.getDecryptedSharedKey(this);
        Log.d(getLocalClassName(), "Decrypted shared key: " + sharedKey);

        PublicKey publicKeyEncoded = getNewPublicKey(gatewayServerUrlHost);
        byte[] encryptedSharedKey = securityRSA.encrypt( sharedKey, publicKeyEncoded, SecurityHandler.decryptionDigestParam);

        SecurityHandler securityHandler = new SecurityHandler(this);
        String encryptedSharedKeyB64 = Base64.encodeToString(encryptedSharedKey, Base64.DEFAULT);
        securityHandler.storeSharedKey(encryptedSharedKeyB64);

        return publicKeyEncoded;
    }

    private PublicKey getNewPublicKey(String gatewayServerUrlHost) throws GeneralSecurityException, IOException {
        SecurityRSA securityRSA = new SecurityRSA(this);
        String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServerUrlHost );
        PublicKey publicKeyEncoded = securityRSA.generateKeyPair(keystoreAlias)
                .generateKeyPair()
                .getPublic();

        return publicKeyEncoded;
    }

    public void publicKeyExchange(String gatewayServerHandshakeUrl) {
        try {
            SecurityHandler securityHandler = new SecurityHandler(this);

            URL gatewayServerUrl = new URL(gatewayServerHandshakeUrl);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(getLocalClassName(), "gateway server: " + gatewayServerUrl);
                        String gatewayServerHost = gatewayServerUrl.getProtocol() + "://" +
                                gatewayServerUrl.getHost();

                        String gatewayServerUrlHost = gatewayServerUrl.getHost();
                        // Extracting and storing userId from gatewayServerHandshake
                        int userIdIndex =4;
                        String userId = gatewayServerUrl.getPath().split("/")[userIdIndex];
                        UserHandler userHandler = new UserHandler(getApplicationContext(), userId);
                        userHandler.commitUser();

                        PublicKey gatewayServerPublicKey = getGatewayServerPublicKey(gatewayServerHost);
                        // TODO: requires testing if re-encryption of key works
                        PublicKey publicKeyEncoded = (securityHandler.hasSharedKey() && gatewayServerPublicKey != null) ?
                                updateSharedKeyEncryption(gatewayServerUrlHost) :
                                getNewPublicKey(gatewayServerUrlHost);

                        GatewayServer gatewayServer = new GatewayServer();
                        gatewayServer.setPublicKey(Base64.encodeToString(gatewayServerPublicKey.getEncoded(), Base64.DEFAULT));

                        gatewayServer.setUrl(gatewayServerUrlHost);

                        String gatewayServerUrlProtocol = new URL(gatewayServerHandshakeUrl).getProtocol();
                        gatewayServer.setProtocol(gatewayServerUrlProtocol);

                        Integer gatewayServerUrlPort = new URL(gatewayServerHandshakeUrl).getPort();
                        gatewayServer.setPort(gatewayServerUrlPort);

                        GatewayServersHandler gatewayServersHandler = new GatewayServersHandler(getApplicationContext());
                        long gatewayServerId = gatewayServersHandler.add(gatewayServer);

                        String PEMPublicKey = SecurityHelpers.convert_to_pem_format(publicKeyEncoded.getEncoded());
                        Intent passwordActivityIntent = new Intent(getApplicationContext(), PasswordActivity.class);

                        Intent syncHandshakeIntent = new Intent(getApplicationContext(), SyncHandshakeActivity.class);
                        if(BuildConfig.DEBUG)
                            Log.d(getLocalClassName(), getPackageName());
                        syncHandshakeIntent.setPackage(getPackageName());
                        syncHandshakeIntent.putExtra("gateway_server_id", gatewayServerId);
                        syncHandshakeIntent.putExtra("state", "complete_handshake");

                        passwordActivityIntent.putExtra("callbackIntent", syncHandshakeIntent);
                        passwordActivityIntent.putExtra("user_id", userId);
                        passwordActivityIntent.putExtra("public_key", PEMPublicKey);
                        passwordActivityIntent.putExtra("gateway_server_url", gatewayServerHandshakeUrl);
                        passwordActivityIntent.putExtra("gateway_server_public_key", gatewayServerPublicKey);

                        startActivity(passwordActivityIntent);
                        finish();
                    } catch(Exception e ) {
                        e.printStackTrace();
                        finish();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (KeyStoreException | NoSuchProviderException | CertificateException | NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private PublicKey getGatewayServerPublicKey(String gatewayServerUrl) throws IOException, InterruptedException {
        String primaryKeySite = new String();
        Log.d(getLocalClassName(), "Acquiring pub key: " + gatewayServerUrl);
        /*
        if(BuildConfig.DEBUG)
            primaryKeySite = getString(R.string.official_staging_site);
        else
            primaryKeySite = getString(R.string.official_site);

         */
        Log.d(getLocalClassName(), primaryKeySite);

        URL url = new URL(gatewayServerUrl);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        Certificate[] certificate = urlConnection.getServerCertificates();
//                        for(Certificate certificate: certificates[0]) {
//                            PublicKey publicKey = certificate.getPublicKey();
//                            Log.d(getLocalClassName(), "Cert det: " +
//                                    Base64.encodeToString(publicKey.getEncoded(), Base64.NO_PADDING) +
//                                    certificate.getType() );
//                        }
        return certificate[0].getPublicKey();
    }
}