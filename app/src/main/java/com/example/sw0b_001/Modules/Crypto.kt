package com.example.sw0b_001.Modules

import android.security.keystore.KeyProperties
import android.util.Base64
import com.example.sw0b_001.Security.SecurityRSA.Companion.encryptionDigestParam
import com.google.common.primitives.Bytes
import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.security.PublicKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.time.Instant
import javax.crypto.Cipher
import javax.net.ssl.HttpsURLConnection


object Crypto {
    fun getUrlPublicKey(urlInput: String): PublicKey {
        val urlBuild = URL(urlInput)
        val url = URL("${urlBuild.protocol}://${urlBuild.host}")
        val urlConnection = url.openConnection() as HttpsURLConnection
        val tmp: InputStream = BufferedInputStream(urlConnection.inputStream)
        val certificate: Array<Certificate> = urlConnection.serverCertificates
        return certificate[0].publicKey
    }

    fun convertToPemFormat(key: ByteArray?): String {
        var keyString: String = Base64.encodeToString(key, Base64.DEFAULT)
        keyString = "-----BEGIN PUBLIC KEY-----\n$keyString"
        keyString += "-----END PUBLIC KEY-----"
        return keyString
    }

    fun encryptRSA(publicKey: PublicKey?, data: ByteArray?): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/" + KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, encryptionDigestParam);
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    fun decryptFernet(key: ByteArray, data: String): String {
        val fKey = Key(key)
        val token = Token.fromString(data)
        val validator = object : StringValidator { }
        return token.validateAndDecrypt(fKey, validator)
    }
}