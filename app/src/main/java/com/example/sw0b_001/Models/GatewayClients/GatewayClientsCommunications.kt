package com.example.sw0b_001.Models.GatewayClients

import android.content.Context
import android.content.SharedPreferences
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.httpGet
import kotlinx.serialization.Serializable

class GatewayClientsCommunications(context: Context) {

    @Serializable
    data class GatewayClient(val msisdn: String, val country: String,
                             val protocol: ArrayList<String>)

    private val filename = "gateway_client_prefs"
    private val defaultKey = "DEFAULT_KEY"

    val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(filename, Context.MODE_PRIVATE)

    fun updateDefaultGatewayClient(msisdn: String) {
        sharedPreferences.edit()
                .putString(defaultKey, msisdn)
                .commit()
    }

    fun getDefaultGatewayClient(): String? {
        return sharedPreferences.getString(defaultKey, "")
    }

    companion object {
        fun fetchRemote(url: String): ResponseResultOf<String> {
            return url.httpGet()
                    .responseString()
        }

    }
}