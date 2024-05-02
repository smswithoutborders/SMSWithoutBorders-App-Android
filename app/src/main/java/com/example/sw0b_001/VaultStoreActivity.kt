package com.example.sw0b_001

import android.os.Bundle
import androidx.fragment.app.commit
import com.github.kittinunf.fuel.core.Headers

class VaultStoreActivity : AppCompactActivityCustomized() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_idoauth_redirect)

        if(intent.hasExtra("platform_name")) {
            supportFragmentManager.commit {
                val platformName = intent.getStringExtra("platform_name")
                val fragment = VaultStorePlatformProcessingFragment(platformName!!)

                add(R.id.open_id_auth_container, fragment)
                setReorderingAllowed(true)
            }
        }
        else
            finish()
    }

    override fun onResume() {
        super.onResume()
        if(!intent.hasExtra("platform_name")) finish()
        else intent.removeExtra("platform_name")
    }
}