package com.example.sw0b_001;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void onClickSignupBtn(View view) throws UnsupportedEncodingException {

        // TODO: change for production
        String signupURL = "https://staging.smswithoutborders.com/sign-up?ari=";
        String intentUrl = URLEncoder.encode("intent://staging.smswithoutborders.com/sign-up/", "UTF-8");
        signupURL = signupURL + intentUrl;

        Uri intentUri = Uri.parse(signupURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

    public void onClickSynchroniseBtn(View view) {
        Intent synchroniseTypeActivityIntent = new Intent(getApplicationContext(), SynchroniseTypeActivity.class);
        startActivity(synchroniseTypeActivityIntent);
    }



    public void linkPrivacyPolicy(View view) {
        // TODO: check for production
        Uri intentUri = Uri.parse(getResources().getString(R.string.privacy_policy));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }
}
