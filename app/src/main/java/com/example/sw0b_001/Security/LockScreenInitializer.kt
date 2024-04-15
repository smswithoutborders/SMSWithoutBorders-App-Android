package com.example.sw0b_001.Security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.startup.Initializer
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.R

class LockScreenInitializer : Initializer<AppCompatActivity> {
    override fun create(context: Context): AppCompatActivity {
        context.startActivity( Intent(context, LockScreenActivity::class.java).apply {
//            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        return LockScreenActivity()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}