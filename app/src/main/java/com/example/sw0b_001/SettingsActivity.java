package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Providers.Gateway.GatewayDao;
import com.example.sw0b_001.Providers.Gateway.GatewayPhonenumber;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    BottomNavigationView bottomNavigationView;
    List<GatewayPhonenumber> phonenumbers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigationView = findViewById(R.id.settings_bottomnav);

        bottomNavigationView.setSelectedItemId(R.id.settings);
        findViewById(R.id.settings).setEnabled(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
//                Log.i(this.getClass().getSimpleName(), item.getTitle().toString());
                switch(item.getItemId()) {
                    case R.id.platform:
                        startActivity(new Intent(getApplicationContext(), PlatformsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                }
                return false;
            }
        });


        radioGroup = findViewById(R.id.settings_phonenumbers);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                GatewayDao gatewayDao = platformDb.gatewayDao();
                phonenumbers = gatewayDao.getAll();
            }
        };
        Thread dbFetchThread = new Thread(runnable);
        dbFetchThread.start();
        try {
            dbFetchThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loadSettings();
    }

    private void loadSettings() {
        for(GatewayPhonenumber phonenumber : phonenumbers) {
            RadioButton button = new RadioButton(this);
            button.setText(phonenumber.getNumber() + "  |  " + phonenumber.getIsp());
            button.setId((int) phonenumber.getId());
            button.setTextSize(20);
            if(phonenumber.isDefault())
                button.setChecked(true);
            button.setPadding(30, 50, 0, 50);


//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Thread makeDefault = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
//                                    Datastore.class, Datastore.DBName).build();
//                            GatewayDao gatewayDao = platformDb.gatewayDao();
//                            gatewayDao.updateDefault(true, phonenumber.getId());
//                            gatewayDao.updateDefault(true, phonenumber.getId());
//                        }
//                    });
//                }
//            });
            radioGroup.addView(button);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Thread makeDefault = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Datastore platformDb = Room.databaseBuilder(getApplicationContext(),
                                Datastore.class, Datastore.DBName).build();
                        GatewayDao gatewayDao = platformDb.gatewayDao();
                        gatewayDao.resetAllDefaults();
                        gatewayDao.updateDefault(true, checkedId);
                    }
                });
                makeDefault.start();
                try {
                    makeDefault.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}