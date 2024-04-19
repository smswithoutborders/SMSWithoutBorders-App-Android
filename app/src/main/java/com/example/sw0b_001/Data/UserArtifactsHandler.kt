package com.example.sw0b_001.Data

import android.content.Context
import at.favre.lib.armadillo.Armadillo


class UserArtifactsHandler {
    companion object {
        private const val PREF_MASTER_KEY_ALIAS = "PREF_MASTER_KEY_ALIAS"
        private const val PREF_USER_ARTIFACTS_FILE= "PREF_USER_ARTIFACTS_FILE"

        private const val USER_ID_KEY = "USER_ID_KEY"
        fun storeUID(context: Context, uid: String) {
//            val masterKey: MasterKey = MasterKey.Builder(context)
//                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                    .build()

//            val sharedPreferences = EncryptedSharedPreferences.create(
//                    context,
//                    PREF_USER_ARTIFACTS_FILE,
//                    masterKey,
//                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//            )

            // https://github.com/patrickfav/armadilloI
            val sharedPreferences = Armadillo.create( context, PREF_USER_ARTIFACTS_FILE)
                    .encryptionFingerprint(context)
                    .build()

            sharedPreferences.edit().putString(USER_ID_KEY, uid).apply()
        }
    }
}