package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.Data.v2.Vault_V2
import com.example.sw0b_001.Modules.Helpers
import com.github.kittinunf.fuel.core.Headers
import kotlinx.serialization.json.Json
import net.openid.appauth.AuthorizationException

import net.openid.appauth.AuthorizationResponse
import java.net.URLDecoder
import java.net.URLEncoder


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

        val parameters = Helpers.extractParameters(intentUrl!!)
        parameters.forEach { println("${it.key}: ${it.value}") }

        val platformName = Helpers.getPath(intentUrl).split("/")[2]
        val state: String = URLDecoder.decode(parameters["state"]!!, "UTF-8")
        var code: String = URLDecoder.decode(parameters["code"]!!, "UTF-8")

        println("\ncode: $code")
        println("state: $state")
        println("state: $state")

        try {
            ThreadExecutorPool.executorService.execute {
                val credentials = UserArtifactsHandler.fetchCredentials(applicationContext)

                val codeVerifier = Vault_V2.fetchOauthRequestVerifier(applicationContext)

                val cookies = Vault_V2.fetchOauthRequestCookies(applicationContext)
                println("Cookies: $cookies")

                val platformsUrl = getString(R.string.smswithoutborders_official_vault)

                when(platformName ) {
                    "/gmail.html", "gmail" -> {
                        var scope: String = URLDecoder.decode(parameters["scope"]!!, "UTF-8")
                        println("scope: $scope")
                        Vault_V2.sendGmailCode(applicationContext,
                                platformsUrl,
                                Headers().set("Set-Cookie", cookies),
                                credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                                code,
                                codeVerifier,
                                scope, state)
                    }
                    "/x.html", "twitter" -> {
                        Vault_V2.sendXCode(applicationContext,
                                platformsUrl,
                                Headers().set("Set-Cookie", cookies),
                                credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                                code,
                                codeVerifier, state)
                    }
                    else -> {
                        Log.e(javaClass.name, "Unknown platform request: $platformName")
                        finish()
                    }
                }
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }


}