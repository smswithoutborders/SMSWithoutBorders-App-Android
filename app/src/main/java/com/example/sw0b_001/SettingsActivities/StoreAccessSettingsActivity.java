package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sw0b_001.QRScannerActivity;
import com.example.sw0b_001.R;

public class StoreAccessSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stored_access_settings);
    }

    public void onManualRefreshClick(View view) {
        Intent scanQRCodeIntent = new Intent(this, QRScannerActivity.class);
        startActivity(scanQRCodeIntent);
        finish();
    }
}
