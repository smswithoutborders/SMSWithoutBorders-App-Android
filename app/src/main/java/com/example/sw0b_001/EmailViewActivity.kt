package com.example.sw0b_001

import android.os.Bundle
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class EmailViewActivity : AppCompactActivityCustomized() {

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
        ThreadExecutorPool.executorService.execute {
            val message = Datastore.getDatastore(applicationContext).encryptedContentDAO()
                .get(intent.getLongExtra("message_id", -1))
            runOnUiThread {
                message.encryptedContent.split(":".toRegex(), 5).let {
                    findViewById<MaterialTextView>(R.id.layout_identity_header_title).apply {
                        text = it[0]
                    }
                    findViewById<MaterialTextView>(R.id.layout_email_body).apply {
                        text = it[4].removePrefix(":")
                    }
                }
            }
        }
    }
}