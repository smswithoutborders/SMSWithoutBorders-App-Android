package com.example.sw0b_001.Security

import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw0b_001.R
import java.net.Authenticator

class LockScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lock_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // TODO: only if the authentication has been set to true
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val lockScreenFragment = LockScreenFragment(
                successRunnable = { finish() },
                failedRunnable = null,
                errorRunnable = { finishAffinity() })
        fragmentTransaction.add(lockScreenFragment, "lock_screen_frag_tag")
        fragmentTransaction.show(lockScreenFragment)
        fragmentTransaction.commitNow()
    }
}