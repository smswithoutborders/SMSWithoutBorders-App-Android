package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Data.RecentsRecyclerAdapter
import com.example.sw0b_001.Data.RecentsViewModel
import com.google.android.material.appbar.MaterialToolbar

/**
 * TODO: Security checks
 * - Checks if username is present - if valid username, continue with user in the app
 * - what if username gets spoofed (security keys won't match tho)
 */
class HomepageActivity : AppCompactActivityCustomized() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val myToolbar = findViewById<MaterialToolbar>(R.id.homepage_recents_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.title = null

        configureRecyclerHandlers()

        findViewById<View>(R.id.homepage_compose_new_btn)
                .setOnClickListener { v -> onComposePlatformClick(v) }

        // TODO: for verification
        //        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        //        registerReceiver(smsVerificationReceiver, intentFilter);
    }

    private fun configureRecyclerHandlers() {
        val recentRecyclerAdapter = RecentsRecyclerAdapter()
        val linearLayoutManager = LinearLayoutManager(applicationContext)

        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true

        val recentRecyclerView = findViewById<RecyclerView>(R.id.recents_recycler_view)
        recentRecyclerView.layoutManager = linearLayoutManager
        recentRecyclerView.adapter = recentRecyclerAdapter
        val recentsViewModel = ViewModelProvider(this)[RecentsViewModel::class.java]

        val encryptedContentDAO = Datastore.getDatastore(applicationContext)
                .encryptedContentDAO()

        recentsViewModel.getMessages(encryptedContentDAO).observe(this) {
            val noRecentMessagesText = findViewById<TextView>(R.id.no_recent_messages)
            recentRecyclerAdapter.mDiffer.submitList(it)

            if (it.isNullOrEmpty())
                noRecentMessagesText.visibility = View.VISIBLE
            else
                noRecentMessagesText.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homepage_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.homepage_settings_menu -> {
                Log.d(localClassName, "Settings has been clicked")
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return false
    }


    fun onComposePlatformClick(view: View?) {
        showComposeNewPlatformLayout(R.layout.fragment_modal_sheet_compose_platforms)
    }

    fun showComposeNewPlatformLayout(layout: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val homepageComposeNewFragment = HomepageComposeNewFragment(layout)
        fragmentTransaction.add(homepageComposeNewFragment,
                HomepageComposeNewFragment.TAG)
        fragmentTransaction.show(homepageComposeNewFragment)
        fragmentTransaction.commitNow()
    }

}