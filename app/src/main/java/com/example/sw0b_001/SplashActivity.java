package com.example.sw0b_001;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.databinding.ActivitySplashBinding;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler hideElementsHandler = new Handler();

    private View screenContentView;
    private ActivitySplashBinding activitySplashBinding;

    // milliseconds
    private final int SPLASH_DELAY_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(activitySplashBinding.getRoot());
        screenContentView = activitySplashBinding.fullscreenContent;

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(0);

    }

    private void AccessWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        return;
    }

    private void AccessHomePageActivity() {
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity(intent);
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
            SecurityHandler securityLayer = new SecurityHandler(getApplicationContext());
            if(securityLayer.hasSharedKey()) {
                return true;
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
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
    private void delayedHide(int delayMillis) {
        hideElementsHandler.removeCallbacks(mHideRunnable);
        hideElementsHandler.postDelayed(mHideRunnable, delayMillis);


        if(checkHasSharedKey()) {
            AccessHomePageActivity();
            finish();
        }
        else {
            Log.d(getLocalClassName(), "[*] Does not have shared key stored!");
        }
    }
}