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
import com.example.sw0b_001.Models.RecentsRecyclerAdapter
import com.example.sw0b_001.Models.RecentsViewModel
import com.google.android.material.appbar.MaterialToolbar

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