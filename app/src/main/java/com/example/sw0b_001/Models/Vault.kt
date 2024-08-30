package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
import at.favre.lib.armadillo.Armadillo
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA
import com.example.sw0b_001.Modules.Crypto
import com.example.sw0b_001.Security.Cryptography
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import vault.v1.EntityGrpc
import vault.v1.EntityGrpc.EntityBlockingStub
import vault.v1.Vault

class Vault {
    private val DEVICE_ID_KEYSTORE_ALIAS = "DEVICE_ID_KEYSTORE_ALIAS"

    private var channel: ManagedChannel = ManagedChannelBuilder
        .forAddress("staging.smswithoutborders.com", 9050)
        .useTransportSecurity()
        .build()
    private var entityStub: EntityBlockingStub = EntityGrpc.newBlockingStub(channel)

    private fun processLongLivedToken(context: Context, encodedLlt: String, deviceIdPubKey: String) {
        val sharedKey = Cryptography.calculateSharedSecret(
            context,
            DEVICE_ID_KEYSTORE_ALIAS,
            Base64.decode(deviceIdPubKey, Base64.DEFAULT))

        val llt = Crypto.decryptFernet(sharedKey,
            String(Base64.decode(encodedLlt, Base64.DEFAULT), Charsets.UTF_8))

        storeLongLivedToken(context, llt)
    }

    fun createEntity(context: Context,
                     phoneNumber: String,
                     countryCode: String,
                     password: String,
                     clientPublishPubKey: String,
                     clientDeviceIDPubKey: String,
                     ownershipResponse: String = "") : Vault.CreateEntityResponse {
        val createEntityRequest1 = Vault.CreateEntityRequest.newBuilder().apply {
            setCountryCode(countryCode)
            setPhoneNumber(phoneNumber)
            setPassword(password)
            setClientPublishPubKey(clientPublishPubKey)
            setClientDeviceIdPubKey(clientDeviceIDPubKey)

            if(ownershipResponse.isNotBlank()) {
                setOwnershipProofResponse(ownershipResponse)
            }
        }.build()

        try {
            val response = entityStub.createEntity(createEntityRequest1)

            if(!response.requiresOwnershipProof) {
                processLongLivedToken(context,
                    response.longLivedToken,
                    response.serverDeviceIdPubKey)
            }
            return response
        } catch(e: Exception) {
            throw Throwable(e)
        }
    }

    fun authenticateEntity(context: Context,
                           phoneNumber: String,
                           password: String,
                           clientPublishPubKey: String,
                           clientDeviceIDPubKey: String,
                           ownershipResponse: String = "") : Vault.AuthenticateEntityResponse {
        val authenticateEntityRequest = Vault.AuthenticateEntityRequest.newBuilder().apply {
            setPhoneNumber(phoneNumber)
            setPassword(password)
            setClientPublishPubKey(clientPublishPubKey)
            setClientDeviceIdPubKey(clientDeviceIDPubKey)

            if(ownershipResponse.isNotBlank()) {
                setOwnershipProofResponse(ownershipResponse)
            }
        }.build()

        try {
            val response = entityStub.authenticateEntity(authenticateEntityRequest)
            if(!response.requiresOwnershipProof) {
                processLongLivedToken(context,
                    response.longLivedToken,
                    response.serverDeviceIdPubKey)
            }
            return response
        } catch(e: Exception) {
            throw Throwable(e)
        }
    }

    fun recoverEntityPassword(context: Context,
                              phoneNumber: String,
                              newPassword: String,
                              clientPublishPubKey: String,
                              clientDeviceIDPubKey: String,
                              ownershipResponse: String? = null) : Vault.ResetPasswordResponse {
        val resetPasswordRequest = Vault.ResetPasswordRequest.newBuilder().apply {
            setPhoneNumber(phoneNumber)
            setNewPassword(newPassword)
            setClientPublishPubKey(clientPublishPubKey)
            setClientDeviceIdPubKey(clientDeviceIDPubKey)
            ownershipResponse?.let {
                setOwnershipProofResponse(ownershipResponse)
            }
        }.build()

        try {
            val response = entityStub.resetPassword(resetPasswordRequest)
            if(!response.requiresOwnershipProof) {
                processLongLivedToken(context,
                    response.longLivedToken,
                    response.serverDeviceIdPubKey)
            }
            return response
        } catch(e: Exception) {
            throw Throwable(e)
        }
    }

    fun deleteEntity(longLivedToken: String) : Vault.DeleteEntityResponse {
        val deleteEntityRequest = Vault.DeleteEntityRequest.newBuilder().apply {
            setLongLivedToken(longLivedToken)
        }.build()

        try {
            return entityStub.deleteEntity(deleteEntityRequest)
        } catch(e: Exception) {
            throw Throwable(e)
        }
    }

    companion object {
        private const val VAULT_ATTRIBUTE_FILES =
            "com.afkanerd.relaysms.VAULT_ATTRIBUTE_FILES"

        private const val LONG_LIVED_TOKEN_KEYSTORE_ALIAS =
            "com.afkanerd.relaysms.LONG_LIVED_TOKEN_KEYSTORE_ALIAS"

        fun storeLongLivedToken(context: Context, llt: String) {
            val publicKey = SecurityRSA.generateKeyPair(LONG_LIVED_TOKEN_KEYSTORE_ALIAS, 2048)
            val privateKeyCipherText = SecurityRSA.encrypt(publicKey, llt.encodeToByteArray())

            val sharedPreferences = Armadillo.create(context, VAULT_ATTRIBUTE_FILES)
                .encryptionFingerprint(context)
                .build()

            sharedPreferences.edit().putString(LONG_LIVED_TOKEN_KEYSTORE_ALIAS,
                Base64.encodeToString(privateKeyCipherText, Base64.DEFAULT)).apply()
        }

        fun fetchLongLivedToken(context: Context) : String {
            return Armadillo.create(context, VAULT_ATTRIBUTE_FILES)
                .encryptionFingerprint(context)
                .build()
                .getString(LONG_LIVED_TOKEN_KEYSTORE_ALIAS, "")!!
        }
    }
}