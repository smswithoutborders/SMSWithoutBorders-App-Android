package com.example.sw0b_001.Modules

import android.security.keystore.KeyProperties
import android.util.Base64
import com.example.sw0b_001.Security.SecurityRSA.Companion.encryptionDigestParam
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URL
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.cert.Certificate
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.net.ssl.HttpsURLConnection


class Crypto {
    companion object {
        fun getGatewayServerPublicKey(gatewayServerUrl: String): PublicKey {
            val urlBuild = URL(gatewayServerUrl)
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
    }

}