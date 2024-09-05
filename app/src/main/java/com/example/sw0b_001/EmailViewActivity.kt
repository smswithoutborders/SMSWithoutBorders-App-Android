package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Modals.PlatformComposers.EmailComposeModalFragment
import com.example.sw0b_001.Models.Messages.EncryptedContent
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmailViewActivity : AppCompactActivityCustomized() {
    private lateinit var message: EncryptedContent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_view)

        val myToolbar = findViewById<MaterialToolbar>(R.id.layout_email_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar?.apply {
            title = intent.getStringExtra("platform_name")
            setDisplayHomeAsUpEnabled(true)
        }
        configureView()
    }

    private fun configureView() {
        CoroutineScope(Dispatchers.Default).launch {
            message = Datastore.getDatastore(applicationContext).encryptedContentDAO()
                .get(intent.getLongExtra("message_id", -1))
            runOnUiThread {
                message.encryptedContent.split(":").let {
                    findViewById<MaterialTextView>(R.id.layout_identity_header_title).apply {
                        text = "<${it[1]}>"
                    }
                    findViewById<MaterialTextView>(R.id.layout_identity_header_subject).apply {
                        text = "${getString(R.string.email_compose_cc)}: ${it[2]}\n" +
                                "${getString(R.string.email_compose_bcc)}: ${it[3]}"
                    }
                    findViewById<MaterialTextView>(R.id.layout_email_body).apply {
                        text = it.subList(5, it.size).joinToString()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.compose_view_edit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.compose_view_edit_menu_edit -> {
                CoroutineScope(Dispatchers.Default).launch {
                    val id = intent.getStringExtra("id")
                    id?.let {
                        val platforms = Datastore.getDatastore(applicationContext)
                            .storedPlatformsDao().fetch(id)
                        runOnUiThread {
                            showPlatformsModal(platforms)
                        }
                    }
                }
                return true
            }
            R.id.compose_view_edit_menu_delete -> {
                CoroutineScope(Dispatchers.Default).launch {
                    Datastore.getDatastore(applicationContext).encryptedContentDAO()
                        .delete(intent.getLongExtra("message_id", -1))
                    runOnUiThread {
                        finish()
                    }
                }
                return true
            }
        }
        return false
    }

    private fun showPlatformsModal(platforms: StoredPlatformsEntity) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val emailComposeModalFragment = EmailComposeModalFragment(platforms, message) {
            finish()
        }
        fragmentTransaction.add(emailComposeModalFragment, "email_compose_tag")
        fragmentTransaction.show(emailComposeModalFragment)
        fragmentTransaction.commitNow()
    }
}