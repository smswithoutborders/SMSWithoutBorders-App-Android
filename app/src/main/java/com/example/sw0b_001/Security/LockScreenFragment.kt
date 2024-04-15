package com.example.sw0b_001.Security

import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LockScreenFragment(val layout: Int = R.layout.fragment_modal_security_lockscreen,
                         val successRunnable: Runnable?,
                         val failedRunnable: Runnable?,
                         val errorRunnable: Runnable?) : BottomSheetDialogFragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.security_lockscreen_modal)

        // Get the BottomSheetBehavior instance
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(requireContext()),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Log.e(javaClass.name, "Error occurred: $errorCode, $errString")

                        if(errorCode == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                            TODO("Implement whatever this means")
                        }
                        dismiss()
                        errorRunnable?.run()
                    }

                    override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        dismiss()
                        successRunnable?.run()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Log.w(javaClass.name, "Failed")
                        failedRunnable?.run()
                    }
                })

        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.lock_screen_relay_sms_is_locked))
                .setSubtitle(getString(R.string.lock_screen_unlock_with_your_phone_s_locking_system))
                .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()

        biometricPrompt.authenticate(promptInfo)

    }
}