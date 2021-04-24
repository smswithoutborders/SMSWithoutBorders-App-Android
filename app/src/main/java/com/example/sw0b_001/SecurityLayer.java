package com.example.sw0b_001;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityLayer {
    private Cipher cipher;
    private SecretKeySpec key;

//    private byte[] iv;
    private IvParameterSpec iv;

    int KEY_SIZE=256;

    public SecurityLayer(){
        try {
//            KeyGenerator keygen = KeyGenerator.getInstance("AES");
//            keygen.init(KEY_SIZE);
//            key = keygen.generateKey();

            byte[] plainTextByte = "c4a15a90-57d4-4935-b5ae-ba89df8e".getBytes();
            key = new SecretKeySpec(plainTextByte, "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            iv = new IvParameterSpec("1234567890123456".getBytes());
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
