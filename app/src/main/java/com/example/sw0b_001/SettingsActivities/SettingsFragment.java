package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sw0b_001.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        Preference languagePreference = findPreference("language_settings");
        Preference securityPrivacyPreference = findPreference("security_settings");
        Preference storeAccessPreference = findPreference("refresh_platforms_settings");
        Preference gatewayClientsPreference = findPreference("gateway_server_settings");

        securityPrivacyPreference.setFragment(SecurityPrivacyFragment.class.getCanonicalName());
        languagePreference.setFragment(LanguageFragment.class.getCanonicalName());

        storeAccessPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                // TODO: change stored access to fragments
                Intent storeAccessIntent = new Intent(getContext(), StoreAccessSettingsActivity.class);
                startActivity(storeAccessIntent);
                return false;
            }
        });

        gatewayClientsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Intent gatewayClientIntent = new Intent(getContext(), GatewayClientsSettingsActivity.class);
                startActivity(gatewayClientIntent);
                return false;
            }
        });
    }


}