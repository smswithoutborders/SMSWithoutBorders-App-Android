package com.example.sw0b_001.Security;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class SecurityHandler {
    KeyStore keyStore;
    Context context;
    SharedPreferences sharedPreferences;

    public static MGF1ParameterSpec defaultEncryptionDigest = MGF1ParameterSpec.SHA256;
    public static MGF1ParameterSpec defaultDecryptionDigest = MGF1ParameterSpec.SHA1;

    public static OAEPParameterSpec encryptionDigestParam = new OAEPParameterSpec("SHA-256", "MGF1", defaultEncryptionDigest, PSource.PSpecified.DEFAULT);
    public static OAEPParameterSpec decryptionDigestParam = new OAEPParameterSpec("SHA-256", "MGF1", defaultDecryptionDigest, PSource.PSpecified.DEFAULT);
    MasterKey masterKeyAlias;

    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/" + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;

    public static final String DEFAULT_AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String DEFAULT_KEYSTORE_PROVIDER = "AndroidKeyStore";
    final String SHARED_SECRET_KEY = "SHARED_SECRET_KEY";

    public SecurityHandler() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);
    }

    public KeyStore getKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);

        return this.keyStore;
    }

    public SecurityHandler(Context context) throws GeneralSecurityException, IOException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);
        this.context = context;

        this.masterKeyAlias = new MasterKey.Builder(this.context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        this.sharedPreferences = EncryptedSharedPreferences.create(
                context,
                this.SHARED_SECRET_KEY,
                this.masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );
    }

    public boolean hasSharedKey() throws KeyStoreException {
        try {
            Boolean keystoreHasSharedKey = sharedPreferences.contains(this.SHARED_SECRET_KEY);
            if (keystoreHasSharedKey) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String generateRandom(int length) {
//        char[] charsArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@', '#', '$', '%', '^', '*'};
        char[] charsArray = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        SecureRandom rand = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(charsArray[rand.nextInt(charsArray.length)]);
        }
        return password.toString();
    }

    public byte[] getSharedKey() {
        String encryptedSharedKey = this.sharedPreferences.getString(SHARED_SECRET_KEY, "");

        byte[] encryptedSharedKeyDecoded = Base64.decode(encryptedSharedKey, Base64.DEFAULT);

        return encryptedSharedKeyDecoded;
    }

    public void storeSharedKey(String sharedKey) throws GeneralSecurityException, IOException {
        SharedPreferences encryptedSharedPreferences = EncryptedSharedPreferences.create(
                context,
                this.SHARED_SECRET_KEY,
                this.masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );

        SharedPreferences.Editor sharedPreferencesEditor = encryptedSharedPreferences.edit();

        sharedPreferencesEditor.putString(this.SHARED_SECRET_KEY, sharedKey);
        if(!sharedPreferencesEditor.commit()) {
            Log.e(getClass().getName(), "- Failed to store shared key!");
            throw new RuntimeException("Failed to store shared key!");
        }
        else {
            Log.i(getClass().getName(), "+ Shared key stored successfully");
        }

    }

}
