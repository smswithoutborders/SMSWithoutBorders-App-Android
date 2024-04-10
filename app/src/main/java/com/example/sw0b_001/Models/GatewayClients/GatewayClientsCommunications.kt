package com.example.sw0b_001.Models.GatewayClients

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.httpGet
import kotlinx.serialization.Serializable

class GatewayClientsCommunications {

    @Serializable
    data class GatewayClient(val msisdn: String, val country: String,
                             val protocol: ArrayList<String>)
    companion object {
        fun fetchRemote(url: String): ResponseResultOf<String> {
            return url.httpGet()
                    .responseString()
        }

        private const val FILENAME = "gateway_client_prefs"
        private const val DEFAULT_KEY = "DEFAULT_KEY"
        fun updateDefaultGatewayClient(context: Context, msisdn: String) {
            val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    FILENAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            // use the shared preferences and editor as you normally would
            sharedPreferences.edit()
                    .putString(DEFAULT_KEY, msisdn)
                    .apply()
        }

        fun getDefaultGatewayClient(context: Context): String? {
            val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    FILENAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            return sharedPreferences.getString(DEFAULT_KEY, "")
        }
    }
}