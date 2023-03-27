package com.example.sw0b_001.Security;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.Models.PublisherHandler;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;

public class SecurityRSA extends SecurityHandler {
    CancellationSignal cancellationSignal = new CancellationSignal();

    private Executor executor;
    private android.hardware.biometrics.BiometricPrompt biometricPrompt;

    public SecurityRSA(Context context) throws GeneralSecurityException, IOException {
        super(context);

    }

    public KeyPairGenerator generateKeyPair(String keystoreAlias) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, DEFAULT_KEYSTORE_PROVIDER);

        keygen.initialize(
                new KeyGenParameterSpec.Builder(
                        keystoreAlias,
                        KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                        .setKeySize(2048)
                        .setDigests(KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .build());
        return keygen;
    }

    public static PublicKey getPublicKeyFromBase64String(String publicKeyBase64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        return pubKey;
    }

//    public byte[] encrypt(byte[] input, String publicKeyBase64) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeySpecException {
//        PublicKey publicKey = getPublicKeyFromBase64String(publicKeyBase64);
//        Cipher cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey, encryptionDigestParam);
//        return cipher.doFinal(input);
//    }
//
    public byte[] encrypt(byte[] input, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, encryptionDigestParam);
        return cipher.doFinal(input);
    }

//    public byte[] encrypt(byte[] input, PublicKey publicKey, OAEPParameterSpec oaepParameterSpec) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableEntryException, CertificateException, KeyStoreException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeySpecException {
//        Cipher cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParameterSpec);
//        return cipher.doFinal(input);
//    }

    // Requirements to use this: input has to be Base64 encoded
    public byte[] decrypt(byte[] encryptedInput, String keyStoreAlias) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        byte[] decryptedBytes = null;
        try {
            KeyStore keyStore = getKeyStore();
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(
                    keyStoreAlias, null);

            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            Cipher cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING);

            cipher.init(Cipher.DECRYPT_MODE, privateKey, decryptionDigestParam);

            decryptedBytes = cipher.doFinal(encryptedInput);
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
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decryptedBytes;
    }


}
