package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sw0b_001.Models.LanguageHandler;
import com.example.sw0b_001.R;
import com.example.sw0b_001.SplashActivity;

public class LanguageFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.language_preferences, rootKey);

        ListPreference languagePreference = findPreference("language_options");
        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String languageLocale = (String) newValue;
                LanguageHandler.updateLanguage(getResources(), languageLocale);
//                startActivity(new Intent(getContext(), SplashActivity.class));

                getActivity().finish();
                startActivity(getActivity().getIntent());
                return true;
            }
        });
    }
}