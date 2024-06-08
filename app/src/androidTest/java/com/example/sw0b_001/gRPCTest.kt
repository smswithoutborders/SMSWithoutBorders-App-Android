package com.example.sw0b_001

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import org.junit.Before
import org.junit.Test
import vault.v1.EntityGrpc
import vault.v1.EntityGrpc.EntityBlockingStub
import vault.v1.EntityGrpc.EntityFutureStub
import vault.v1.EntityGrpc.EntityStub
import vault.v1.Vault1.CreateEntityRequest
import java.net.Inet6Address

class gRPCTest {
    lateinit var channel: ManagedChannel
    lateinit var entityStub: EntityBlockingStub

    val globalPhoneNumber = "+237123456789"

    @Before
    fun init() {
        channel = ManagedChannelBuilder
            .forAddress("staging.smswithoutborders.com", 9050)
            .useTransportSecurity()
            .build()

        entityStub = EntityGrpc.newBlockingStub(channel)
    }

    @Test
    fun vaultTestCreateEntity() {
        val createEntityRequest1 = CreateEntityRequest.newBuilder().apply {
            setPhoneNumber(globalPhoneNumber)
        }.build()

        try {
            val createResponse = entityStub.createEntity(createEntityRequest1)
            assert(createResponse.requiresOwnershipProof)
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

    @Test
    fun vaultTestCreateEntity2() {
        vaultTestCreateEntity()
        val createEntityRequest2 = CreateEntityRequest.newBuilder().apply {
            setCountryCode("CM")
            setPhoneNumber(globalPhoneNumber)
            setPassword("dMd2Kmo9")
            setClientPublishPubKey("dMd2Kmo9")
            setClientDeviceIdPubKey("dMd2Kmo9")
            setOwnershipProofResponse("123456")
        }.build()

        try {
            val createResponse = entityStub.createEntity(createEntityRequest2)
            assert(createResponse.requiresOwnershipProof)
            println(createResponse.message)
        } catch(e: Exception) {
            if(e is StatusRuntimeException) {
                when(e.status.code.toString()) {
                    "UNAUTHENTICATED" -> {
                        println(e.message)
                    }
                }
            }
            throw e
        }
    }

}