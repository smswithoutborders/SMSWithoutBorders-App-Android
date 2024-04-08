package com.example.sw0b_001.Models.GatewayServers

import android.content.Context
import android.util.Log
import com.example.sw0b_001.Models.BackendCommunications
import com.example.sw0b_001.Security.SecurityHandler
import com.example.sw0b_001.Security.SecurityHelpers
import com.example.sw0b_001.Security.SecurityRSA
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URL
import java.security.PublicKey
import java.util.Base64

class GatewayServerHandler {


    @Serializable
    data class SyncRequest(val public_key: String, val mgf1ParameterSpec: String,
                           val password: String)

    @Serializable
    data class SyncPayload(val shared_key: String, val msisdn_hash: String)
    companion object {

        fun constructUrl(baseUrl: String, uid: String): String {
            return "${baseUrl}:15000/v2/sync/users/${uid}/sessions/000/"
        }
        fun getBaseUrl(url: String): String {
            val urlParts = URL(url)
            val host = urlParts.host
            val protocol = urlParts.protocol

            // Check for empty strings to handle potential invalid URLs
            return if (host.isEmpty() || protocol.isEmpty()) {
                ""
            } else {
                "$protocol://$host"
            }
        }

        fun sync(context: Context,
                 password: ByteArray,
                 gatewayServerPublicKey: PublicKey,
                 gatewayServerVerificationUrl: String,
                 publicKey: PublicKey): BackendCommunications.NetworkResponseResults{
            val encryptedPassword = SecurityRSA(context)
                    .encrypt(password, gatewayServerPublicKey)

            val passwordEncoded = android.util.Base64.encodeToString(encryptedPassword,
                    android.util.Base64.DEFAULT)
//            val publicKeyEncoded = android.util.Base64.encodeToString(publicKey.encoded,
//                    android.util.Base64.DEFAULT)
            val pemPublicKey = SecurityHelpers.convert_to_pem_format(publicKey.encoded)

            val payload = Json.encodeToString(SyncRequest(pemPublicKey,
                    "sha256", passwordEncoded))

            return try {
                val (_, response, result) = Fuel.post(gatewayServerVerificationUrl)
                        .jsonBody(payload)
                        .responseString()
                when(response.statusCode) {
                    200 -> {
                        /*
                        - shared_key
                        - msisdn_hash
                        - user_platforms
                         */
                        val json = Json { ignoreUnknownKeys = true }
                        val syncPayload = json.decodeFromString<SyncPayload>(result.get())
                        SecurityHandler(context).storeSharedKey(syncPayload.shared_key)
                        SecurityHandler(context).storeSharedKey(syncPayload.msisdn_hash)
                    }
                }
                BackendCommunications.NetworkResponseResults(response,
                        Result.Success(result.get()))
            } catch(e: Exception ) {
                Log.e(javaClass.name, "Status code not 200 error", e)
                BackendCommunications.NetworkResponseResults(null, Result.error(e))
            }
        }
    }
}