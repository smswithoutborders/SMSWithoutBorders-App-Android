package com.example.sw0b_001.Models

import android.util.Base64
import com.example.sw0b_001.Modules.Crypto
import com.example.sw0b_001.Security.SecurityCurve25519
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import publisher.v1.PublisherGrpc
import publisher.v1.PublisherGrpc.PublisherBlockingStub
import vault.v1.EntityGrpc
import vault.v1.EntityGrpc.EntityBlockingStub
import vault.v1.Vault

class Vault {
    private lateinit var channel: ManagedChannel
    private lateinit var entityStub: EntityBlockingStub

    private val deviceIdPubKey = SecurityCurve25519().generateKey()
    private val publishPubKey = SecurityCurve25519().generateKey()

    fun init() {
        channel = ManagedChannelBuilder
            .forAddress("staging.smswithoutborders.com", 9050)
            .useTransportSecurity()
            .build()

        entityStub = EntityGrpc.newBlockingStub(channel)
    }

    fun createEntity(phoneNumber: String, countryCode: String, password: String,
                     clientPublishPubKey: String, clientDeviceIDPubKey: String,
                     ownershipResponse: String = "") {
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
            val createResponse = entityStub.createEntity(createEntityRequest1)
            println(createResponse.message)
        } catch(e: Exception) {
            if(e is StatusRuntimeException) {
                when(e.status.code.toString()) {
                    "INVALID_ARGUMENT" -> {
                        println(e.message)
                        throw e
                    }
                    "ALREADY_EXISTS" -> {
                        println(e.message)
                    }
                }
            }
        }
    }

    fun authenticateEntity(phoneNumber: String, password: String,
                           clientPublishPubKey: String,
                           clientDeviceIDPubKey: String, ownershipResponse: String = "") : String {
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

            val sharedKey = SecurityCurve25519().calculateSharedSecret(
                Base64.decode(createResponse.serverDeviceIdPubKey, Base64.DEFAULT), deviceIdPubKey)

            return Crypto.decryptFernet(sharedKey,
                String(Base64.decode(createResponse.longLivedToken, Base64.DEFAULT), Charsets.UTF_8))
        } catch(e: Exception) {
            if(e is StatusRuntimeException) {
                when(e.status.code.toString()) {
                    "INVALID_ARGUMENT" -> {
                        println(e.message)
                        throw e
                    }
                    "ALREADY_EXISTS" -> {
                        println(e.message)
                    }
                }
            }
            throw e
        }
    }

    fun recoverEntityPassword(phoneNumber: String, newPassword: String,
                           clientPublishPubKey: String,
                           clientDeviceIDPubKey: String) {
        val resetPasswordRequest = Vault.ResetPasswordRequest.newBuilder().apply {
            setPhoneNumber(phoneNumber)
            setNewPassword(newPassword)
            setClientPublishPubKey(clientPublishPubKey)
            setClientDeviceIdPubKey(clientDeviceIDPubKey)
        }.build()

        try {
            val createResponse = entityStub.resetPassword(resetPasswordRequest)
            println(createResponse.message)
        } catch(e: Exception) {
            if(e is StatusRuntimeException) {
                when(e.status.code.toString()) {
                    "INVALID_ARGUMENT" -> {
                        println(e.message)
                        throw e
                    }
                    "ALREADY_EXISTS" -> {
                        println(e.message)
                    }
                }
            }
        }
    }

    fun deleteEntity(longLivedToken: String) {
        val deleteEntityRequest = Vault.DeleteEntityRequest.newBuilder().apply {
            setLongLivedToken(longLivedToken)
        }.build()

        try {
            val createResponse = entityStub.deleteEntity(deleteEntityRequest)
            println(createResponse.message)
        } catch(e: Exception) {
            if(e is StatusRuntimeException) {
                when(e.status.code.toString()) {
                    "INVALID_ARGUMENT" -> {
                        println(e.message)
                        throw e
                    }
                    "ALREADY_EXISTS" -> {
                        println(e.message)
                    }
                }
            }
        }
    }


}