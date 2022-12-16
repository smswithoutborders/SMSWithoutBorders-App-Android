package com.example.sw0b_001.Security;

import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityAES extends SecurityHandler {

    public SecurityAES() throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {
        super();
    }

    public SecurityAES(Context context) throws GeneralSecurityException, IOException {
        super(context);
    }

    public byte[] encrypt(byte[] iv, byte[] input, byte[] sharedKey) throws Throwable {
        byte[] ciphertext = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(sharedKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(DEFAULT_AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            ciphertext = cipher.doFinal(input);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(e);
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] iv, byte[] input, byte[] sharedKey) throws Throwable {
        byte[] decryptedText = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(sharedKey, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(DEFAULT_AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            decryptedText = cipher.doFinal(input);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(e);
        }
        return decryptedText;
    }
}
