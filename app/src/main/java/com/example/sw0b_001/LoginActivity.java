package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.sw0b_001.Helpers.SecurityLayer;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class LoginActivity extends AppCompatActivity {
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
//                System.out.println("[+] Does not have RSA keys");
//                Log.d(MainActivity.class.getSimpleName(), )
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
//                    System.out.println("[+] Has RSA Keys....");
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
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public void AccessPlatforms() {
        Intent intent = new Intent(this, PlatformsActivity.class);
        startActivity(intent);
    }


    public void AccessPermissions() {
        Intent intent = new Intent(this, PermissionsActivity.class);
        startActivity(intent);
    }
}