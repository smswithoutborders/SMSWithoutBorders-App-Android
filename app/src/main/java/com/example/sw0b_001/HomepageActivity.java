package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.sw0b_001.HomepageFragments.AvailablePlatformsFragment;
import com.example.sw0b_001.HomepageFragments.NotificationsFragment;
import com.example.sw0b_001.HomepageFragments.RecentsFragment;
import com.example.sw0b_001.HomepageFragments.SettingsFragment;
import com.example.sw0b_001.Models.Notifications.NotificationsHandler;
import com.example.sw0b_001.Models.RabbitMQ;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.rabbitmq.client.DeliverCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class HomepageActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();

    final String RECENTS_FRAGMENT_TAG = "RECENTS_FRAGMENT_TAG";
    final String SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG";

    RabbitMQ rabbitMQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.homepage_bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.recents);

        TextView textView = findViewById(R.id.fragment_title);

        try {
            rabbitMQ = new RabbitMQ(getApplicationContext());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().add(R.id.homepage_fragment_container_view,
                        RecentsFragment.class, null, RECENTS_FRAGMENT_TAG)
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .commitNow();

        Fragment currentFragment = fragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG);
        if(currentFragment instanceof SettingsFragment) {
            textView.setText(R.string.settings_settings);
            textView.setVisibility(View.VISIBLE);
        }

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

        try {
            connectRMQForNotifications();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void connectRMQForNotifications() throws Throwable {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String messageBase64 = new String(delivery.getBody(), "UTF-8");
            if(BuildConfig.DEBUG)
                Log.d(getClass().getName(), "[x] Received Base64'" + messageBase64 + "'");

            try {
                String notificationData = new String(Base64.decode(messageBase64, Base64.DEFAULT), StandardCharsets.UTF_8);

                if(BuildConfig.DEBUG)
                    Log.d(getClass().getName(), "[x] Received '" + notificationData + "'");

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
    protected void onStop() {
        Thread rmqThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
        super.onStop();
    }
}