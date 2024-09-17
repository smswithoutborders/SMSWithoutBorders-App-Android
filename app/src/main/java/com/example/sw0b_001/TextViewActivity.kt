package com.example.sw0b_001

import android.os.Bundle
import com.example.sw0b_001.Modules.Helpers.formatDate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class TextViewActivity : MessagesComposeAppCompactActivityFactory() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_view)

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
            findViewById<MaterialTextView>(R.id.view_message_body).apply {
                text = it.subList(1, it.size).joinToString()
            }

            findViewById<MaterialTextView>(R.id.layout_text_date).apply {
                text = formatDate(applicationContext, message.date)
            }
        }
    }

}