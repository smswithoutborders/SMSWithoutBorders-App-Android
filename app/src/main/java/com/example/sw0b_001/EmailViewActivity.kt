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

class EmailViewActivity : MessagesComposeAppCompactActivityFactory() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_view)

        val myToolbar = findViewById<MaterialToolbar>(R.id.view_message_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar?.apply {
            title = intent.getStringExtra("platform_name")
            setDisplayHomeAsUpEnabled(true)
        }

        configureView()
    }

    private fun configureView() {
        message.encryptedContent.split(":").let {
            findViewById<MaterialTextView>(R.id.layout_identity_header_title).apply {
                text = "<${it[1]}>"
            }
            findViewById<MaterialTextView>(R.id.layout_identity_header_subject).apply {
                text = "${getString(R.string.email_compose_cc)}: ${it[2]}\n" +
                        "${getString(R.string.email_compose_bcc)}: ${it[3]}"
            }
            findViewById<MaterialTextView>(R.id.view_message_body).apply {
                text = it.subList(5, it.size).joinToString()
            }
        }
    }

}