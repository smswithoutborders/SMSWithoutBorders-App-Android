package com.example.sw0b_001.Modules

import android.content.Context
import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt

class Security {

    companion object {
        fun isBiometricLockAvailable(context: Context): Int {
            val biometricManager = BiometricManager.from(context)
            return biometricManager.canAuthenticate(BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        }
    }
}