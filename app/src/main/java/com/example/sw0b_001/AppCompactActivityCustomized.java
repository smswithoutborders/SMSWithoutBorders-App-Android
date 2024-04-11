package com.example.sw0b_001;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.Models.LanguageHandler;
import com.example.sw0b_001.R;
import com.example.sw0b_001.Security.SecurityHandler;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.concurrent.Executor;

public class AppCompactActivityCustomized extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO: check if shared key is available else kill
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        implementViewSecurities(view);
        super.setContentView(view);
    }

    private void implementViewSecurities(View view) {
        view.setFilterTouchesWhenObscured(true);
    }


    public void authenticateWithLockScreen(Intent callbackIntent, AppCompatActivity parent) throws InterruptedException {
        Executor executor = ContextCompat.getMainExecutor(context);
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {

            }
        });

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.hardware.biometrics.BiometricPrompt biometricPrompt = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ?
                    new android.hardware.biometrics.BiometricPrompt.Builder(context)
                            .setTitle(context.getString(R.string.settings_biometric_login))
                            .setSubtitle(context.getString(R.string.settings_biometric_login_subtitle))
                            .setDescription(context.getString(R.string.settings_biometric_login_description))
                            .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                            .build() :
                    new android.hardware.biometrics.BiometricPrompt.Builder(context)
                            .setTitle(context.getString(R.string.settings_biometric_login))
                            .setSubtitle(context.getString(R.string.settings_biometric_login_subtitle))
                            .setDescription(context.getString(R.string.settings_biometric_login_description))
                            .setDeviceCredentialAllowed(true)
                            .build();

            biometricPrompt.authenticate(cancellationSignal,
                    executor, new android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode,
                                                          @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(context,
                                                "Authentication error: " + errorCode + ":" + errString, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }

                        @Override
                        public void onAuthenticationSucceeded(
                                @NonNull android.hardware.biometrics.BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            if (BuildConfig.DEBUG)
                                Toast.makeText(context,
                                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                            ActivityOptions options = ActivityOptions.makeCustomAnimation(context,
                                    android.R.anim.fade_in, android.R.anim.fade_out);
                            context.startActivity(callbackIntent, options.toBundle());

                            if(parent != null)
                                parent.finish();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            if (BuildConfig.DEBUG)
                                Toast.makeText(context, "Authentication failed",
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}
