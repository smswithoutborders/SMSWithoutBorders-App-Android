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

        if(getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            String deepLinkUrl = getIntent().getDataString();
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
        }
        catch(Exception e) {
            Log.e(getLocalClassName(), "Exception with DeepLink", e);
        }
        finally {
            finish();
        }
    }
}