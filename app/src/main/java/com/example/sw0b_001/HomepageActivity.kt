package com.example.sw0b_001

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
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
import com.example.sw0b_001.Settings.GatewayClientListingFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class HomepageActivity : AppCompactActivityCustomized() {
    lateinit var myToolbar: MaterialToolbar

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    supportFragmentManager.popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    recreate()
                }
                else -> { }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        myToolbar = findViewById(R.id.homepage_recents_toolbar)
        setSupportActionBar(myToolbar)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.navigationBarColor = getResources().getColor(R.color.md_theme_surfaceContainer, theme);

        val homepageLoggedIn = HomepageLoggedIn()
        val homepageNotLoggedIn = HomepageNotLoggedIn()

        supportFragmentManager.commit {
            if(Vault.fetchLongLivedToken(applicationContext).isNotBlank()) {
                if(supportFragmentManager.findFragmentByTag("homepage_not_fragment") != null) {
                    replace(R.id.homepage_fragment_container, homepageLoggedIn,
                        "homepage_fragment")
                }
                else {
                    add(R.id.homepage_fragment_container, homepageLoggedIn, "homepage_fragment")
                }
            }
            else {
                if(supportFragmentManager.findFragmentByTag("homepage_fragment") != null)
                    replace(R.id.homepage_fragment_container, homepageNotLoggedIn, "homepage_not_fragment")
                else
                    add(R.id.homepage_fragment_container, homepageNotLoggedIn, "homepage_not_fragment")
            }
        }


        val bottomNavBar = findViewById<BottomNavigationView>(R.id.homepage_bottom_navbar)
        bottomNavBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.recents_navbar -> {
                    supportFragmentManager.commit {
                        supportActionBar?.title = getString(R.string.recents)
                        if(Vault.fetchLongLivedToken(applicationContext).isNotBlank()) {
                            replace(R.id.homepage_fragment_container, HomepageLoggedIn(),
                                "homepage_fragment" )
                        }
                        else {
                            replace( R.id.homepage_fragment_container, HomepageNotLoggedIn(),
                                "homepage_not_fragment" )
                        }
                    }
                    true
                }
                R.id.gateway_clients_navbar -> {
                    supportFragmentManager.commit {
                        supportActionBar?.title = getString(R.string.gateway_clients)
                        replace(R.id.homepage_fragment_container, GatewayClientListingFragment(),
                            "gateway_client_listing_fragment")
                    }
                    true
                }
                else -> false
            }
        }

        addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.homepage_main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.homepage_settings_menu -> {
                        activityLauncher.launch(Intent(applicationContext,
                            SettingsActivity::class.java))
                        return true
                    }
                }
                return false
            }

        })
    }
}