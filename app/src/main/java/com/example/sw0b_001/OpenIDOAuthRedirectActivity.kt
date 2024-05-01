package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityAES
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Helpers
import com.example.sw0b_001.Security.SecurityHelpers
import com.example.sw0b_001.Security.SecurityRSA
import com.github.kittinunf.fuel.core.Headers

import java.net.URLDecoder


class OpenIDOAuthRedirectActivity : AppCompatActivity() {
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
        parameters.forEach { println("${it.key}: ${it.value}") }

        val state: String = URLDecoder.decode(parameters["state"]!!, "UTF-8")
        var fragmentIndex = -1
        if(BuildConfig.DEBUG && parameters.containsKey("debug")) {
            startActivityFromState(state, fragmentIndex)
            return
        }

        val decodedState = Base64.decode(state, Base64.DEFAULT)

        val credentials = UserArtifactsHandler.fetchCredentials(applicationContext)

        val secretKeyStr = UserArtifactsHandler.getSharedKeyDecrypted(applicationContext)
        var decryptedState = String(SecurityAES.decryptAESGCM(decodedState,
                SecurityHelpers.generateSecretKey(secretKeyStr, "AES")))

        if(decryptedState.contains(":")) {
            val splitVal = decryptedState.split(":", limit = 1)
            if(splitVal.size > 1)
                splitVal.apply {
                    decryptedState = this[0]
                    fragmentIndex = this[1].toInt()
                }
        }

        val platformName = Helpers.getPath(intentUrl).split("/")[2]
        val code: String = URLDecoder.decode(parameters["code"]!!, "UTF-8")

        try {
            ThreadExecutorPool.executorService.execute {

                val codeVerifier = Vault_V2.fetchOauthRequestVerifier(applicationContext)

                val cookies = Vault_V2.fetchOauthRequestCookies(applicationContext)

                val platformsUrl = getString(R.string.smswithoutborders_official_vault)

                when(platformName ) {
                    "/gmail.html", "gmail" -> {
                        var scope: String = URLDecoder.decode(parameters["scope"]!!, "UTF-8")
                        val networkResponseResults = Vault_V2.sendGmailCode(applicationContext,
                                platformsUrl,
                                Headers().set("Set-Cookie", cookies),
                                credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                                code,
                                codeVerifier,
                                scope,
                                "")
                        when(networkResponseResults.response.statusCode) {
                            200 -> { startActivityFromState(decryptedState, fragmentIndex) }
                            in 400..500-> {
                                Log.e(javaClass.name, String(networkResponseResults.response.data))
                            }
                        }
                    }
                    "/x.html", "twitter" -> {
                        val networkResponseResults = Vault_V2.sendXCode(applicationContext,
                                platformsUrl,
                                Headers().set("Set-Cookie", cookies),
                                credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                                code,
                                codeVerifier,
                                "")
                        when(networkResponseResults.response.statusCode) {
                            200 -> { startActivityFromState(decryptedState, fragmentIndex) }
                            in 400..500-> {
                                Log.e(javaClass.name, String(networkResponseResults.response.data))
                            }
                        }
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

    private fun startActivityFromState(state: String, fragmentIndex: Int) {
        val intent = Intent()
        intent.setClassName(applicationContext, state)
        intent.putExtra("fragment_index", fragmentIndex)
        startActivity(intent)
    }

}