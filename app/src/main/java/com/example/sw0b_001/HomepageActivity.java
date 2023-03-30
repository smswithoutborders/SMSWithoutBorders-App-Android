package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.HomepageFragments.AvailablePlatformsFragment;
import com.example.sw0b_001.HomepageFragments.NotificationsFragment;
import com.example.sw0b_001.HomepageFragments.RecentsFragment;
import com.example.sw0b_001.HomepageFragments.SettingsFragment;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Models.Notifications.NotificationsHandler;
import com.example.sw0b_001.Models.RabbitMQ;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityHelpers;
import com.example.sw0b_001.Security.SecurityRSA;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rabbitmq.client.DeliverCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class HomepageActivity extends AppCompactActivityCustomized {

    FragmentManager fragmentManager = getSupportFragmentManager();

    final String RECENTS_FRAGMENT_TAG = "RECENTS_FRAGMENT_TAG";
    final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";

    RabbitMQ rabbitMQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        try {
            SecurityHandler securityHandler = new SecurityHandler(getBaseContext());
            if(securityHandler.requiresSyncing()) {
                securityHandler.removeSharedKey();
                startActivity(new Intent(this, SplashActivity.class));
                finish();
                return;
            }

            if(SecurityHandler.phoneCredentialsPossible(getApplicationContext()) ) {
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                if (!securityHandler.seenBiometricCheckAlwaysOn()) {
                    startActivity(new Intent(this, AppLockBiometricActivity.class));
                    finish();
                }

                else if (!securityHandler.seenBiometricCheckDecryption()) {
                    startActivity(new Intent(this, MessageLockBiometricsActivity.class));
                    finish();
                }
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.recents);

        TextView textView = findViewById(R.id.fragment_title);

        try {
            rabbitMQ = new RabbitMQ(getApplicationContext());
        } catch (Throwable e) {
            e.printStackTrace();
        }

//        fragmentManager.beginTransaction().add(R.id.homepage_fragment_container_view,
//                        RecentsFragment.class, null, RECENTS_FRAGMENT_TAG)
//                .setReorderingAllowed(true)
//                .setCustomAnimations(android.R.anim.slide_in_left,
//                        android.R.anim.slide_out_right,
//                        android.R.anim.fade_in,
//                        android.R.anim.fade_out)
//                .commitNow();
//
//        Fragment currentFragment = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG);
//        if(currentFragment instanceof SettingsFragment) {
//            textView.setText(R.string.settings_settings);
//            textView.setVisibility(View.VISIBLE);
//        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                textView.setVisibility(View.GONE);
                final int itemId = item.getItemId();
                switch(itemId) {
                    case R.id.recents: {
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                RecentsFragment.class, null, RECENTS_FRAGMENT_TAG)
                                .setReorderingAllowed(true)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        return true;
                    }
                    case R.id.settings: {
                        textView.setText(R.string.settings_settings);
                        textView.setVisibility(View.VISIBLE);
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                SettingsFragment.class, null, SETTINGS_FRAGMENT_TAG)
                                .setReorderingAllowed(true)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        return true;
                    }

                    case R.id.messages: {
                        textView.setText(R.string.messages_title);
                        textView.setVisibility(View.VISIBLE);
                        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                                        NotificationsFragment.class, null)
                                .setReorderingAllowed(true)
                                .setCustomAnimations(android.R.anim.slide_in_left,
                                        android.R.anim.slide_out_right,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out)
                                .commit();
                        return true;
                    }
                }
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkAccountSynchronization();
                    connectRMQForNotifications();
                } catch(Throwable e ) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkAccountSynchronization() throws InterruptedException, GeneralSecurityException, IOException, JSONException {
        // TODO: should become a WorkManager if fails

        List<GatewayServer> gatewayServerList = GatewayServersHandler.getAllGatewayServers(getApplicationContext());
        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        SecurityHandler securityHandler = new SecurityHandler(getApplicationContext());
        String msisdn = securityHandler.getMSISDN();

        SecurityRSA securityRSA = new SecurityRSA(getApplicationContext());
        String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServerList.get(0).getUrl());
        byte[] msisdnSigned = securityRSA.sign(msisdn.getBytes(StandardCharsets.UTF_8), keystoreAlias);
        String msisdnEncoded = Base64.encodeToString(msisdnSigned, Base64.DEFAULT);

        String gatewayServerPublicKey = gatewayServerList.get(0).getPublicKey();
        byte[] encryptedMsisdn = securityRSA.encrypt(msisdn.getBytes(StandardCharsets.UTF_8),
                SecurityRSA.getPublicKeyFromBase64String(gatewayServerPublicKey));
        msisdn = Base64.encodeToString(encryptedMsisdn, Base64.DEFAULT);

        JSONObject jsonBody = new JSONObject( "{\"msisdn\": \"" + msisdn + "\"}");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                gatewayServerList.get(0).composeFullURL()
                        + "/v2/sync/users/" + msisdnEncoded + "/verification",
                jsonBody, future, future);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy( 0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        try {
            requestQueue.add(jsonObjectRequest);

            JSONObject response = future.get(15, TimeUnit.SECONDS);
            if(response.has("shared_key")) {
                try {
                    String rxSharedKey = (String) response.get("shared_key");
                    byte[] decryptedSharedKey = SecurityHelpers.getDecryptedSharedKey(getApplicationContext());
                    byte[] decodedRxSharedKey = Base64.decode(rxSharedKey, Base64.DEFAULT);

                    if(!new String(securityRSA.decrypt(decodedRxSharedKey, keystoreAlias)).equals(
                            new String(decryptedSharedKey, StandardCharsets.UTF_8))) {
                        securityHandler.removeSharedKey();
                        startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                        finish();
                    }
                    if(BuildConfig.DEBUG)
                        Log.d(getLocalClassName(), "Verification passed!");
                } catch(Throwable e ) {
                    e.printStackTrace();
                }
            }
        } catch(TimeoutException | InterruptedException e) {
            e.printStackTrace();
        } catch(ExecutionException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause instanceof VolleyError) {
                VolleyError error = (VolleyError) cause;
                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse != null && networkResponse.statusCode == 403) {
                    securityHandler.removeSharedKey();
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    finish();
                }
                else if(networkResponse != null && networkResponse.statusCode == 404) {
                    Log.d(getLocalClassName(), "Verification code said it's 404...");
                }
            }
        }
    }

    private void connectRMQForNotifications() throws Throwable {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String messageBase64 = new String(delivery.getBody(), "UTF-8");

            try {
                String notificationData = new String(Base64.decode(messageBase64, Base64.DEFAULT), StandardCharsets.UTF_8);

                JSONObject jsonObject = new JSONObject(notificationData);
                long id = jsonObject.getLong("id");
                String message = jsonObject.getString("message");

                String type = new String();
                if(jsonObject.has("type"))
                    type = jsonObject.getString("type");

                NotificationsHandler.storeNotification(getBaseContext(), id, message, type);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    rabbitMQ.startConnection();
                    rabbitMQ.consume(deliverCallback);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onComposePlatformClick(View view) {
        fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                        AvailablePlatformsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment currentFragment = fragmentManager.findFragmentByTag(RECENTS_FRAGMENT_TAG);
        if (currentFragment instanceof RecentsFragment) {
            fragmentManager.beginTransaction().replace(R.id.homepage_fragment_container_view,
                            RecentsFragment.class, null, RECENTS_FRAGMENT_TAG)
                    .setReorderingAllowed(true)
                    .setCustomAnimations(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out)
                    .commit();
        }
//        if(!rabbitMQ.isOpen()) {
//            try {
//                connectRMQForNotifications();
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        Thread rmqThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (rabbitMQ.getConnection() != null)
                        rabbitMQ.getConnection().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        rmqThread.start();
        try {
            rmqThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}