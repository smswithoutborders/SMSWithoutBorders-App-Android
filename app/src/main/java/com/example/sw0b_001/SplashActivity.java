package com.example.sw0b_001;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.Models.LanguageHandler;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.databinding.ActivitySplashBinding;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompactActivityCustomized {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler hideElementsHandler = new Handler();

    private View screenContentView;
    private ActivitySplashBinding activitySplashBinding;
    SecurityHandler securityHandler;

    // milliseconds
    private final int SPLASH_DELAY_DURATION = 3000;

    private boolean checkHasLockScreenAlways() {
        // Get the SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the state of the SwitchPreferenceCompact
        boolean isChecked = prefs.getBoolean("lock_screen_always_on", false);
        return isChecked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLanguage();
        try {
            securityHandler = new SecurityHandler(getApplicationContext());
            activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
            setContentView(activitySplashBinding.getRoot());

            ActionBar ab = getSupportActionBar();
            // Enable the Up button
            ab.hide();
            screenContentView = activitySplashBinding.fullscreenContent;

            if(checkHasSharedKey()) {
                if(checkHasLockScreenAlways() && securityHandler.phoneCredentialsPossible()) {
                    enableLockScreen();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                            android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }
            else {
                delayedHide(0);
            }
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void enableLockScreen() throws InterruptedException {
        Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        securityHandler.authenticateWithLockScreen(intent, this);
    }

    private void updateLanguage() {
        // Get the SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Locale locale = Locale.getDefault();
        String languageCode = locale.getLanguage();

        // Get the state of the SwitchPreferenceCompact
        String languageLocale = prefs.getString("language_options", languageCode);
        Log.d(getLocalClassName(), "Language code: " + languageCode);
        Log.d(getLocalClassName(), "Language locale: " + languageLocale);

        LanguageHandler.updateLanguage(getResources(), languageLocale);
    }

    private void AccessWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        return;
    }

    private void hideUIElements() throws InterruptedException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        hideElementsHandler.removeCallbacks(makeUIElementsVisible);
        hideElementsHandler.postDelayed(hideUIElementsRunnable, UI_ANIMATION_DELAY);

        screenContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                AccessWelcomeActivity();
                finish();
            }
        }, SPLASH_DELAY_DURATION);
    }

    private final Runnable hideUIElementsRunnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            screenContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable makeUIElementsVisible = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                hideUIElements();
            } catch (InterruptedException | CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
                e.printStackTrace();
            }
        }
    };

    private boolean checkHasSharedKey() {
        try {
            if(securityHandler.hasSharedKey()) {
                return true;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) throws InterruptedException {
        hideElementsHandler.removeCallbacks(mHideRunnable);
        hideElementsHandler.postDelayed(mHideRunnable, delayMillis);
    }
}