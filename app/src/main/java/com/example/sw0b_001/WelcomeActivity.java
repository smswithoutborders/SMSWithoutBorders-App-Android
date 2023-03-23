package com.example.sw0b_001;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WelcomeActivity extends AppCompactActivityCustomized {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void onClickSignupBtn(View view) throws UnsupportedEncodingException {

        // TODO: change for production
        String signupURL = getString(R.string.smswithoutborders_official_site_signup) + "?ari=";
        String intentUrl = URLEncoder.encode(
                getString(R.string.smswithoutborders_official_site_signup_intent), "UTF-8");
        signupURL = signupURL + intentUrl;

        Uri intentUri = Uri.parse(signupURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

    public void onContinueClick(View view) {
        String smswithoutbordersHandshakeUrl = getString(R.string.smswithoutborders_official_site_login);
        Uri intentUri = Uri.parse(smswithoutbordersHandshakeUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }



    public void linkPrivacyPolicy(View view) {
        // TODO: check for production
        Uri intentUri = Uri.parse(getResources().getString(R.string.privacy_policy));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }
}
