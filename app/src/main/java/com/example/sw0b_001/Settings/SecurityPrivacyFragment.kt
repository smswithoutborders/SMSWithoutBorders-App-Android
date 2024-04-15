package com.example.sw0b_001.Settings

import android.app.Activity
import android.app.LocaleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.LocaleListCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.example.sw0b_001.Modules.Security
import com.example.sw0b_001.R
import java.util.Locale

class SecurityPrivacyFragment : PreferenceFragmentCompat() {

    private val registerActivityResult =
            registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == Activity.RESULT_OK) {
                    findPreference<SwitchPreferenceCompat>("lock_screen_always_on")
                            ?.isChecked = true
                } else {
                    Toast.makeText(requireContext(),
                            getString(R.string.security_settings_failed_to_switch_security),
                            Toast.LENGTH_SHORT)
                            .show()
                }
            }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.security_privacy_preferences, rootKey)

        val lockScreenAlwaysOn = findPreference<SwitchPreferenceCompat>("lock_screen_always_on")
        when(Security.isBiometricLockAvailable(requireContext())) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                lockScreenAlwaysOn?.isEnabled = false
        }
        lockScreenAlwaysOn?.onPreferenceChangeListener = switchSecurityPreferences()
    }

    private fun switchSecurityPreferences(): OnPreferenceChangeListener {
        return OnPreferenceChangeListener {_, newValue ->
            if(newValue as Boolean) {
                when (Security.isBiometricLockAvailable(requireContext())) {
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                            }
                        } else {
                            Intent(Settings.ACTION_SECURITY_SETTINGS)
                        }
                        registerActivityResult.launch(enrollIntent)
                    }
                }
            }
            true
        }
    }
}