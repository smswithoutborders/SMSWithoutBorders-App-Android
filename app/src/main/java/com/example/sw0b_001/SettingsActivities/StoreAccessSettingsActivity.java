package com.example.sw0b_001.SettingsActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sw0b_001.Models.EncryptedContent.EncryptedContentHandler;
import com.example.sw0b_001.QRScannerActivity;
import com.example.sw0b_001.R;
import com.example.sw0b_001.SynchroniseTypeActivity;

public class StoreAccessSettingsActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stored_access_settings);
    }

    private void cleanseEncryptedContentDatabase() {
        EncryptedContentHandler.clearedStoredEncryptedContents(getApplicationContext());
    }

    public void onContinueClick(View view) {
        cleanseEncryptedContentDatabase();
        String smswithoutbordersHandshakeUrl = "https://staging.smswithoutborders.com/login";
        Uri intentUri = Uri.parse(smswithoutbordersHandshakeUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

    public void onManualRefreshClick(View view) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, QRScannerActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Showing the toast message
                Toast.makeText(getApplicationContext(), "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, QRScannerActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SynchroniseTypeActivity.class);
                startActivity(intent);
            }
        }
        finish();
    }
}
