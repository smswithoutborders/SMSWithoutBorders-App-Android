package com.example.sw0b_001.Security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

object SecurityRSA {
    private const val DEFAULT_KEYSTORE_PROVIDER = "AndroidKeyStore"

//    private const val DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/" +
//            KeyProperties.ENCRYPTION_PADDING_RSA_OAEP

    private const val DEFAULT_KEYPAIR_ALGORITHM_PADDING = "RSA/ECB/" +
            KeyProperties.ENCRYPTION_PADDING_RSA_OAEP

    private val defaultEncryptionDigest = MGF1ParameterSpec.SHA256

    private val defaultDecryptionDigest = MGF1ParameterSpec.SHA1

    private val decryptionDigestParam = OAEPParameterSpec("SHA-256", "MGF1",
        defaultDecryptionDigest, PSource.PSpecified.DEFAULT)

    private var encryptionDigestParam = OAEPParameterSpec("SHA-256", "MGF1",
        defaultEncryptionDigest, PSource.PSpecified.DEFAULT)

    fun encrypt(input: ByteArray, publicKey: PublicKey): ByteArray {
//        val cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING)
        val cipher = Cipher.getInstance("RSA/ECB/" + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)

        cipher.init(Cipher.ENCRYPT_MODE, publicKey, encryptionDigestParam)

        return cipher.doFinal(input)
    }

    fun decrypt(privateKey: PrivateKey, encryptedInput: ByteArray): ByteArray {
//        val cipher = Cipher.getInstance(DEFAULT_KEYPAIR_ALGORITHM_PADDING)
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")

        cipher.init(Cipher.DECRYPT_MODE, privateKey, decryptionDigestParam)

        return cipher.doFinal(encryptedInput)
    }

    fun generateKeyPair(keystoreAlias: String, keySize: Int = 2048): KeyPair {
        val keygen = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, DEFAULT_KEYSTORE_PROVIDER)
        keygen.initialize(
            KeyGenParameterSpec.Builder( keystoreAlias,
                KeyProperties.PURPOSE_DECRYPT
                        or KeyProperties.PURPOSE_ENCRYPT
                        or KeyProperties.PURPOSE_SIGN)
                .setKeySize(keySize)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                .setDigests(KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA256,
                    KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .build())
        return keygen.generateKeyPair()
    }
}
