package com.example.sw0b_001;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void onClickSynchroniseBtn(View view) {
        Intent synchroniseTypeActivityIntent = new Intent(getApplicationContext(), PermissionsActivity.class);
        startActivity(synchroniseTypeActivityIntent);

    }



    public void linkPrivacyPolicy(View view) {
        Uri intentUri = Uri.parse(getResources().getString(R.string.privacy_policy));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }
}
