package com.example.sw0b_001.Security;

import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class SecurityHelpers {
    public static PublicKey getRSAPublicKeyFromB64(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    @NonNull
    public static String convert_to_pem_format(byte[] key) {
        String keyString = Base64.encodeToString(key, Base64.DEFAULT);
        keyString = "-----BEGIN PUBLIC KEY-----\n" + keyString;
        keyString += "-----END PUBLIC KEY-----";

        return keyString;
    }

    public static boolean canSign(PrivateKey privateKey, String keyStoreAlias) throws UnrecoverableEntryException, KeyStoreException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(privateKey.getAlgorithm(), "AndroidKeyStore");
        KeyInfo keyInfo = keyFactory.getKeySpec(privateKey, KeyInfo.class);

        return keyInfo.getPurposes() == (KeyProperties.PURPOSE_SIGN
                | KeyProperties.PURPOSE_ENCRYPT
                | KeyProperties.PURPOSE_DECRYPT);
    }

    public static byte[] sign(PrivateKey privateKey, byte[] message) throws
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("SHA512withRSA/PSS");
        s.initSign(privateKey);
        s.update(message);
        return s.sign();
    }
}
