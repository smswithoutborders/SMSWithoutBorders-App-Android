package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Login extends AppCompatActivity {
    SecurityLayer securityLayer;
    private static final int REQUEST_CAMERA_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void validateInput(View view) {
        try {
            securityLayer = new SecurityLayer();
            EditText password = findViewById(R.id.user_password);

            if(!securityLayer.hasRSAKeys()) {
                System.out.println("[+] Does not have RSA keys");
                AccessPermissions();
            }
            else {
                if(password.getText().toString().isEmpty()) {
                    password.setError("Password cannot be empty!");
                    return;
                }

                if(!securityLayer.authenticate(getApplicationContext(), password.getText().toString())) {
                    password.setError("Failed to authenticate!");
                }
                else {
                    System.out.println("[+] Has RSA Keys....");
                    // TODO remove this when done
                    try {
                        KeyStore keyStore = KeyStore.getInstance(SecurityLayer.DEFAULT_KEYSTORE_PROVIDER);
                        keyStore.load(null);
                        keyStore.deleteEntry(SecurityLayer.DEFAULT_KEYSTORE_ALIAS);
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AccessPlatforms();
                    finish();
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public void AccessPlatforms() {
        Intent intent = new Intent(this, Platforms.class);
        startActivity(intent);
    }


    public void AccessPermissions() {
        Intent intent = new Intent(this, Permissions.class);
        startActivity(intent);
    }
}