package com.example.sw0b_001.Security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.sw0b_001.Helpers.GatewayValues;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

public class SecurityHandler {
    Cipher cipher;
    SecretKeySpec key;
    IvParameterSpec iv;
    KeyStore keyStore;
    Context context;
    SharedPreferences sharedPreferences;
    static final String SHARED_SECRET_KEY = "SHARED_SECRET_KEY";
    OAEPParameterSpec param = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);

    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/" + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;
    public static final String DEFAULT_KEYSTORE_ALIAS = "DEFAULT_SWOB_KEYSTORE";
    public static final String DEFAULT_SHARED_KEY_ALIAS = "DEFAULT_SHARED_KEY";
    public static final String DEFAULT_KEYSTORE_PROVIDER = "AndroidKeyStore";

    public SecurityHandler() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);
    }

    public SecurityHandler(Context context) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);
        this.context = context;

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean hasSharedKey() throws KeyStoreException {
        try {
            MasterKey masterKeyAlias = new MasterKey.Builder(this.context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    SecurityHandler.SHARED_SECRET_KEY,
                    masterKeyAlias,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );

            Boolean keystoreHasSharedKey = sharedPreferences.contains(SecurityHandler.SHARED_SECRET_KEY);

            if (keystoreHasSharedKey) {
                return true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static PublicKey getPublicKeyFromBase64String(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        return pubKey;
    }

    public byte[] encryptRSA(byte[] input, String publicKeyBase64) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        PublicKey publicKey = getPublicKeyFromBase64String(publicKeyBase64);
        this.cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
        this.cipher.init(Cipher.ENCRYPT_MODE, publicKey, param);
        return cipher.doFinal(input);
    }

    // Requirements to use this: input has to be Base64 encoded
    public byte[] decrypt_RSA(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        input = Base64.decode(input, Base64.DEFAULT);
        byte[] decBytes = null;
        try {

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)this.keyStore.getEntry(DEFAULT_KEYSTORE_ALIAS, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            this.cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
            this.cipher.init(Cipher.DECRYPT_MODE, privateKey, param);
            decBytes = this.cipher.doFinal(input);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decBytes;
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

    public byte[] encrypt_AES(byte[] input) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, KeyStoreException, UnrecoverableEntryException, CertificateException, IOException {
        String plainTextByte = this.sharedPreferences.getString(GatewayValues.SHARED_KEY, null);
        byte[] decryptedKey = decrypt_RSA(plainTextByte.getBytes());
        this.key = new SecretKeySpec(decryptedKey, "AES");
        KeyStore keystore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        keystore.load(null);

        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
        byte[] ciphertext = this.cipher.doFinal(input);
        return ciphertext;
    }

    public byte[] encrypt_AES(byte[] input, byte[] iv) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, KeyStoreException, UnrecoverableEntryException, CertificateException, IOException {
        String plainTextByte = this.sharedPreferences.getString(GatewayValues.SHARED_KEY, null);
        byte[] decryptedKey = decrypt_RSA(plainTextByte.getBytes());
        this.key = new SecretKeySpec(decryptedKey, "AES");
        KeyStore keystore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        keystore.load(null);

        this.iv = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
        byte[] ciphertext = this.cipher.doFinal(input);
        return ciphertext;
    }

    public byte[] encrypt_AES(String input, byte[] iv) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, KeyStoreException, UnrecoverableEntryException, CertificateException, IOException {
        String plainTextByte = this.sharedPreferences.getString(GatewayValues.SHARED_KEY, null);
        byte[] decryptedKey = decrypt_RSA(plainTextByte.getBytes("UTF-8"));
        this.key = new SecretKeySpec(decryptedKey, "AES");

        this.iv = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
        byte[] ciphertext = this.cipher.doFinal(input.getBytes("UTF-8"));
        return ciphertext;
    }

    public byte[] decrypt_AES(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        String plainTextByte = this.sharedPreferences.getString(GatewayValues.SHARED_KEY, null);
        byte[] decryptedKey = decrypt_RSA(plainTextByte.getBytes());
        this.key = new SecretKeySpec(decryptedKey, "AES");
        byte[] decBytes = null;
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.cipher.init(Cipher.DECRYPT_MODE, this.key, this.iv);
            decBytes = this.cipher.doFinal(input);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return decBytes;
    }

    public byte[] getIV() {
        // return this.iv.getIV();
        return this.cipher.getIV();
    }


    public void storeSharedKey(String sharedKeyBytes) throws GeneralSecurityException, IOException {
        MasterKey masterKeyAlias = new MasterKey.Builder(this.context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences encryptedSharedPreferences = EncryptedSharedPreferences.create(
                context,
                SecurityHandler.SHARED_SECRET_KEY,
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM );

        SharedPreferences.Editor sharedPreferencesEditor = encryptedSharedPreferences.edit();

        // (shared_secret_key)
        // If stolen IV still required and currently encrypted with public key
        sharedPreferencesEditor.putString(SecurityHandler.SHARED_SECRET_KEY, sharedKeyBytes);
        if(!sharedPreferencesEditor.commit()) {
            Log.e(getClass().getName(), "- Failed to store shared key!");
            throw new RuntimeException("Failed to store shared key!");
        }
        else {
            Log.i(getClass().getName(), "+ Shared key stored successfully");
        }

    }

    public KeyPairGenerator generateKeyPair(String keystoreAlias) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, DEFAULT_KEYSTORE_PROVIDER);

        keygen.initialize(
                new KeyGenParameterSpec.Builder(
                        keystoreAlias,
                        KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .build());
        return keygen;
    }


    public static String convert_to_pem_format(byte[] key) {
        String keyString = Base64.encodeToString(key, Base64.DEFAULT);
        keyString = "-----BEGIN PUBLIC KEY-----\n" + keyString;
        keyString += "-----END PUBLIC KEY-----";

        return keyString;
    }
}
