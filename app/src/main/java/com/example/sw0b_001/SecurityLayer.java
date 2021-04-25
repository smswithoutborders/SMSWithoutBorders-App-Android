package com.example.sw0b_001;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.RequiresApi;
import android.content.Context;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
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
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityLayer {
    private Cipher cipher;
    private SecretKeySpec key;
    private IvParameterSpec iv;
    private final int KEY_SIZE=256;
    private KeyPair keyPair;
    private KeyStore keyStore;


    public static final String DEFAULT_KEYPAIR_ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA;
    public static final String DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/PKCS1Padding";

    public static final String DEFAULT_KEYSTORE_ALIAS = "DEFAULT_SWOB_KEYSTORE";
    public static String DEFAULT_KEYSTORE_PROVIDER = "AndroidKeyStore";



    @RequiresApi(api = Build.VERSION_CODES.O)
    public SecurityLayer() throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchProviderException, CertificateException, KeyStoreException, UnrecoverableKeyException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_PROVIDER);
        keyStore.load(null);


       if(!hasRSAKeys()) {
           System.out.println("[+] Does not have RSA keys");
           this.init_RSA();
       }
       else {
           System.out.println("[+] Has RSA Keys....");
           System.out.println("[+] Public key: " + Base64.encodeToString(getPublicKey().getEncoded(), Base64.URL_SAFE));
       }


       byte[] encryptedText = encrypt_RSA("Hello world");
       System.out.println("[+] Encrypted: " + Base64.encodeToString(encryptedText, Base64.URL_SAFE));
       System.out.println("[+] Decrypted: " + new String(decrypt_RSA(encryptedText)));

    }

    public boolean hasRSAKeys() throws KeyStoreException {
        return keyStore.containsAlias(DEFAULT_KEYSTORE_ALIAS);
    }

    private PublicKey getPublicKey() throws KeyStoreException {
        PublicKey publicKey = keyStore.getCertificate(DEFAULT_KEYSTORE_ALIAS).getPublicKey();
        return publicKey;
    }


    private void init_RSA() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(
                "RSA", DEFAULT_KEYSTORE_PROVIDER);
        keygen.initialize(
                new KeyGenParameterSpec.Builder(
                        DEFAULT_KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .build());
        keyPair = keygen.generateKeyPair();
        System.out.println("[+] Public key: " + Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.URL_SAFE));
    }

    public byte[] encrypt_RSA(String input) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        return cipher.doFinal(input.getBytes());
    }

    public byte[] decrypt_RSA(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] decBytes = null;
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(DEFAULT_KEYSTORE_ALIAS, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, cipher.getParameters());
            decBytes = cipher.doFinal(input);
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

    public byte[] encrypt(String input) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        char[] charsArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@', '#', '$', '%', '^', '*'};
        SecureRandom rand = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            password.append(charsArray[rand.nextInt(charsArray.length)]);
        }
        String strIV = password.toString();

        byte[] plainTextByte = "c4a15a90-57d4-4935-b5ae-ba89df8e".getBytes();
        key = new SecretKeySpec(plainTextByte, "AES");
        iv = new IvParameterSpec(strIV.getBytes());
        cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] ciphertext = cipher.doFinal(input.getBytes());
        return ciphertext;
    }

    public byte[] decrypt(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] decBytes = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            decBytes = cipher.doFinal(input);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decBytes;
    }

    public byte[] getIV() {
        return this.iv.getIV();
    }
}
