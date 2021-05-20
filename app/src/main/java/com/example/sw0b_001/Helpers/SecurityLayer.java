package com.example.sw0b_001.Helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Base64;

import android.content.Context;

import com.example.sw0b_001.Helpers.Gateway;

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
    private final int KEY_SIZE=256;
    private KeyPair keyPair;
    private KeyStore keyStore;


    public static final String DEFAULT_KEYPAIR_ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA;
    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING_AES = "RSA/ECB/PKCS1Padding";
//    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;
    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/" + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP;
//    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/" + KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1;


    public static final String DEFAULT_KEYSTORE_ALIAS = "DEFAULT_SWOB_KEYSTORE";
    public static String DEFAULT_KEYSTORE_PROVIDER = "AndroidKeyStore";
    public static String DEFAULT_PASSWORD = "AFKANERDSWOB";

    OAEPParameterSpec param = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);

//    @RequiresApi(api = Build.VERSION_CODES.O)
    public SecurityLayer() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        this.keyStore.load(null);
    }

    public boolean hasRSAKeys() throws KeyStoreException {
        return this.keyStore.containsAlias(DEFAULT_KEYSTORE_ALIAS);
//        return true;
    }

    private PublicKey getPublicKey() throws KeyStoreException {
        PublicKey publicKey = this.keyStore.getCertificate(DEFAULT_KEYSTORE_ALIAS).getPublicKey();
        return publicKey;
    }

    public String init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertificateException, IOException, NoSuchPaddingException, UnrecoverableKeyException, KeyStoreException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        System.out.println("[+] Initializing security layer");
        this.init_RSA();
        PublicKey pk = this.keyStore.getCertificate(DEFAULT_KEYSTORE_ALIAS).getPublicKey();

//        byte[] encryptedText = this.encrypt_RSA("Hello world");
//        System.out.println("[+] Encrypted: " + Base64.encodeToString(encryptedText, Base64.DEFAULT));
//        encryptedText = Base64.decode(Base64.encodeToString(encryptedText, Base64.DEFAULT), Base64.DEFAULT);
//        System.out.println("[+] Decrypted: " + new String(this.decrypt_RSA(encryptedText)));

        return Base64.encodeToString(pk.getEncoded(), Base64.DEFAULT);
    }


    private void init_RSA() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, DEFAULT_KEYSTORE_PROVIDER);
        keygen.initialize(
                new KeyGenParameterSpec.Builder(
                        DEFAULT_KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_DECRYPT )
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .build());

        keygen.generateKeyPair();
//        System.out.println("[+] Public key: " + Base64.encodeToString(this.keyPair.getPublic().getEncoded(), Base64.DEFAULT));
    }

    public byte[] encrypt_RSA(byte[] input) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        this.cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
        this.cipher.init(Cipher.ENCRYPT_MODE, this.getPublicKey(), param);
        return cipher.doFinal(input);
    }

    public byte[] decrypt_RSA(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        System.out.println("[+] Decoding len: " + input.length);
//        System.out.println("[+] Decoding: " + Base64.decode(input, Base64.DEFAULT));
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

    public byte[] encrypt_AES(String input) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        char[] charsArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@', '#', '$', '%', '^', '*'};
        SecureRandom rand = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            password.append(charsArray[rand.nextInt(charsArray.length)]);
        }
        String strIV = password.toString();

        byte[] plainTextByte = "c4a15a90-57d4-4935-b5ae-ba89df8e".getBytes();
        this.key = new SecretKeySpec(plainTextByte, "AES");
        this.iv = new IvParameterSpec(strIV.getBytes());
        this.cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING_AES);
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
        byte[] ciphertext = this.cipher.doFinal(input.getBytes());
        return ciphertext;
    }

    public byte[] decrypt_AES(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] decBytes = null;
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, this.key, this.iv);
            decBytes = this.cipher.doFinal(input);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decBytes;
    }

    public byte[] getIV() {
        return this.iv.getIV();
    }


    public boolean storeSecretKey(byte[] secretKey) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        SecretKey key = new SecretKeySpec(secretKey, "AES");
        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
//        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(DEFAULT_PASSWORD.toCharArray());
        KeyStore ks = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        ks.load(null);
        this.keyStore.setEntry(Gateway.SHARED_KEY, skEntry, new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
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

    public boolean authenticate(Context context, String password) throws NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        byte[] hsPasswd = hash_sha256(password);
        System.out.println("[+] Hashed Password: " + hsPasswd);
        System.out.println("[+] Hashed Password (b64): " + new String(hsPasswd));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String passwdHash = preferences.getString(Gateway.VAR_PASSWDHASH, null);
        passwdHash = new String(decrypt_RSA(passwdHash.getBytes()));

        System.out.println("[+] Stored Password: " + passwdHash);
        return new String(hsPasswd).toUpperCase().equals(passwdHash.toUpperCase());

    }
}
