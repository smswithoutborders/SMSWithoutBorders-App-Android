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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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

    public void processQR(String QRText) {
        Log.i(this.getClass().getSimpleName(), "[+] QR text: " + QRText);
        SecurityLayer sl;
        try {
            sl = new SecurityLayer();

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            JSONObject jsonBody = new JSONObject("{\"public_key\": \"" + sl.init() + "\"}");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(QRText, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("DONE: " + response.toString());
                    try {
                        String passwdHash = response.getString("pd");
                        String publicKey = response.getString("pk");
                        String sharedKey = response.getString("sk");
                        JSONArray platforms = response.getJSONArray("pl");
                        JSONArray phonenumbers = response.getJSONArray("ph");
                        Log.i(this.getClass().getSimpleName(), "PasswdHash: " + passwdHash);
                        Log.i(this.getClass().getSimpleName(),"PublicKey: " + publicKey);
                        Log.i(this.getClass().getSimpleName(),"SharedKey: " + sharedKey);
                        Log.i(this.getClass().getSimpleName(),"Platforms: " + platforms);
                        Log.i(this.getClass().getSimpleName(),"Phonenumbers: " + platforms);

                        Map<Integer, List<String>>[] extractedInformation = extractPlatformFromGateway(platforms);
                        Map<Integer, List<String>> providers = extractedInformation[0];
                        Map<Integer, List<String>> provider_platforms_map = extractedInformation[1];

                        storePlatformFromGateway(providers, provider_platforms_map);

                        byte[] decryptedSharedKey = sl.decrypt_RSA(sharedKey.getBytes("UTF-8"));
                        Log.i(this.getClass().getSimpleName(), "[+] Decrypted SharedKey: " + new String(decryptedSharedKey));

                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = app_preferences.edit();
                        editor.putString(GatewayValues.VAR_PUBLICKEY, publicKey);
                        editor.putString(GatewayValues.VAR_PASSWDHASH, passwdHash);
                        editor.commit();

                        List<GatewayPhonenumber> list_phonenumbers = extractPhonenumbersFromGateway(phonenumbers);
                        storePhonenumbersFromGateway(list_phonenumbers);

                        Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        logoutIntent.putExtra("shared_key", sharedKey);
                        logout(logoutIntent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Failed: " + error);
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

    private List<GatewayPhonenumber> extractPhonenumbersFromGateway(JSONArray gatewayData) throws JSONException {
        List<GatewayPhonenumber> phonenumbers = new ArrayList<>();
        for(int i=0;i<gatewayData.length(); ++i ) {
            JSONObject phone = gatewayData.getJSONObject(i);
            GatewayPhonenumber phonenumber = new GatewayPhonenumber()
                    .setType(phone.getString("type"))
                    .setNumber(phone.getString("number"))
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

    private void storePlatformFromGateway(Map<Integer, List<String>> providers, Map<Integer, List<String>> platforms) throws InterruptedException {
        Thread storeProviders = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore dbConnector = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                PlatformDao providerDao = dbConnector.platformDao();
                for(int i=0;i<providers.size();++i) {
                    Platforms provider = new Platforms()
                            .setName(platforms.get(i).get(0))
                            .setDescription(providers.get(i).get(1))
                            .setProvider(providers.get(i).get(0))
                            .setType(platforms.get(i).get(1));
                    if(provider.getName().toLowerCase().equals("gmail") && provider.getProvider().toLowerCase().equals("google"))
                        provider.setImage(R.drawable.roundgmail);
                    providerDao.insert(provider);
                }
            }
        });
        storeProviders.start();
        storeProviders.join();
    }

    private Map<Integer, List<String>>[] extractPlatformFromGateway(JSONArray gatewayData) throws JSONException {
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