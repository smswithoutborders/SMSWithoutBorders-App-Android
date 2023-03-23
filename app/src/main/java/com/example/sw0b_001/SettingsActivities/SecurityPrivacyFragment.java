package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.sw0b_001.R;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.SplashActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurityPrivacyFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.security_privacy_preferences, rootKey);

        Preference accountLogoutPreference = findPreference("logout");

        accountLogoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                try {
                    SecurityHandler securityHandler = new SecurityHandler(getContext());
                    securityHandler.removeSharedKey();

                    startActivity(new Intent(getContext(), SplashActivity.class));
                    getActivity().finish();
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }
        });
    }

}