package com.example.sw0b_001.Security;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.ActivityOptions;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.MGF1ParameterSpec;
import java.util.concurrent.Executor;

import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class SecurityHandler {
    public static final String SHARED_SECRET_KEY = "SHARED_SECRET_KEY";
    public static final String MSISDN_HASH = "MSISDN_HASH";

    public static boolean hasSharedKey(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                context,
                SHARED_SECRET_KEY,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );
        return sharedPreferences.contains(SHARED_SECRET_KEY);
    }

    public static String getEncryptedBase64SharedKey(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                context,
                SHARED_SECRET_KEY,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );
        return sharedPreferences.getString(SHARED_SECRET_KEY, "");
    }

    public static String getMSISDN(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences encryptedSharedPreferences = EncryptedSharedPreferences.create(
                context,
                MSISDN_HASH,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );

        return encryptedSharedPreferences.getString(MSISDN_HASH, "");
    }

    public static void removeSharedKey(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences encryptedSharedPreferences = EncryptedSharedPreferences.create(
                context,
                SHARED_SECRET_KEY,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );

        SharedPreferences.Editor sharedPreferencesEditor = encryptedSharedPreferences.edit();
        sharedPreferencesEditor.remove(SHARED_SECRET_KEY);
        sharedPreferencesEditor.apply();
    }

    public static void storeMSISDN(Context context, String msisdnHash) throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences encryptedSharedPreferences = EncryptedSharedPreferences.create(
                context,
                MSISDN_HASH,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );

        SharedPreferences.Editor sharedPreferencesEditor = encryptedSharedPreferences.edit();
        sharedPreferencesEditor.putString(MSISDN_HASH, msisdnHash).apply();
    }

    public static void storeSharedKey(Context context, String sharedKey) throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences encryptedSharedPreferences = EncryptedSharedPreferences.create(
                context,
                SHARED_SECRET_KEY,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );

        SharedPreferences.Editor sharedPreferencesEditor = encryptedSharedPreferences.edit();

        sharedPreferencesEditor.putString(SHARED_SECRET_KEY, sharedKey).apply();
    }

    public static boolean phoneCredentialsPossible(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BiometricManager biometricManager = (BiometricManager) context.getSystemService(Context.BIOMETRIC_SERVICE);
            int canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
            switch (canAuthenticate) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    return true;

                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    break;
//                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                    // Prompts the user to create credentials that your app accepts.
//                    final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
//                    enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                            BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
////                    startActivityForResult(enrollIntent, REQUEST_CODE);
//                    break;
//                case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
//                    // TODO:
//                    break;
                default:
                    break;
            }
        }
        else {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isDeviceSecure();
        }
        return false;
    }
}
