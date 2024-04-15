package com.example.sw0b_001.Security

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import com.example.sw0b_001.R

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


        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getBoolean("lock_screen_always_on", false)) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()

            val lockScreenFragment = LockScreenFragment(
                    successRunnable = { finish() },
                    failedRunnable = null,
                    errorRunnable = { finishAffinity() })
            fragmentTransaction.add(lockScreenFragment, "lock_screen_frag_tag")
            fragmentTransaction.show(lockScreenFragment)
            fragmentTransaction.commitNow()
        } else {
            finish()
        }
    }
}