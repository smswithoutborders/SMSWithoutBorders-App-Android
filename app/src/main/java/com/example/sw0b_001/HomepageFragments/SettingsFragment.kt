package com.example.sw0b_001.HomepageFragments

import android.app.LocaleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.sw0b_001.R
import com.example.sw0b_001.SettingsActivities.GatewayClientsSettingsActivity
import com.example.sw0b_001.SettingsActivities.SecurityPrivacyFragment
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        val securityPrivacyPreference = findPreference<Preference>("security_settings")
        val gatewayClientsPreference = findPreference<Preference>("gateway_server_settings")

        securityPrivacyPreference!!.fragment = SecurityPrivacyFragment::class.java.getCanonicalName()

        val languagePreference = findPreference<ListPreference>("language_options")
        languagePreference!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val languageLocale: String = newValue.toString();
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context?.getSystemService(LocaleManager::class.java)?.applicationLocales =
                                LocaleList(Locale.forLanguageTag(languageLocale))
                    }
                    else {
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageLocale);
                        AppCompatDelegate.setApplicationLocales(appLocale);
                    }
                    true;
                }

        gatewayClientsPreference!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val gatewayClientIntent = Intent(context, GatewayClientsSettingsActivity::class.java)
            startActivity(gatewayClientIntent)
            true
        }
    }
}