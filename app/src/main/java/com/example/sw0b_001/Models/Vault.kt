package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
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

    fun createEntity(phoneNumber: String,
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

        return entityStub.createEntity(createEntityRequest1)
    }

    fun authenticateEntity(context: Context,
                           phoneNumber: String,
                           password: String,
                           clientPublishPubKey: String,
                           clientDeviceIDPubKey: String,
                           ownershipResponse: String = "") :
            Pair<Vault.AuthenticateEntityResponse, String> {
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
            val createResponse = entityStub.authenticateEntity(authenticateEntityRequest)
            println(createResponse.message)
            println(createResponse.serverDeviceIdPubKey)

            var llt = ""
            if(!createResponse.requiresOwnershipProof) {
                val sharedKey = Cryptography.calculateSharedSecret(
                    context,
                    DEVICE_ID_KEYSTORE_ALIAS,
                    Base64.decode(createResponse.serverDeviceIdPubKey, Base64.DEFAULT))

                llt = Crypto.decryptFernet(sharedKey,
                    String(Base64.decode(createResponse.longLivedToken, Base64.DEFAULT), Charsets.UTF_8))
            }
            return Pair(createResponse, llt)
        } catch(e: Exception) {
            throw Throwable(e)
        }
    }

    fun recoverEntityPassword(phoneNumber: String,
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
            return entityStub.resetPassword(resetPasswordRequest)
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


}