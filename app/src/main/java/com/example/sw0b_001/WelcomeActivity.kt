package com.example.sw0b_001

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.android.material.button.MaterialButton
import java.io.UnsupportedEncodingException

class WelcomeActivity : AppCompactActivityCustomized() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        findViewById<View>(R.id.welcome_login_btn).setOnClickListener { v -> onClickLogin(v) }

        findViewById<MaterialButton>(R.id.welcome_signup_btn)
                .setOnClickListener { onClickSignupBtn(it) }
        findViewById<MaterialButton>(R.id.welcome_login_btn)
                .setOnClickListener { onClickLogin(it) }
    }

    fun onClickSignupBtn(view: View?) {
        TODO("Implement this")
    }

    fun onClickLogin(view: View?) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun linkPrivacyPolicy(view: View?) {
        val intentUri = Uri.parse(getResources().getString(R.string.privacy_policy))
        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        startActivity(intent)
    }
}
