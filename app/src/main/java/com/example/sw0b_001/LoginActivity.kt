package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.BackendCommunications
import com.example.sw0b_001.Models.GatewayServers.GatewayServer
import com.example.sw0b_001.Models.GatewayServers.GatewayServerHandler
import com.example.sw0b_001.Models.Platforms.Platform
import com.example.sw0b_001.Models.Platforms.PlatformsHandler
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.serialization.json.Json
import java.security.PublicKey

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialButton>(R.id.login_btn).setOnClickListener {
            login(it)
        }

        val customUrlView = findViewById<TextInputLayout>(R.id.login_url)
        findViewById<MaterialTextView>(R.id.login_advanced_toggle).setOnClickListener {
            if(customUrlView.visibility == View.VISIBLE)
                customUrlView.visibility = View.INVISIBLE
            else
                customUrlView.visibility = View.VISIBLE
        }
    }

    private fun syncGatewayServer(url: String, password: String, uid: String) {
        val baseUrl = GatewayServerHandler.getBaseUrl(url)
        val gatewayServerPublicKey =
                SyncHandshakeActivity
                        .getGatewayServerPublicKey(baseUrl)
        val publicKey = SyncHandshakeActivity.getNewPublicKey(applicationContext,
                baseUrl)

        val networkResponseResults = GatewayServerHandler.sync(applicationContext,
                password.toByteArray(),
                gatewayServerPublicKey,
                GatewayServerHandler.constructUrl(baseUrl, uid),
                publicKey)

        when(networkResponseResults.result) {
            is Result.Success -> {
                val gatewayServer = GatewayServer()
                gatewayServer.id = System.currentTimeMillis()
                gatewayServer.url = baseUrl
                gatewayServer.publicKey = Base64.encodeToString(gatewayServerPublicKey.encoded,
                        Base64.DEFAULT)
                gatewayServer.port = 15000
                gatewayServer.protocol = "https"
                Datastore.getDatastore(applicationContext).gatewayServersDAO()
                        .insert(gatewayServer)
            }
            is Result.Failure -> {

            }
        }
    }

    private fun storePlatforms(user: BackendCommunications, url: String, headers: Headers) {
        val (_, response, result) = user.getPlatforms(url, headers)
        val platforms = Json.decodeFromString<BackendCommunications.Platforms>(result.get())

        Datastore.getDatastore(applicationContext)
                .platformDao().deleteAll()

        platforms.saved_platforms.forEach {
            val platform = Platform()
            platform.name = it.name
            platform.description = ""
            platform.type = it.type
            platform.letter = it.letter
            platform.logo = PlatformsHandler
                    .hardGetLogoByName(applicationContext, it.name)
            Datastore.getDatastore(applicationContext)
                    .platformDao().insert(platform)
        }
    }

    private fun login(view: View) {
        val cardView = findViewById<MaterialCardView>(R.id.login_status_card)
        cardView.visibility = View.GONE

        val phoneNumber = findViewById<TextInputEditText>(R.id.login_phonenumber_text_input)
                .text.toString()
        val password = findViewById<TextInputEditText>(R.id.login_password_text_input)
                .text.toString()
        val customUrl = findViewById<TextInputEditText>(R.id.login_url_input)

        var url = getString(R.string.default_login_url)
        if(customUrl != null && customUrl.text != null)
            url = customUrl.text.toString()

        val progressIndicator = findViewById<LinearProgressIndicator>(R.id.login_progress_bar)
        progressIndicator.visibility = View.VISIBLE

        ThreadExecutorPool.executorService.execute(Runnable {
            val networkResponseResults = BackendCommunications.login(phoneNumber, password, url)
            when(networkResponseResults.result) {
                is Result.Success -> {
                    val obj = Json
                            .decodeFromString<BackendCommunications.UID>(
                                    networkResponseResults.result.get())
                    val uid = obj.uid
                    BackendCommunications(obj.uid).storeUID(applicationContext, url)
                    syncGatewayServer(url, password, uid)
                    storePlatforms(BackendCommunications(uid),
                            getString(R.string.default_backend_url),
                            networkResponseResults.response!!.headers)

                    startActivity(Intent(this, HomepageActivity::class.java))
                }
                is Result.Failure -> {
                    val errorTextView = findViewById<MaterialTextView>(R.id.login_error_text)
                    runOnUiThread(Runnable {
                        cardView.visibility = View.VISIBLE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = getString(R.string.login_wrong_credentials)
                    })
                }
                else -> {
                    runOnUiThread(Runnable {
                        cardView.visibility = View.VISIBLE
                        findViewById<MaterialButton>(R.id.login_retry_btn).visibility = View.VISIBLE
                    })
                }
            }
            runOnUiThread(Runnable {
                progressIndicator.visibility = View.GONE
            })
        })
    }
}