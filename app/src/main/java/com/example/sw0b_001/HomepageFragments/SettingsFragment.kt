package com.example.sw0b_001.HomepageFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sw0b_001.Models.LanguageHandler;
import com.example.sw0b_001.R;
import com.example.sw0b_001.SettingsActivities.GatewayClientsSettingsActivity;
import com.example.sw0b_001.SettingsActivities.SecurityPrivacyFragment;
import com.example.sw0b_001.SettingsActivities.StoreAccessSettingsActivity;
import com.example.sw0b_001.SplashActivity;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        Preference securityPrivacyPreference = findPreference("security_settings");
        Preference storeAccessPreference = findPreference("refresh_platforms_settings");
        Preference gatewayClientsPreference = findPreference("gateway_server_settings");

        securityPrivacyPreference.setFragment(SecurityPrivacyFragment.class.getCanonicalName());

        ListPreference languagePreference = findPreference("language_options");
        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String languageLocale = (String) newValue;
                LanguageHandler.updateLanguage(getResources(), languageLocale);

                startActivity(new Intent(getContext(), SplashActivity.class));
                getActivity().finish();
                return true;
            }
        });


        storeAccessPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                // TODO: change stored access to fragments
                Intent storeAccessIntent = new Intent(getContext(), StoreAccessSettingsActivity.class);
                startActivity(storeAccessIntent);
                return true;
            }
        });

        gatewayClientsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Intent gatewayClientIntent = new Intent(getContext(), GatewayClientsSettingsActivity.class);
                startActivity(gatewayClientIntent);
                return true;
            }
        });
    }

}