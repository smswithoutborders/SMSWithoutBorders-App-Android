package com.example.sw0b_001.Security;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.sw0b_001.BuildConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityAES extends SecurityHandler {

    public SecurityAES() throws CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException, NoSuchPaddingException {
        super();
    }

    public SecurityAES(Context context) throws GeneralSecurityException, IOException {
        super(context);
    }

    public byte[] encrypt(byte[] input, byte[] sharedKey) throws Throwable {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(sharedKey, "AES");

            Cipher cipher = Cipher.getInstance(DEFAULT_AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] ciphertext = cipher.doFinal(input);

            byte[] cipherTextIv = new byte[16 + ciphertext.length];
            System.arraycopy(cipher.getIV(), 0,  cipherTextIv, 0, 16);
            System.arraycopy(ciphertext, 0,  cipherTextIv, 16, ciphertext.length);

            return cipherTextIv;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(e);
        }
    }

    public byte[] decrypt(byte[] input, byte[] sharedKey) throws Throwable {
        byte[] decryptedText = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(sharedKey, "AES");

            byte[] iv = new byte[16];
            System.arraycopy(input, 0, iv, 0, 16);

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
