package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.sw0b_001.Security.SecurityHandler;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MessageLockBiometricsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_lock_biometrics);
    }

    private void navigateAway() throws GeneralSecurityException, IOException {
        SecurityHandler securityHandler = new SecurityHandler(getApplicationContext());
        securityHandler.setSeenBiometricScreenDecryption(true);

        startActivity(new Intent(this, HomepageActivity.class));
        finish();
    }

    public void enabledDecryptionClicked(View view) throws GeneralSecurityException, IOException {
        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "Enabled clicked");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("lock_screen_for_encryption", true).apply();

        navigateAway();
    }

    public void notNOwDecryptionClicked(View view) throws GeneralSecurityException, IOException {
        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "Not now clicked");

        navigateAway();
    }
}