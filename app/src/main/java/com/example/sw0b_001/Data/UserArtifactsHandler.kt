package com.example.sw0b_001.Data

import android.content.Context
import android.util.Base64
import android.util.Pair
import at.favre.lib.armadillo.Armadillo
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA


class UserArtifactsHandler {
    companion object {
        private const val PREF_MASTER_KEY_ALIAS = "PREF_MASTER_KEY_ALIAS"
        private const val PREF_USER_ARTIFACTS_FILE= "PREF_USER_ARTIFACTS_FILE"

        private const val USER_ID_KEY = "USER_ID_KEY"
        private const val PHONE_NUMBER = "PHONE_NUMBER"
        private const val PASSWORD = "PASSWORD"

        fun fetchCredentials(context: Context) : android.util.Pair<String, ByteArray> {
            val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                    .encryptionFingerprint(context)
                    .build()

            val phoneNumber = sharedPreferences.getString(PHONE_NUMBER, "")
            if(phoneNumber == "")
                throw Exception("No Phone number for fetching")

            val encodedPasswordCipher = sharedPreferences.getString(PASSWORD, "")
            if(encodedPasswordCipher == "")
                throw Exception("No password for fetching")

            val decodedPasswordCipher = Base64.decode(encodedPasswordCipher, Base64.DEFAULT)

            val keyPair = KeystoreHelpers.getKeyPairFromKeystore(phoneNumber)

            return Pair<String, ByteArray>(phoneNumber,
                    SecurityRSA.decrypt(keyPair.private, decodedPasswordCipher))
        }

        fun storeCredentials(context: Context, phoneNumber: String, password: String) {
            val publicKey = SecurityRSA.generateKeyPair(phoneNumber, 2048)
            val passwordCipher = SecurityRSA.encrypt(publicKey, password.encodeToByteArray())
            val encodedPasswordCipher = Base64.encodeToString(passwordCipher, Base64.DEFAULT)

            // https://github.com/patrickfav/armadilloI
            val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                    .encryptionFingerprint(context)
                    .build()

            sharedPreferences.edit()
                    .putString(PHONE_NUMBER, phoneNumber)
                    .putString(PASSWORD, encodedPasswordCipher)
                    .apply()
        }

        fun storeUID(context: Context, uid: String) {
            // https://github.com/patrickfav/armadilloI
            val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                    .encryptionFingerprint(context)
                    .build()

            sharedPreferences.edit().putString(USER_ID_KEY, uid).apply()
        }
    }
}