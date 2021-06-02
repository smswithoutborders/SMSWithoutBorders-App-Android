package com.example.sw0b_001.Helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Base64;

import android.content.Context;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

public class SecurityLayer {
    private Cipher cipher;
    private SecretKeySpec key;
    private IvParameterSpec iv;
    private KeyStore keyStore;

    private SharedPreferences preferences;


    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/" + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;

    public static final String DEFAULT_KEYSTORE_ALIAS = "DEFAULT_SWOB_KEYSTORE";
    public static String DEFAULT_KEYSTORE_PROVIDER = "AndroidKeyStore";

    OAEPParameterSpec param = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);

//    @RequiresApi(api = Build.VERSION_CODES.O)
    public SecurityLayer() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);
    }

    public SecurityLayer(Context context) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean hasKeyPairs(Context context) throws KeyStoreException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains(GatewayValues.SHARED_KEY) && this.keyStore.containsAlias(DEFAULT_KEYSTORE_ALIAS) && preferences.contains(GatewayValues.VAR_PASSWDHASH);
    }

    private PublicKey getPublicKey() throws KeyStoreException {
        PublicKey publicKey = this.keyStore.getCertificate(DEFAULT_KEYSTORE_ALIAS).getPublicKey();
        return publicKey;
    }

    public String init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertificateException, IOException, NoSuchPaddingException, UnrecoverableKeyException, KeyStoreException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        this.init_RSA();
        PublicKey pk = this.keyStore.getCertificate(DEFAULT_KEYSTORE_ALIAS).getPublicKey();
        return Base64.encodeToString(pk.getEncoded(), Base64.DEFAULT);
    }


    private void init_RSA() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, DEFAULT_KEYSTORE_PROVIDER);
        keygen.initialize(
                new KeyGenParameterSpec.Builder(
                        DEFAULT_KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_DECRYPT )
//                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setDigests(KeyProperties.DIGEST_SHA256)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .build());

        keygen.generateKeyPair();
    }

    public byte[] encrypt_RSA(byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        this.cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
        this.cipher.init(Cipher.ENCRYPT_MODE, this.getPublicKey(), param);
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
        String plainTextByte = preferences.getString(GatewayValues.SHARED_KEY, null);
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
        String plainTextByte = preferences.getString(GatewayValues.SHARED_KEY, null);
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
        String plainTextByte = preferences.getString(GatewayValues.SHARED_KEY, null);
        byte[] decryptedKey = decrypt_RSA(plainTextByte.getBytes("UTF-8"));
        this.key = new SecretKeySpec(decryptedKey, "AES");

        this.iv = new IvParameterSpec(iv);
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
        byte[] ciphertext = this.cipher.doFinal(input.getBytes("UTF-8"));
        return ciphertext;
    }

    public byte[] decrypt_AES(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        String plainTextByte = preferences.getString(GatewayValues.SHARED_KEY, null);
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


    public boolean storeSecretKey(byte[] secretKey) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
        KeyStore ks = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        ks.load(null);
        this.keyStore.setEntry(GatewayValues.SHARED_KEY, skEntry, new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .build());
        return true;
    }

    public byte[] hash_sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString().getBytes();
    }

    public byte[] hash_sha512(String input) throws NoSuchAlgorithmException {
        MessageDigest md = java.security.MessageDigest.getInstance("SHA-512");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString().getBytes();
    }


    public boolean authenticate(String password) throws NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        byte[] hsPasswd = hash_sha512(password);

        String passwdHash = preferences.getString(GatewayValues.VAR_PASSWDHASH, null);
        passwdHash = new String(decrypt_RSA(passwdHash.getBytes()));

        return new String(hsPasswd).toUpperCase().equals(passwdHash.toUpperCase());

    }

    public boolean authenticate(String password, byte[] passwd1) throws NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        return new String(hash_sha512(password)).toUpperCase().equals(new String(passwd1).toUpperCase());
    }
}
