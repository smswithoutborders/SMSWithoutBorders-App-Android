package com.example.sw0b_001.Data.GatewayServers

import android.util.Log
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA
import com.example.sw0b_001.Data.Vault_V2
import com.example.sw0b_001.Modules.Crypto
import com.example.sw0b_001.Modules.Network
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.result.Result
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

class GatewayServer_V2 {

    @Serializable
    data class HandshakeGatewayServerPayload(val msisdn_hash: String, val shared_key: String,
                                   val user_platforms: Vault_V2.Platforms)

    @Serializable
    data class HandshakeGatewayServerRequest(val password: String,
                                             val public_key: String,
                                             val mgf1ParameterSpec: String )

    companion object {
        private const val MGF1_PARAMETER_SPEC_VALUE = "sha256"
        fun sync(url: String, uid: String, password: String):
                HandshakeGatewayServerPayload{

            val pubKey = SecurityRSA.generateKeyPair(uid, 2048).encoded

            val publicKey = Crypto.convertToPemFormat(pubKey)

            val gatewayServerPublicKey = Crypto.getGatewayServerPublicKey(url)

//            val encryptedPassword = android.util.Base64
//                    .encodeToString(SecurityRSA.encrypt(gatewayServerPublicKey,
//                            password.encodeToByteArray()),
//                            android.util.Base64.DEFAULT)

            val encryptedPassword = android.util.Base64
                    .encodeToString(Crypto.encryptRSA(gatewayServerPublicKey,
                            password.encodeToByteArray()),
                            android.util.Base64.DEFAULT)

            val payload = Json.encodeToString(
                    HandshakeGatewayServerRequest(encryptedPassword,
                            publicKey, MGF1_PARAMETER_SPEC_VALUE))

            val networkResponseResults: Network.NetworkResponseResults =
                    Network.jsonRequest(url, payload)
            when(networkResponseResults.response.statusCode) {
                in 400..500 -> throw Exception("Invalid Creds")
                in 500..600 -> throw Exception("Server error")
                else -> return Json
                        .decodeFromString<HandshakeGatewayServerPayload>(
                            networkResponseResults.result.get())
            }
        }
    }
}