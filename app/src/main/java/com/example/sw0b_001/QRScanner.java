package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.HardwarePropertiesManager;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class QRScanner extends AppCompatActivity {

    Button scanButton;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private boolean requestingPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        System.out.println("[+] Back to the beginning...");

//        scanButton = findViewById(R.id.scan_btn);
        txtBarcodeValue = findViewById(R.id.qr_text);
        surfaceView = findViewById(R.id.surfaceView);

        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1080, 1200)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QRScanner.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() { 
//                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                Toast.makeText(getApplicationContext(), "QR code detected", Toast.LENGTH_SHORT).show();

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("[+] QR code detected");
                            try {
                                //TODO: put a loader here, while loads of internet activities are happening
                                cameraSource.stop();

                                Barcode.UrlBookmark intentData = barcodes.valueAt(0).url;
                                System.out.print("\t[+]: ");
                                System.out.println(intentData);
                                txtBarcodeValue.setText(intentData.url);

//                                System.out.println("[+] GATEWAY PUBLICKEY: " + gatewayPublicKey);
//                                System.out.println("[+] GATEWAY ORIGINATING URL: " + gatewayOriginatingURL);

                                SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = app_preferences.edit();

                                // TODO: revise method used to store publicKey, might not actually be best for storing URLs
                                editor.putString(Gateway.VAR_PUBLICKEY, intentData.toString());
                                editor.commit();

                                SecurityLayer securityLayer = new SecurityLayer();
                                if( securityLayer.initiate2WayHandshake()) {

                                    AccessPlatforms();
                                    finish();
                                }
                            } catch (KeyStoreException e) {
                                e.printStackTrace();
                            } catch (CertificateException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public void AccessPlatforms() {
        Intent intent = new Intent(this, Platforms.class);
        startActivity(intent);
    }
}