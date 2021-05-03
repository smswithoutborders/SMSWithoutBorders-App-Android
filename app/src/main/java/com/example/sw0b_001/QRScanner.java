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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
                    barcodeDetector.release();
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("[+] QR code detected");
                            cameraSource.stop();
                            try {
                                SecurityLayer sl = new SecurityLayer();

                                Barcode.UrlBookmark intentData = barcodes.valueAt(0).url;
                                System.out.println("\t[+]: " + intentData.url);

                                String appOutput = intentData.url;
                                txtBarcodeValue.setText(appOutput);
                                appOutput += "\n[+] Transmitting public key... ";
                                txtBarcodeValue.setText(appOutput);
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                // Request a string response from the provided URL.
                                sl.init();
                                JSONObject jsonBody = new JSONObject("{\"public_key\": \"" + sl.init() + "\"}");
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(intentData.url, jsonBody, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
//                                            txtBarcodeValue.setText("DONE!");
//                                            txtBarcodeValue.setText(response.toString());
                                            System.out.println("DONE: " + response.toString());
                                            // TODO: revise method used to store publicKey, might not actually be best for storing URLs
                                            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = app_preferences.edit();
//                                            editor.putString(Gateway.VAR_PUBLICKEY, intentData.toString());
//                                            editor.commit();
                                            if( sl.initiate2WayHandshake()) {

                                                //AccessPlatforms();
                                                //finish();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
//                                            txtBarcodeValue.setText(appOutput);
                                            System.out.println("Failed: " + error);
//                                            if( error.networkResponse.statusCode == 500) {
//                                                finish();
//
//                                                return;
//                                            }
                                        }
                                });
                                queue.add(jsonObjectRequest);
                            } catch (KeyStoreException e) {
                                e.printStackTrace();
                            } catch (CertificateException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (UnrecoverableKeyException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (NoSuchProviderException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
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
        finish();
    }
}