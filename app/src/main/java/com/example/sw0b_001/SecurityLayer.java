package com.example.sw0b_001;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;

public class SecurityLayer {

    KeyStore keyStore;
    KeyPair generatedKeyPair;

    private static final String     AndroidKeyStore = "AndroidKeyStore";

    public PublicKey generateKeys(){
        try {
            KeyPairGenerator keyPairGenerated = KeyPairGenerator.getInstance("RSA");
            keyPairGenerated.initialize(512);
            generatedKeyPair = keyPairGenerated.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return generatedKeyPair.getPrivate();
    }
}
