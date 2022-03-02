package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.User.UserHandler;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Models.GatewayServers.GatewayServers;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Providers.Gateway.GatewayPhonenumber;
import com.example.sw0b_001.Providers.Gateway.GatewayDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SyncHandshakeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_processing);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String state = getIntent().getStringExtra("state");
        if(state.equals("complete_handshake")) {
            Log.d(this.getLocalClassName(), "Completing handshake");

            Serializable serializable = getIntent().getSerializableExtra("payload");
            JSONObject jsonObjectPayload = (JSONObject) serializable;

            // TODO: extract the contents of the object
            Log.d(getLocalClassName(), "Completed handshake and information is stored");
        }
        else {
            Log.d(this.getLocalClassName(), "Going to public key exchange");
            publicKeyExchange(state);
        }
    }

    public void publicKeyExchange(String gatewayServerHandshakeUrl) {
        Log.d(getLocalClassName(), gatewayServerHandshakeUrl);
        try {
            SecurityHandler securityLayer = new SecurityHandler();

            URL gatewayServerUrl = new URL(gatewayServerHandshakeUrl);
            String gatewayServerUrlHost = gatewayServerUrl.getHost();

            // Extracting and storing userId from gatewayServerHandshake
            int userIdIndex =4;
            String userId = gatewayServerUrl.getPath().split("/")[userIdIndex];
            Log.d(getLocalClassName(), "userId: " + userId);
            UserHandler userHandler = new UserHandler(getApplicationContext(), userId);
            userHandler.commitUser();

            String keystoreAlias = gatewayServerUrlHost + "-keystore-alias";
            Log.d(getLocalClassName(), "keystoreAlias: " + keystoreAlias);
            PublicKey publicKeyEncoded = securityLayer.generateKeyPair(keystoreAlias)
                    .generateKeyPair()
                    .getPublic();

            String publicKeyBase64 = Base64.encodeToString(publicKeyEncoded.getEncoded(), Base64.DEFAULT);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JSONObject jsonBody = new JSONObject("{\"public_key\": \"" + publicKeyBase64 + "\"}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(gatewayServerHandshakeUrl, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        /*
                        - Should receive server's public key
                            - should store the key after receiving it
                        - Would need to send password to the server after that
                        - Would receive the shared key if authentication is available
                         */

                        // TODO: change from "pd" to "public_key"
                        String gatewayServerPublicKey = response.getString("public_key");
                        Log.d(getLocalClassName(), "Gateway server public key: " + gatewayServerPublicKey);

                        String gatewayServerVerifyUrl = response.getString("verification_url");
                        Log.d(getLocalClassName(), "Verify URL: " + gatewayServerVerifyUrl);

                        // Formatting public key to work well from here
                        // TODO: check to make sure this is working
                        gatewayServerPublicKey = gatewayServerPublicKey.replace("-----BEGIN PUBLIC KEY-----\n", "");
                        gatewayServerPublicKey = gatewayServerPublicKey.replace("-----END PUBLIC KEY-----", "");

                        // Log.d(getLocalClassName(), "Server public key: " + serverPublicKey);
                        GatewayServers gatewayServer = new GatewayServers();
                        gatewayServer.setPublicKey(gatewayServerPublicKey);

                        String gatewayServerUrlHost = new URL(gatewayServerHandshakeUrl).getHost();
                        gatewayServer.setUrl(gatewayServerUrlHost);

                        String gatewayServerUrlProtocol = new URL(gatewayServerHandshakeUrl).getProtocol();
                        gatewayServer.setProtocol(gatewayServerUrlProtocol);

                        Integer gatewayServerUrlPort = new URL(gatewayServerHandshakeUrl).getPort();
                        gatewayServer.setPort(gatewayServerUrlPort);

                        gatewayServerVerifyUrl = gatewayServerUrlProtocol + "://" + gatewayServerUrlHost + ":" + gatewayServerUrlPort + gatewayServerVerifyUrl;
                        Log.d(getLocalClassName(), "Gateway server verification url: " + gatewayServerVerifyUrl);

                        GatewayServersHandler gatewayServersHandler = new GatewayServersHandler(getApplicationContext());
                        long gatewayServerId = gatewayServersHandler.add(gatewayServer);

                        // Navigating user to password intent
                        Intent passwordActivityIntent = new Intent(getApplicationContext(), PasswordActivity.class);

                        Intent syncHandshakeIntent = new Intent(getApplicationContext(), SyncHandshakeActivity.class);
                        syncHandshakeIntent.putExtra("state", "complete_handshake");

                        passwordActivityIntent.putExtra("callbackIntent", syncHandshakeIntent);
                        passwordActivityIntent.putExtra("gatewayserver_id", gatewayServerId);
                        passwordActivityIntent.putExtra("verification_url", gatewayServerVerifyUrl);
                        startActivity(passwordActivityIntent);

                        finish();
                    } catch (JSONException | InterruptedException | MalformedURLException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    System.out.println("Failed: " + error);
                    // Log.e(this.getClass().getSimpleName(), error.toString());
                    error.printStackTrace();
                }
            });
            queue.add(jsonObjectRequest);
        } catch (KeyStoreException | NoSuchProviderException | CertificateException | NoSuchAlgorithmException | IOException | JSONException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public static List<GatewayPhonenumber> extractPhonenumbersFromGateway(JSONArray gatewayData) throws JSONException {
        List<GatewayPhonenumber> phonenumbers = new ArrayList<>();
        for(int i=0;i<gatewayData.length(); ++i ) {
            JSONObject phone = gatewayData.getJSONObject(i);
            GatewayPhonenumber phonenumber = new GatewayPhonenumber()
                    .setType(phone.getString("type"))
                    .setNumber(phone.getString("number"))
                    .setDefault(phone.getBoolean("default"))
                    .setCountryCode(phone.getString("country_code"))
                    .setIsp(phone.getString("isp"));

            phonenumbers.add(phonenumber);
        }
        return phonenumbers;
    }

    private void storePhonenumbersFromGateway(List<GatewayPhonenumber> phonenumbers) throws InterruptedException {
        Thread storeProviders = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore dbConnector = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DatabaseName)
                        .fallbackToDestructiveMigration()
                        .build();
                GatewayDao gatewayDao = dbConnector.gatewayDao();
                gatewayDao.deleteAll();
                for(int i=0;i<phonenumbers.size();++i) {
                    gatewayDao.insert(phonenumbers.get(i));
                }
            }
        });
        storeProviders.start();
        storeProviders.join();
    }

    public Map<Integer, List<String>>[] extractPlatformFromGateway(JSONArray gatewayData) throws JSONException {
        Map<Integer, List<String>> providers = new HashMap<>();
        Map<Integer, List<String>> platforms = new HashMap<>();
        for(int i=0;i<gatewayData.length(); ++i) {
            JSONObject provider = (JSONObject) gatewayData.get(i);
//            Log.i(this.getClass().getSimpleName(), "Providers: " + provider.get("provider").toString());

            List<String> providerDetails = new ArrayList<>();
            providerDetails.add(provider.get("provider").toString());
            providerDetails.add(provider.get("description").toString());
            providers.put(i, providerDetails);

            JSONArray provider_platforms = (JSONArray) provider.get("platforms");
            for(int j=0;j<provider_platforms.length();++j) {
                JSONObject platform = (JSONObject) provider_platforms.get(j);
//                Log.i(this.getClass().getSimpleName(), "\tPlatforms: " + platform.get("name").toString());

                List<String> platformDetails = new ArrayList<>();
                platformDetails.add(platform.get("name").toString());
                platformDetails.add(platform.get("type").toString());
                platforms.put(i, platformDetails);
            }
        }

        Map<Integer, List<String>>[] extractedInformation= new Map[]{providers, platforms};
        return extractedInformation;
    }


    private void logout(Intent intent) {
        startActivity(intent);
        finish();
    }
}