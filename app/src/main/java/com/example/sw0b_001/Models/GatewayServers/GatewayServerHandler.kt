package com.example.sw0b_001.Models.GatewayServers

import android.content.Context
import com.example.sw0b_001.Security.SecurityRSA
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.PublicKey
import java.util.Base64

class GatewayServerHandler {


    @Serializable
    data class SyncRequest(val public_key: String, val mgf1ParameterSpec: String,
                           val password: String)
    companion object {
        fun sync(context: Context, password: ByteArray, gatewayServerPublicKey: PublicKey,
                 gatewayServerVerificationUrl: String, publicKey: PublicKey) {
            val encryptedPassword = SecurityRSA(context)
                    .encrypt(password, gatewayServerPublicKey)

            val passwordEncoded = android.util.Base64.encodeToString(encryptedPassword,
                    android.util.Base64.DEFAULT)
            val publicKeyEncoded = android.util.Base64.encodeToString(publicKey.encoded,
                    android.util.Base64.DEFAULT)

            val payload = Json.encodeToString(SyncRequest(publicKeyEncoded,
                    "sha256", passwordEncoded))

            val (_, response, result) = Fuel.post(gatewayServerVerificationUrl)
                    .jsonBody(payload)
                    .responseString()
        }

    }
}