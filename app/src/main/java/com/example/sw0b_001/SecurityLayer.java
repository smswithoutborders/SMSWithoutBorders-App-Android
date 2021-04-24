package com.example.sw0b_001;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SecurityLayer {
    private Cipher cipher;
    private byte[] IV = null;
    private SecretKey key;

    int KEY_SIZE=256;

    public void init(){
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(KEY_SIZE);
            key = keygen.generateKey();

            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String input) throws BadPaddingException, IllegalBlockSizeException {
        byte[] ciphertext = cipher.doFinal(input.getBytes());
        this.IV = cipher.getIV();
        return ciphertext;
    }

    public byte[] decrypt(byte[] input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        IvParameterSpec iv = new IvParameterSpec(this.IV);
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
        return this.IV;
    }
}
