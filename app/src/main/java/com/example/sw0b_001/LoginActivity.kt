package com.example.sw0b_001

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw0b_001.Models.BackendCommunications
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

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
    }

    private fun login(view: View) {
        val phoneNumber = findViewById<TextInputEditText>(R.id.login_phonenumber_text_input)
                .text.toString()
        val password = findViewById<TextInputEditText>(R.id.login_password_text_input)
                .text.toString()
        val customUrl = findViewById<TextInputEditText>(R.id.login_url_input)

        val url = getString(R.string.default_url)
        if(customUrl != null && customUrl.text != null)
            customUrl.text.toString()

        val progressIndicator = findViewById<LinearProgressIndicator>(R.id.login_progress_bar)
        progressIndicator.visibility = View.VISIBLE
        ThreadExecutorPool.executorService.execute(Runnable {
            try {
                val (_, response, result) = BackendCommunications.login(phoneNumber, password, url)
                if (response.statusCode != 200) {
                    findViewById<MaterialCardView>(R.id.login_status_card).visibility = View.VISIBLE
                    val errorTextView = findViewById<MaterialTextView>(R.id.login_error_text)
                    errorTextView.text = result.get()
                } else {
                    Log.d(javaClass.name, "Storing UID for user")
                }
            } catch(e: Exception) {
                Log.e(javaClass.name, "Exception: $e")
                findViewById<MaterialCardView>(R.id.login_status_card).visibility = View.VISIBLE
                val errorTextView = findViewById<MaterialTextView>(R.id.login_error_text)
                errorTextView.text = e.message

                findViewById<MaterialButton>(R.id.login_retry_btn).visibility = View.VISIBLE
            } finally {
                runOnUiThread(Runnable {
                    progressIndicator.visibility = View.GONE
                })
            }
        })
    }
}