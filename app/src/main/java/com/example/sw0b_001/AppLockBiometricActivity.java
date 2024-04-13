package com.example.sw0b_001;

import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AppLockBiometricActivity extends AppCompactActivityCustomized {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_biometric);
    }

    private void navigateAway() throws GeneralSecurityException, IOException {
        startActivity(new Intent(this, HomepageActivity.class));
        finish();
    }

    public void enableClicked(View view) throws GeneralSecurityException, IOException {
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       prefs.edit().putBoolean("lock_screen_always_on", true).apply();

       navigateAway();
    }

    public void notNowClicked(View view) throws GeneralSecurityException, IOException {
        navigateAway();
    }
}