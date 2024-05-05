package com.example.sw0b_001.Security

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SecurityAES {
    companion object {
        private const val defaultAlgorithm = "AES/CBC/PKCS5Padding"
        fun encrypt(input: ByteArray, sharedKey: ByteArray): ByteArray {
            return try {
                val secretKeySpec = SecretKeySpec(sharedKey, "AES")
                val cipher = Cipher.getInstance(defaultAlgorithm)

                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
                val cipherText = cipher.doFinal(input)
                return cipher.iv + cipherText

            } catch (e: Exception) {
                throw e;
            }
        }

        fun decrypt(input: ByteArray, sharedKey: ByteArray?): ByteArray {
            val secretKeySpec = SecretKeySpec(sharedKey, "AES")

            val iv: ByteArray = input.copyOfRange(0, 16)
            val content: ByteArray = input.slice(16..Int.MAX_VALUE).toByteArray()

            val ivParameterSpec = IvParameterSpec(iv)
            val cipher = Cipher.getInstance(defaultAlgorithm)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
            return cipher.doFinal(content)
        }
    }
}
