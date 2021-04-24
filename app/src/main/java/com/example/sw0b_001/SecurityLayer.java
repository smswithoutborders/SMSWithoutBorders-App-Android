package com.example.sw0b_001;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
    private int KEY_SIZE=256;
    private KeyPair keyPair;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SecurityLayer() throws NoSuchAlgorithmException, UnsupportedEncodingException {
       this.init_AES();
       this.init_RSA();
    }

    private void init_RSA() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(KEY_SIZE);
        keyPair = keygen.generateKeyPair();
        System.out.println("[+] Public key: " + Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.URL_SAFE));
        System.out.println("[+] Private key: " + Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.URL_SAFE));
    }

    private void init_AES() {
        try {
            char[] charsArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '@', '#', '$', '%', '^', '*'};
            SecureRandom rand = new SecureRandom();
            StringBuilder password = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                password.append(charsArray[rand.nextInt(charsArray.length)]);
            }
            String strIV = password.toString();

            byte[] plainTextByte = "c4a15a90-57d4-4935-b5ae-ba89df8e".getBytes();
            key = new SecretKeySpec(plainTextByte, "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            iv = new IvParameterSpec(strIV.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String input) throws BadPaddingException, IllegalBlockSizeException {
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
