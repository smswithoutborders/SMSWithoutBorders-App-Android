package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

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

public class Login extends AppCompatActivity {

    EditText phonenumber, password;
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
            phonenumber = findViewById(R.id.user_phonenumber);
            password = findViewById(R.id.user_password);

            System.out.println("[+] Phonenumber:" + phonenumber.getText().toString());
            System.out.println("[+] Password: " + password.getText().toString());

            if(!securityLayer.hasRSAKeys()) {
                System.out.println("[+] Does not have RSA keys");
                AccessPermissions();
                securityLayer.init();

            }
            else {
                System.out.println("[+] Has RSA Keys....");
//                System.out.println("[+] Public key: " + Base64.encodeToString(getPublicKey().getEncoded(), Base64.URL_SAFE));
                AccessPlatforms();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
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