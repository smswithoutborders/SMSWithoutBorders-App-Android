package com.example.sw0b_001.Security

import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException
import java.security.UnrecoverableEntryException
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object SecurityHelpers {
    fun getRSAPublicKeyFromB64(publicKeyBase64: String?): PublicKey {
        val publicBytes = Base64.decode(publicKeyBase64, Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(publicBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    fun convert_to_pem_format(key: ByteArray?): String {
        var keyString = Base64.encodeToString(key, Base64.DEFAULT)
        keyString = "-----BEGIN PUBLIC KEY-----\n$keyString"
        keyString += "-----END PUBLIC KEY-----"
        return keyString
    }

    fun canSign(privateKey: PrivateKey, keyStoreAlias: String?): Boolean {
        val keyFactory = KeyFactory.getInstance(privateKey.algorithm, "AndroidKeyStore")
        val keyInfo = keyFactory.getKeySpec(privateKey, KeyInfo::class.java)
        return keyInfo.purposes == (KeyProperties.PURPOSE_SIGN
                or KeyProperties.PURPOSE_ENCRYPT
                or KeyProperties.PURPOSE_DECRYPT)
    }

    fun sign(privateKey: PrivateKey?, message: ByteArray?): ByteArray {
        val s = Signature.getInstance("SHA512withRSA/PSS")
        s.initSign(privateKey)
        s.update(message)
        return s.sign()
    }

    fun generateSecretKey(keyBytes: ByteArray, algorithm: String): SecretKey? {
        if (keyBytes.isEmpty()) {
            throw IllegalArgumentException("Empty byte array provided for key generation")
        }
        return try {
            SecretKeySpec(keyBytes, 0, keyBytes.size, algorithm)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
