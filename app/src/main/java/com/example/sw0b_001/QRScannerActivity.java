package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;

import java.net.MalformedURLException;
import java.net.URL;

public class QRScannerActivity extends AppCompatActivity {

    Button scanButton;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    SurfaceView surfaceView;
    private boolean requestingPermission = false;
    private CodeScanner codeScanner;
    private View loaderView;
    private TextView syncText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QRScannerActivity.this, "Synchronization begins...", Toast.LENGTH_SHORT).show();
                        // TODO: authenticate text before sending for processing
                        try {
                            String resultValue = result.getText();

                            Log.d(getLocalClassName(), "QR code: " + resultValue);

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