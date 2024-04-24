package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.Data.v2.Vault_V2
import com.example.sw0b_001.Modules.Helpers
import kotlinx.serialization.json.Json
import net.openid.appauth.AuthorizationException

import net.openid.appauth.AuthorizationResponse


class OpenIDOAuthRedirectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_open_idoauth_redirect)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Helpers.logIntentDetails(intent)

        /**
         * Send this to Vault to complete the OAuth process
         */

        val intentUrl = intent.dataString
        if(intentUrl.isNullOrEmpty())
            finish()

        val platformName = Helpers.getPath(intentUrl!!)
        try {
            val credentials = UserArtifactsHandler.fetchCredentials(applicationContext)
            val url = getString(R.string.smswithoutborders_official_site_login)
            val networkResponseResults =
                    Vault_V2.login(credentials.first, credentials.second.toString(), url)

            val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid
            // TODO: check if this matches what has been stored

            val parameters = Helpers.extractParameters(intentUrl)
            when(platformName ) {
                "/gmail.html" -> {
                    Vault_V2.getGmailGrant(url, networkResponseResults.response.headers, uid,
                            credentials.first)
                    Vault_V2.sendGmailCode(url, networkResponseResults.response.headers, uid,
                            parameters["code"]!!)
                }
                "/x.html" -> {
                    Vault_V2.getXGrant(url, networkResponseResults.response.headers, uid,
                            credentials.first)
                    Vault_V2.sendXCode(url, networkResponseResults.response.headers, uid,
                            parameters["code"]!!)
                }
                else -> {
                    Log.e(javaClass.name, "Unknown platform request: $platformName")
                    finish()
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }


}