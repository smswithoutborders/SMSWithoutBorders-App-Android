package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Helpers.GatewayValues;
import com.example.sw0b_001.Helpers.SecurityLayer;
import com.example.sw0b_001.Providers.Gateway.GatewayPhonenumber;
import com.example.sw0b_001.Providers.Gateway.GatewayDao;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SyncProcessingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_processing);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String syncUrl = getIntent().getStringExtra("syncUrl");
        processQR(syncUrl);
    }

    private void installSSLCertificationRequirements() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public void processQR(String QRText) {
        Log.i(this.getClass().getSimpleName(), "[+] QR text: " + QRText);
        SecurityLayer sl;
        try {
            sl = new SecurityLayer();

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JSONObject jsonBody = new JSONObject("{\"public_key\": \"" + sl.init() + "\"}");
            installSSLCertificationRequirements();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QRText, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("DONE: " + response.toString());
                    try {
                        String passwdHash = response.getString("pd");
                        String publicKey = response.getString("pk");
                        String sharedKey = response.getString("sk");
                        JSONObject platforms = response.getJSONObject("pl");
                        JSONArray phonenumbers = response.getJSONArray("ph");
                        Log.i(this.getClass().getSimpleName(), "PasswdHash: " + passwdHash);
                        Log.i(this.getClass().getSimpleName(),"PublicKey: " + publicKey);
                        Log.i(this.getClass().getSimpleName(),"SharedKey: " + sharedKey);
                        Log.i(this.getClass().getSimpleName(),"Platforms: " + platforms);
                        Log.i(this.getClass().getSimpleName(),"Phonenumbers: " + phonenumbers);

                        Map<Integer, List<String>>[] extractedInformation = extractPlatformFromGateway(platforms.getJSONArray("user_provider"));
                        Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        logoutIntent.putExtra("shared_key", sharedKey);
                        logoutIntent.putExtra("public_key", publicKey);
                        logoutIntent.putExtra("platforms", extractedInformation);
                        logoutIntent.putExtra("password_hash", passwdHash);
                        List<GatewayPhonenumber> list_phonenumbers = SyncProcessingActivity.extractPhonenumbersFromGateway(phonenumbers);
                        storePhonenumbersFromGateway(list_phonenumbers);
                        logout(logoutIntent);

                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    System.out.println("Failed: " + error);
                    Log.i(this.getClass().getSimpleName(), error.toString());
                }
            });
            queue.add(jsonObjectRequest);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
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
                        Datastore.class, Datastore.DBName).build();
                GatewayDao gatewayDao = dbConnector.gatewayDao();
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
            Log.i(this.getClass().getSimpleName(), "Providers: " + provider.get("provider").toString());

            List<String> providerDetails = new ArrayList<>();
            providerDetails.add(provider.get("provider").toString());
            providerDetails.add(provider.get("description").toString());
            providers.put(i, providerDetails);

            JSONArray provider_platforms = (JSONArray) provider.get("platforms");
            for(int j=0;j<provider_platforms.length();++j) {
                JSONObject platform = (JSONObject) provider_platforms.get(j);
                Log.i(this.getClass().getSimpleName(), "\tPlatforms: " + platform.get("name").toString());

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