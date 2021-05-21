package com.example.sw0b_001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Helpers.Datastore;
import com.example.sw0b_001.Helpers.Gateway;
import com.example.sw0b_001.Helpers.SecurityLayer;
import com.example.sw0b_001.Providers.Emails.EmailThreads;
import com.example.sw0b_001.Providers.Emails.EmailThreadsDao;
import com.example.sw0b_001.Providers.Platforms.PlatformDao;
import com.example.sw0b_001.Providers.Platforms.Platforms;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class QRScannerActivity extends AppCompatActivity {

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
                    if (ActivityCompat.checkSelfPermission(QRScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
                            SecurityLayer sl;
                            try {
                                sl = new SecurityLayer();

                                Barcode.UrlBookmark intentData = barcodes.valueAt(0).url;
                                System.out.println("\t[+]: " + intentData.url);

                                String appOutput = intentData.url;
                                txtBarcodeValue.setText(appOutput);

                                appOutput += "\n[+] Transmitting public key... ";
                                txtBarcodeValue.setText(appOutput);
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                                JSONObject jsonBody = new JSONObject("{\"public_key\": \"" + sl.init() + "\"}");
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(intentData.url, jsonBody, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            System.out.println("DONE: " + response.toString());
                                            try {
                                                String passwdHash = response.getString("pd");
                                                String publicKey = response.getString("pk");
                                                String sharedKey = response.getString("sk");
                                                JSONArray platforms = response.getJSONArray("pl");
                                                Log.i(this.getClass().getSimpleName(), "PasswdHash: " + passwdHash);
                                                Log.i(this.getClass().getSimpleName(),"PublicKey: " + publicKey);
                                                Log.i(this.getClass().getSimpleName(),"SharedKey: " + sharedKey);
                                                Log.i(this.getClass().getSimpleName(),"Platforms: " + platforms);

                                                Map<Integer, List<String>>[] extractedInformation = extractPlatformFromGateway(platforms);
                                                Map<Integer, List<String>> providers = extractedInformation[0];
                                                Map<Integer, List<String>> provider_platforms_map = extractedInformation[1];

                                                storePlatformFromGateway(providers, provider_platforms_map);

                                                byte[] decryptedSharedKey = sl.decrypt_RSA(sharedKey.getBytes("UTF-8"));
                                                Log.i(this.getClass().getSimpleName(), "[+] Decrypted SharedKey: " + new String(decryptedSharedKey));

                                                SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                SharedPreferences.Editor editor = app_preferences.edit();
                                                editor.putString(Gateway.VAR_PUBLICKEY, publicKey);
                                                editor.putString(Gateway.VAR_PASSWDHASH, passwdHash);
                                                editor.commit();
                                                Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                logoutIntent.putExtra("shared_key", sharedKey);
//                                                logoutIntent.putExtra("public_key", sharedKey);
                                                logout(logoutIntent);
                                                finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (BadPaddingException e) {
                                                e.printStackTrace();
                                            } catch (IllegalBlockSizeException e) {
                                                e.printStackTrace();
                                            } catch (InvalidKeyException e) {
                                                e.printStackTrace();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
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

    private void storePlatformFromGateway(Map<Integer, List<String>> providers, Map<Integer, List<String>> platforms) {
        Thread storeProviders = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore dbConnector = Room.databaseBuilder(getApplicationContext(),
                        Datastore.class, Datastore.DBName).build();
                PlatformDao providerDao = dbConnector.platformDao();
                for(int i=0;i<providers.size();++i) {
                    Platforms provider = new Platforms()
                            .setName(providers.get(i).get(0))
                            .setDescription(providers.get(i).get(1))
                            .setType(providers.get(i).get(2));
                    if(provider.getName().toLowerCase().equals("google") && platforms.get(i).get(0).equals("gmail"))
                        provider.setImage(R.drawable.roundgmail);
                    providerDao.insert(provider);
                }
            }
        });
    }

    private Map<Integer, List<String>>[] extractPlatformFromGateway(JSONArray gatewayData) throws JSONException {
        Map<Integer, List<String>> providers = new HashMap<>();
        Map<Integer, List<String>> platforms = new HashMap<>();
        for(int i=0;i<gatewayData.length(); ++i) {
            JSONObject provider = (JSONObject) gatewayData.get(i);
            Log.i(this.getClass().getSimpleName(), "Providers: " + provider.get("provider").toString());

            List<String> providerDetails = new ArrayList<>();
            providerDetails.add(provider.get("provider").toString());
            providerDetails.add(provider.get("description").toString());
            providerDetails.add(provider.get("type").toString());
            providers.put(i, providerDetails);

            JSONArray provider_platforms = (JSONArray) provider.get("platforms");
            for(int j=0;j<provider_platforms.length();++j) {
                JSONObject platform = (JSONObject) provider_platforms.get(j);
                Log.i(this.getClass().getSimpleName(), "\tPlatforms: " + platform.get("name").toString());

                List<String> platformDetails = new ArrayList<>();
                platformDetails.add(platform.get("name").toString());
                platforms.put(i, platformDetails);
            }
        }

        Map<Integer, List<String>>[] extractedInformation= new Map[]{providers, platforms};
        return extractedInformation;
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

    private void logout(Intent intent) {
        startActivity(intent);
        finish();
    }

    public void AccessPlatforms() {
        Intent intent = new Intent(this, PlatformsActivity.class);
        startActivity(intent);
        finish();
    }
}