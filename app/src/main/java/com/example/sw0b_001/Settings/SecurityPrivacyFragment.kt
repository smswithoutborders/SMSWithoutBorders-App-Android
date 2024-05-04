package com.example.sw0b_001.Settings

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.sw0b_001.Modals.LoginModalFragment
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Security
import com.example.sw0b_001.Modals.PlatformsModalFragment
import com.example.sw0b_001.R
import com.example.sw0b_001.Security.LockScreenFragment

class SecurityPrivacyFragment : PreferenceFragmentCompat() {

    private val lockScreenAlwaysOnSettingsKey = "lock_screen_always_on"
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.security_privacy_preferences, rootKey)

        val revokeVaults = findPreference<Preference>("revoke")
        revokeVaults?.setOnPreferenceClickListener {
            showPlatformsModal()
            true
        }

        val lockScreenAlwaysOn = findPreference<SwitchPreferenceCompat>(lockScreenAlwaysOnSettingsKey)
        when(Security.isBiometricLockAvailable(requireContext())) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                lockScreenAlwaysOn?.isEnabled = false
                lockScreenAlwaysOn?.summary =
                        getString(R.string.settings_security_biometric_user_cannot_add_this_functionality_at_this_time)
            }
        }
        lockScreenAlwaysOn?.onPreferenceChangeListener = switchSecurityPreferences()

        val logout = findPreference<Preference>("logout")
        logout?.setOnPreferenceClickListener {
            UserArtifactsHandler.clearCredentials(requireContext())
            Toast.makeText(requireContext(),
                    getString(R.string.logout_all_credentials_have_been_cleared_from_app),
                    Toast.LENGTH_LONG).show()
            activity?.finish()
            true
        }

        val delete = findPreference<Preference>("delete")
        delete?.setOnPreferenceClickListener {
            showLoginModal()
            true
        }
        if(!UserArtifactsHandler.isCredentials(requireContext())) {
            logout?.isEnabled = false
            logout?.summary = getString(R.string
                    .logout_you_have_no_accounts_logged_into_vaults_at_this_time)

            delete?.isEnabled = false
            delete?.summary = getString(R.string
                    .security_settings_you_have_no_accounts_to_delete_in_vault_at_this_time)
        }
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
                return@OnPreferenceChangeListener true
            } else {
                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                val lockScreenFragment = LockScreenFragment(
                        successRunnable = {
                            val sharedPreferences = PreferenceManager
                                    .getDefaultSharedPreferences(requireContext())
                            sharedPreferences.edit().putBoolean(lockScreenAlwaysOnSettingsKey, false)
                                    .apply()
                            findPreference<SwitchPreferenceCompat>("lock_screen_always_on")
                                    ?.isChecked = false
                        },
                        failedRunnable = null,
                        errorRunnable = null)
                fragmentTransaction?.add(lockScreenFragment, "lock_screen_frag_tag")
                fragmentTransaction?.show(lockScreenFragment)
                fragmentTransaction?.commitNow()
            }
            false
        }
    }

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
    private fun showLoginModal() {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        val loginModalFragment = LoginModalFragment(Runnable {
            val credentials = UserArtifactsHandler.fetchCredentials(requireContext())
            val networkResponseResults = Vault_V2.delete(requireContext(),
                    credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                    credentials[UserArtifactsHandler.PASSWORD]!!)
            when(networkResponseResults.response.statusCode) {
                200 -> {
                    UserArtifactsHandler.clearCredentials(requireContext())
                    activity?.let {
                        it.runOnUiThread {
                            it.recreate()
                            Toast.makeText(requireContext(),
                                    getString(R.string.security_settings_vault_account_deleted),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                } else -> {
                    activity?.let {
                        it.runOnUiThread {
                            Toast.makeText(requireContext(),
                                    getString(R.string.networ_error_reaching_server_please_try_again),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
        fragmentTransaction?.add(loginModalFragment, "login_signup_login_vault_tag")
        fragmentTransaction?.show(loginModalFragment)
        fragmentTransaction?.commit()
    }

    private fun showPlatformsModal() {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        val platformsModalFragment =
                PlatformsModalFragment(PlatformsModalFragment.SHOW_TYPE_SAVED_REVOKE)
        fragmentTransaction?.add(platformsModalFragment, "store_platforms_tag")
        fragmentTransaction?.show(platformsModalFragment)
        fragmentTransaction?.commitNow()
    }
}