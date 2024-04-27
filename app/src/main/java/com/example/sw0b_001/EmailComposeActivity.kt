package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentHandler
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.Models.Platforms._PlatformsHandler
import com.example.sw0b_001.Models.PublisherHandler
import com.example.sw0b_001.Models.SMSHandler
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.databinding.ActivityEmailComposeBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class EmailComposeActivity : AppCompactActivityCustomized() {
    private var binding: ActivityEmailComposeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailComposeBinding.inflate(layoutInflater)
        val view: View = binding!!.getRoot()
        setContentView(view)
        val composeToolbar = findViewById<MaterialToolbar>(R.id.email_compose_toolbar)
        setSupportActionBar(composeToolbar)

        // Enable the Up button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("encrypted_content_id")) {
            populateEncryptedContent()
        }
    }

    private fun populateEncryptedContent() {
        val encryptedContentId = intent.getLongExtra("encrypted_content_id", -1)

        ThreadExecutorPool.executorService.execute {
            val encryptedContentDAO = Datastore.getDatastore(applicationContext)
                    .encryptedContentDAO()
            val encryptedContent = encryptedContentDAO[encryptedContentId]
            val decryptedEmailContent = PublisherHandler.decryptPublishedContent(
                    applicationContext, encryptedContent.encryptedContent)
            runOnUiThread { populateFields(decryptedEmailContent) }
        }
    }

    private fun populateFields(decryptedEmailContent: String) {
        // Parse the input
        val decryptedEmailContentComponents = decryptedEmailContent.split(":".toRegex(), 4)

        val to = decryptedEmailContentComponents[1]
        val cc = decryptedEmailContentComponents[2]
        val bcc = decryptedEmailContentComponents[3]
        val subject = decryptedEmailContentComponents[4]
        val body: String = decryptedEmailContentComponents[5]

        val toEditText = findViewById<EditText>(R.id.email_to)
        val ccEditText = findViewById<EditText>(R.id.email_cc)
        val bccEditText = findViewById<EditText>(R.id.email_bcc)
        val subjectEditText = findViewById<EditText>(R.id.email_subject)
        val bodyEditText = findViewById<EditText>(R.id.email_compose_body_input)

        toEditText.setText(to)
        ccEditText.setText(cc)
        bccEditText.setText(bcc)
        subjectEditText.setText(subject)
        bodyEditText.setText(body)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.email_compose_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val toEditText = findViewById<TextInputEditText>(R.id.email_to)
        val ccTextInputEditText = findViewById<TextInputEditText>(R.id.email_cc)
        val bccTextInputEditText = findViewById<TextInputEditText>(R.id.email_bcc)
        val subjectTextInputEditText = findViewById<TextInputEditText>(R.id.email_subject)
        val bodyTextInputEditText = findViewById<EditText>(R.id.email_compose_body_input)

        if (item.itemId == R.id.action_send) {
            val to = toEditText.text.toString()
            val cc = ccTextInputEditText.text.toString()
            val bcc = bccTextInputEditText.text.toString()
            val subject = subjectTextInputEditText.text.toString()
            val body = bodyTextInputEditText.text.toString()

            if (to.isEmpty()) {
                toEditText.error = getString(R.string.message_compose_empty_recipient)
                return false
            }
            if (body.isEmpty()) {
                bodyTextInputEditText.error = getString(R.string.message_compose_empty_body)
                return false
            }

            val platformId = intent.getLongExtra(INTENT_PLATFORM_ID, -1)
            val platforms = _PlatformsHandler.getPlatform(applicationContext, platformId)

            val formattedContent = processEmailForEncryption(
                    platforms.letter,
                    to,
                    cc,
                    bcc,
                    subject,
                    body)

            val encryptedContentBase64 = PublisherHandler.formatForPublishing(applicationContext, formattedContent)
            val gatewayClientMSISDN = GatewayClientsCommunications(applicationContext)
                    .getDefaultGatewayClient()

            val defaultSMSAppIntent = SMSHandler.transferToDefaultSMSApp( applicationContext,
                    gatewayClientMSISDN, encryptedContentBase64)

            if (defaultSMSAppIntent.resolveActivity(packageManager) != null) {
                startActivity(defaultSMSAppIntent)
                setResult(RESULT_OK, Intent())
                EncryptedContentHandler.store(applicationContext, encryptedContentBase64, gatewayClientMSISDN, platforms.name)
                finish()
            }
            return true
        }

        return false;
    }

    private fun processEmailForEncryption(platformLetter: String,
                                          to: String,
                                          cc: String,
                                          bcc: String,
                                          subject: String,
                                          body: String): String {
        return "$platformLetter:$to:$cc:$bcc:$subject:$body"
    }

    companion object {
        const val INTENT_PLATFORM_ID = "INTENT_PLATFORM_ID"
    }
}
