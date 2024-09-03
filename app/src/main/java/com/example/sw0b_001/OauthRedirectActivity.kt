package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityAES
import com.example.sw0b_001.Models.Publisher
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.Vault
import com.example.sw0b_001.Modules.Helpers
import com.example.sw0b_001.Security.SecurityHelpers
import com.github.kittinunf.fuel.core.Headers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.net.URLDecoder


class OauthRedirectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_open_idoauth_redirect)
        Helpers.logIntentDetails(intent)

        /**
         * Send this to Vault to complete the OAuth process
         */

        val intentUrl = intent.dataString
        if(intentUrl.isNullOrEmpty()) {
            Log.e(javaClass.name, "Intent has no URL")
            finish()
        }

        val parameters = Helpers.extractParameters(intentUrl!!)
        val decoded = String(Base64.decode(URLDecoder.decode(parameters["state"]!!, "UTF-8"),
            Base64.DEFAULT), Charsets.UTF_8)

        val values = decoded.split(",")
        val platform = values[0]
        val supportsUrlScheme = values[1] == "true"
        val code: String = URLDecoder.decode(parameters["code"]!!, "UTF-8")

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val publisher = Publisher(applicationContext)
            try {
                val llt = Vault.fetchLongLivedToken(applicationContext)
                val codeVerifier = Publisher.fetchOauthRequestVerifier(applicationContext)
                publisher.sendOAuthAuthorizationCode(llt,
                    platform,
                    code,
                    codeVerifier,
                    supportsUrlScheme)

                val vault = Vault(applicationContext)
                vault.refreshStoredTokens(applicationContext)
                vault.shutdown()
            } catch(e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            } finally {
                publisher.shutdown()
            }
            runOnUiThread {
                finish()
            }
        }
    }

}