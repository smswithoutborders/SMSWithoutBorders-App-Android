package com.example.sw0b_001;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sw0b_001.databinding.ActivitySynchroniseTypeBinding;

import java.net.URL;

public class SyncInitiateActivity extends AppCompactActivityCustomized {
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private ActivitySynchroniseTypeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySynchroniseTypeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get a support ActionBar corresponding to this toolbar
//        ActionBar ab = getSupportActionBar();
//        // Enable the Up button
//        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Intent defaultIntent = getIntent();

        if(defaultIntent.getAction() != null && defaultIntent.getAction().equals(Intent.ACTION_VIEW)) {
            String deepLinkUrl = defaultIntent.getDataString();
            handleIncomingDeepLink(deepLinkUrl);
        }
    }

    private void handleIncomingDeepLink(String deepLinkUrl) {
        try {
            if(deepLinkUrl.contains("apps://"))
                deepLinkUrl = deepLinkUrl.replace("apps://",  "https://");

            else if(deepLinkUrl.contains("app://"))
                deepLinkUrl = deepLinkUrl.replace("app://", "http://");

            else if(deepLinkUrl.contains("intent://"))
                return;

            // If not a link would crash the app
            URL resultURL = new URL(deepLinkUrl);

            Intent intent = new Intent(getApplicationContext(), SyncHandshakeActivity.class);
            intent.putExtra("state", deepLinkUrl);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            finish();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            finish();
        }
    }

    public void onContinueClick(View view) {
        String smswithoutbordersHandshakeUrl = getString(R.string.smswithoutborders_official_site_login);
        Log.d(getLocalClassName(), "** " + smswithoutbordersHandshakeUrl);
        Uri intentUri = Uri.parse(smswithoutbordersHandshakeUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

    public void scanQR(View view) {
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
                Intent intent = new Intent(this, SyncInitiateActivity.class);
                startActivity(intent);
            }
        }
        finish();
    }

    public void linkPrivacyPolicy(View view) {
        Uri intentUri = Uri.parse(getResources().getString(R.string.privacy_policy));
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
    }

}