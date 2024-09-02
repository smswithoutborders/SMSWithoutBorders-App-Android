package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Homepage.HomepageLoggedIn
import com.example.sw0b_001.Homepage.HomepageNotLoggedIn
import com.example.sw0b_001.Modals.LoginSignupVaultModalFragment
import com.example.sw0b_001.Modals.RebrandingModalFragment
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.Messages.MessagesRecyclerAdapter
import com.example.sw0b_001.Models.Messages.MessagesViewModel
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.Vault
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class HomepageActivity : AppCompactActivityCustomized() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val myToolbar = findViewById<MaterialToolbar>(R.id.homepage_recents_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.title = null

        supportFragmentManager.commit {
            if(Vault.fetchLongLivedToken(applicationContext).isNotBlank())
                add(R.id.homepage_fragment_container, HomepageLoggedIn(), "homepage_fragment")
            else
                add(R.id.homepage_fragment_container, HomepageNotLoggedIn(), "homepage_not_fragment")
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.navigationBarColor = getResources().getColor(R.color.md_theme_surfaceContainer, theme);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homepage_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.homepage_settings_menu -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return false
    }

}