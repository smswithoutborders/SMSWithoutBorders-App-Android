package com.example.sw0b_001

import io.grpc.ManagedChannelBuilder
import org.junit.Test
import vault.v1.EntityGrpc
import vault.v1.EntityGrpc.EntityBlockingStub
import vault.v1.EntityGrpc.EntityFutureStub
import vault.v1.EntityGrpc.EntityStub
import vault.v1.Vault1.CreateEntityRequest

class gRPCTest {

    @Test
    fun vaultV1Test() {
        val channel = ManagedChannelBuilder
            .forAddress("staging.smswithoutborders.com", 6000).usePlaintext().build()

        val stub = EntityStub.newStub(null, channel)

        val createEntityRequest1 = CreateEntityRequest.newBuilder().apply {
            setPhoneNumber("+237123456789")
        }

        val createEntityRequest2 = CreateEntityRequest.newBuilder().apply {
            setCountryCode("CM")
            setPhoneNumber("+237123456789")
            setPassword("dMd2Kmo9")
            setClientPublishPubKey("dMd2Kmo9")
            setClientDeviceIdPubKey("dMd2Kmo9")
            setOwnershipProofResponse("dMd2Kmo9")
        }
    }
}