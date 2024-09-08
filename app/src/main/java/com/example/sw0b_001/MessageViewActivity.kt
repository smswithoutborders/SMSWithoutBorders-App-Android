package com.example.sw0b_001

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Modules.Helpers.formatDate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageViewActivity : MessagesComposeAppCompactActivityFactory() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_view)

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
            findViewById<MaterialTextView>(R.id.view_message_body).apply {
                text = it.subList(2, it.size).joinToString()
            }

            findViewById<MaterialTextView>(R.id.layout_message_date).apply {
                text = formatDate(applicationContext, message.date)
            }
        }
    }

}