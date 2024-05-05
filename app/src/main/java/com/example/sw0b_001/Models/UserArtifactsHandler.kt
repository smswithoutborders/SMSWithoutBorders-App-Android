package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
import at.favre.lib.armadillo.Armadillo
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA


object UserArtifactsHandler {
    private const val PREF_MASTER_KEY_ALIAS = "PREF_MASTER_KEY_ALIAS"
    private const val PREF_USER_ARTIFACTS_FILE= "PREF_USER_ARTIFACTS_FILE"

    const val USER_ID_KEY = "USER_ID_KEY"
    const val PHONE_NUMBER = "PHONE_NUMBER"
    const val PASSWORD = "PASSWORD"
    const val SHARED_KEY = "SHARED_KEY"

    fun clearCredentials(context: Context) {
        val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                .encryptionFingerprint(context)
                .build()

        sharedPreferences.edit()
                .remove(PHONE_NUMBER)
                .remove(PASSWORD)
                .remove(USER_ID_KEY)
                .remove(SHARED_KEY)
                .apply()
    }

    fun isCredentials(context: Context): Boolean {
        val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                .encryptionFingerprint(context)
                .build()

        return sharedPreferences.contains(PHONE_NUMBER) &&
                sharedPreferences.contains(PASSWORD) &&
                sharedPreferences.contains(USER_ID_KEY) &&
                sharedPreferences.contains(SHARED_KEY)
    }

    fun fetchCredentials(context: Context) : Map<String, String> {
        val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                .encryptionFingerprint(context)
                .build()

        val phoneNumber = sharedPreferences.getString(PHONE_NUMBER, "")
        if(phoneNumber == "") throw Exception("No Phone number for fetching")

        val encodedPasswordCipher = sharedPreferences.getString(PASSWORD, "")
        if(encodedPasswordCipher == "") throw Exception("No password for fetching")

        val uid = sharedPreferences.getString(USER_ID_KEY, "")
        if(uid == "") throw Exception("No uid for fetching")

        val sharedKey = sharedPreferences.getString(SHARED_KEY, "")
        if(sharedKey == "") throw Exception("No shared key for fetching")

        val decodedPasswordCipher = Base64.decode(encodedPasswordCipher, Base64.DEFAULT)

        val keyPair = KeystoreHelpers.getKeyPairFromKeystore(phoneNumber)

//            return Pair<String, ByteArray>(phoneNumber,
//                    SecurityRSA.decrypt(keyPair.private, decodedPasswordCipher))
        val password = String(SecurityRSA.decrypt(keyPair.private, decodedPasswordCipher))

        val map = emptyMap<String, String>().toMutableMap()
        map[PHONE_NUMBER] = phoneNumber!!
        map[PASSWORD] = password
        map[USER_ID_KEY] = uid!!
        map[SHARED_KEY] = sharedKey!!
        return map.toMap()
    }

    fun storeCredentials(context: Context, phoneNumber: String, password: String, uid: String) {
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
                .putString(USER_ID_KEY, uid)
                .apply()
    }

    fun storeSharedKey(context: Context, sharedKey: String) {
        val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                .encryptionFingerprint(context)
                .build()

        sharedPreferences.edit()
                .putString(SHARED_KEY, sharedKey)
                .apply()
    }

    fun getSharedKeyDecrypted(context: Context) : ByteArray {
        val credentials = fetchCredentials(context)
        val sharedKey = Base64.decode(credentials[SHARED_KEY], Base64.DEFAULT)
        val privateKey = KeystoreHelpers.getKeyPairFromKeystore(credentials[USER_ID_KEY]).private
        return com.example.sw0b_001.Security.SecurityRSA.decrypt(privateKey, sharedKey)
    }
}