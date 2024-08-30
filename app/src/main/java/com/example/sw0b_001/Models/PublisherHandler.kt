package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
import android.util.Log
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
import com.example.sw0b_001.Security.SecurityAES
import java.nio.charset.Charset

object PublisherHandler {
    private fun encryptContentForPublishing(context: Context, emailContent: String): ByteArray {
        val credentials = UserArtifactsHandler.fetchCredentials(context)
        val keypair = KeystoreHelpers
                .getKeyPairFromKeystore(credentials[UserArtifactsHandler.USER_ID_KEY])

//        val sharedKeyDecrypted = SecurityRSA.decrypt(keypair.private,
//                Base64.decode(credentials[UserArtifactsHandler.SHARED_KEY], Base64.DEFAULT))

//        val sharedKeyDecrypted = com.afkanerd.smswithoutborders.libsignal_doubleratchet
//                .SecurityRSA.decrypt(keypair.private,
//                        Base64.decode(credentials[UserArtifactsHandler.SHARED_KEY], Base64.DEFAULT))
        val sharedKeyDecrypted = UserArtifactsHandler.getSharedKeyDecrypted(context)
        Log.d(javaClass.name, "Decrypted sharedkey: ${String(sharedKeyDecrypted)}")
        return SecurityAES.encrypt(emailContent.toByteArray(Charset.defaultCharset()), sharedKeyDecrypted)
    }

    fun formatForPublishing(context: Context, formattedContent: String): String {
        return try {
            val encryptedContent = encryptContentForPublishing(context, formattedContent)
            val iv = ByteArray(16)
            val content = ByteArray(encryptedContent.size - 16)

            System.arraycopy(encryptedContent, 0, iv, 0, iv.size)
            System.arraycopy(encryptedContent, 16, content, 0, content.size)

            val encodedContent = Base64.encode(content, Base64.DEFAULT)
            Log.d(javaClass.name, "Encrypted content: ${Base64.encodeToString(content, 
                    Base64.DEFAULT)}")
            val finalContent = ByteArray(16 + encodedContent.size)

            System.arraycopy(iv, 0, finalContent, 0, iv.size)
            System.arraycopy(encodedContent, 0, finalContent, 16, encodedContent.size)

            Base64.encodeToString(finalContent, Base64.DEFAULT)
        } catch (e: Exception) {
            throw Throwable(e)
        }
    }
}
