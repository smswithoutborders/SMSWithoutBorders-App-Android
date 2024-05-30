package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Modals.LoginSignupVaultModalFragment
import com.example.sw0b_001.Modals.PlatformsModalFragment
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.Messages.MessagesRecyclerAdapter
import com.example.sw0b_001.Models.Messages.MessagesViewModel
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class HomepageActivity : AppCompactActivityCustomized() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val myToolbar = findViewById<MaterialToolbar>(R.id.homepage_recents_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.title = null

        configureRecyclerHandlers()

        findViewById<View>(R.id.homepage_compose_new_btn)
                .setOnClickListener { v -> onComposePlatformClick() }

        findViewById<View>(R.id.homepage_add_new_btn)
                .setOnClickListener { v -> onComposePlatformClick(PlatformsModalFragment
                        .SHOW_TYPE_UNSAVED) }
    }

    override fun onResume() {
        super.onResume()
        configureButtonViews()
    }

    private fun configureButtonViews() {
        if(UserArtifactsHandler.isCredentials(applicationContext)) {
            findViewById<MaterialButton>(R.id.homepage_login_new_btn).visibility = View.GONE
            findViewById<MaterialButton>(R.id.homepage_add_new_btn).visibility = View.VISIBLE
            findViewById<MaterialButton>(R.id.homepage_compose_new_btn).visibility = View.VISIBLE
        } else {
            findViewById<MaterialButton>(R.id.homepage_login_new_btn).apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    loginSignupModal()
                }
            }
            findViewById<MaterialButton>(R.id.homepage_add_new_btn).visibility = View.GONE
            findViewById<MaterialButton>(R.id.homepage_compose_new_btn).visibility = View.GONE
        }
    }

    private fun loginSignupModal() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val runnable = Runnable {
            runOnUiThread {
                recreate()
                configureButtonViews()
                Toast.makeText(applicationContext, getString(R.string.homepage_vault_account_added),
                        Toast.LENGTH_SHORT).show()
            }
        }
        val loginSignupModalFragment = LoginSignupVaultModalFragment(runnable, runnable)
        fragmentTransaction.add(loginSignupModalFragment, "login_signup_login_vault_tag")
        fragmentTransaction.show(loginSignupModalFragment)
        fragmentTransaction.commit()

    }

    private fun configureRecyclerHandlers() {
        val recentRecyclerAdapter = MessagesRecyclerAdapter()
        val linearLayoutManager = LinearLayoutManager(applicationContext,
                LinearLayoutManager.VERTICAL, false);

        val messagesRecyclerView = findViewById<RecyclerView>(R.id.recents_recycler_view)
        messagesRecyclerView.layoutManager = linearLayoutManager
        messagesRecyclerView.adapter = recentRecyclerAdapter

        val viewModel: MessagesViewModel by viewModels()

        val noRecentMessagesText = findViewById<TextView>(R.id.no_recent_messages)
        viewModel.getMessages(applicationContext).observe(this) {
            recentRecyclerAdapter.mDiffer.submitList(it)
            if (it.isNullOrEmpty())
                noRecentMessagesText.visibility = View.VISIBLE
            else
                noRecentMessagesText.visibility = View.GONE
        }

        recentRecyclerAdapter.messageOnClickListener.observe(this, Observer {
            if(it != null) {
                recentRecyclerAdapter.messageOnClickListener = MutableLiveData()
                when(it.type) {
                    Platforms.TYPE_TEXT -> {
                        startActivity(Intent(this, TextViewActivity::class.java).apply {
                            putExtra("platform_name", it.platformName)
                            putExtra("message_id", it.id)
                        })
                    }
                    Platforms.TYPE_EMAIL -> {
                        startActivity(Intent(this, EmailViewActivity::class.java).apply {
                            putExtra("platform_name", it.platformName)
                            putExtra("message_id", it.id)
                        })
                    }
                }
            }
        })
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


    fun onComposePlatformClick(showType: Int = PlatformsModalFragment.SHOW_TYPE_SAVED) {
        showPlatformsModal(showType)
    }

    private fun showPlatformsModal(showType: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val platformsModalFragment = PlatformsModalFragment(showType)
        fragmentTransaction.add(platformsModalFragment, "store_platforms_tag")
        fragmentTransaction.show(platformsModalFragment)
        fragmentTransaction.commitNow()
    }

}