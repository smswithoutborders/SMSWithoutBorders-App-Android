package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw0b_001.Models.BackendCommunications
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.result.Result
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

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
            val res = BackendCommunications.login(phoneNumber, password, url)
            when(res) {
                is Result.Success -> {
                    val obj = Json.decodeFromString<BackendCommunications.UID>(res.get())
                    BackendCommunications(obj.uid).storeUID(applicationContext, url)
                    startActivity(Intent(this, HomepageActivity::class.java))
                }
                is Result.Failure -> {
                    val errorTextView = findViewById<MaterialTextView>(R.id.login_error_text)
                    runOnUiThread(Runnable {
                        cardView.visibility = View.VISIBLE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "Wrong number or password, please try again..."
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