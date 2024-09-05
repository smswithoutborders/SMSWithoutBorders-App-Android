package com.example.sw0b_001

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.sw0b_001.Homepage.HomepageLoggedIn
import com.example.sw0b_001.Homepage.HomepageNotLoggedIn
import com.example.sw0b_001.Models.Vault
import com.example.sw0b_001.Homepage.GatewayClientListingFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomepageActivity : AppCompactActivityCustomized() {
    private lateinit var myToolbar: MaterialToolbar
    private val homepageLoggedIn = HomepageLoggedIn()
    private val homepageNotLoggedIn = HomepageNotLoggedIn()

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