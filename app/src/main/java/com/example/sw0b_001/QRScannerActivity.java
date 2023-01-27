package com.example.sw0b_001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.sw0b_001.databinding.ActivityQrscannerBinding;
import com.google.zxing.Result;

import java.net.URL;

public class QRScannerActivity extends AppCompactActivityCustomized {

    private boolean requestingPermission = false;
    private CodeScanner codeScanner;

    private ActivityQrscannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrscannerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QRScannerActivity.this, "QR Code identified!", Toast.LENGTH_LONG).show();
                        try {
                            String resultValue = result.getText();


                            // Raises an exception in case result is not a URL
                            URL resultURL = new URL(resultValue);

                            Intent intent = new Intent(getApplicationContext(), SyncHandshakeActivity.class);
                            intent.putExtra("state", resultValue);
                            startActivity(intent);

                            finish();
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                            Toast.makeText(QRScannerActivity.this, "Failed to synchronize [" + result.getText() + "]", Toast.LENGTH_SHORT).show();
                            // TODO return use to QR scan screen
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}