package com.example.sw0b_001

import android.util.Base64
import com.example.sw0b_001.Modules.Crypto
import com.example.sw0b_001.Modules.Helpers
import com.example.sw0b_001.Security.SecurityCurve25519
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import publisher.v1.PublisherGrpc
import publisher.v1.PublisherGrpc.PublisherBlockingStub
import publisher.v1.PublisherGrpc.PublisherStub
import publisher.v1.PublisherOuterClass
import publisher.v1.PublisherOuterClass.GetOAuth2AuthorizationUrlResponse
import vault.v1.EntityGrpc
import vault.v1.EntityGrpc.EntityBlockingStub
import vault.v1.Vault
import vault.v1.Vault.AuthenticateEntityResponse

/**
 * Flow from https://github.com/smswithoutborders/SMSwithoutborders-BE/blob/feature/grpc_api/docs/grpc.md
 * 1. Create Account
 * https://github.com/smswithoutborders/SMSwithoutborders-BE/blob/feature/grpc_api/docs/grpc.md#create-an-entity
 *
 * 2. Complete Account creation
 * https://github.com/smswithoutborders/SMSwithoutborders-BE/blob/feature/grpc_api/docs/grpc.md#complete-creation
 *
 * 3. Authenticate an entity
 * https://github.com/smswithoutborders/SMSwithoutborders-BE/blob/feature/grpc_api/docs/grpc.md#authenticate-an-entity
 *
 * 4. Store entities
 * https://github.com/smswithoutborders/SMSwithoutborders-BE/blob/feature/grpc_api/docs/grpc.md#store-an-entitys-token
 *
 * 5. List stored entities
 * https://github.com/smswithoutborders/SMSwithoutborders-BE/blob/feature/grpc_api/docs/grpc.md#list-an-entitys-stored-tokens
 */

/**
 * Docs: https://grpc.github.io/grpc/core/md_doc_statuscodes.html
 */
class gRPCTest {

    private lateinit var channel: ManagedChannel
    private lateinit var publisherChannel: ManagedChannel

    private lateinit var entityStub: EntityBlockingStub
    private lateinit var publisherStub: PublisherBlockingStub

    private val globalPhoneNumber = "+2371123457"
    private val globalCountryCode = "CM"
    private val globalPassword = "dMd2Kmo9#"
    private val deviceIdPubKey = SecurityCurve25519().generateKey()
    private val publishPubKey = SecurityCurve25519().generateKey()

    private val vault = com.example.sw0b_001.Models.Vault()

    @Before
    fun init() {
    }

    @Test
    fun endToEndCompleteTest() {
        try {
            println("Starting")
            var response = vault.createEntity(globalPhoneNumber,
                globalCountryCode,
                globalPassword,
                Base64.encodeToString(publishPubKey.publicKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey.publicKey, Base64.DEFAULT))

            assertTrue(response.requiresOwnershipProof)

            var response1 = vault.createEntity(globalPhoneNumber,
                globalCountryCode,
                globalPassword,
                Base64.encodeToString(publishPubKey.publicKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey.publicKey, Base64.DEFAULT),
                "123456")

        } catch(e: StatusRuntimeException) {
            println("Exception code: ${e.status.code.value()}")
            println("Exception code: ${e.status.description}")
            when(e.status.code.value()) {
                3 -> {
                    println("invalid arg - code is ${e.status.code.value()}")
                    throw e
                }
                6 -> { println("already exist - code is ${e.status.code.value()}")}
            }
        } catch(e: Exception) {
            println("Regular exception requested")
        }

        try {
            var response4 = vault.recoverEntityPassword(globalPhoneNumber,
                globalPassword,
                Base64.encodeToString(publishPubKey.publicKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey.publicKey, Base64.DEFAULT))

            assertTrue(response4.requiresOwnershipProof)

            var response5 = vault.recoverEntityPassword(globalPhoneNumber,
                globalPassword,
                Base64.encodeToString(publishPubKey.publicKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey.publicKey, Base64.DEFAULT),
                "123456")

            var response2 = vault.authenticateEntity(globalPhoneNumber,
                globalPassword,
                Base64.encodeToString(publishPubKey.publicKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey.publicKey, Base64.DEFAULT))

            assertTrue(response2.first.requiresOwnershipProof)

            var response3 = vault.authenticateEntity(globalPhoneNumber,
                globalPassword,
                Base64.encodeToString(publishPubKey.publicKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey.publicKey, Base64.DEFAULT),
                "123456",
                deviceIdPubKey)

            var response6 = vault.deleteEntity(response3.second)
        } catch(e: StatusRuntimeException) {
            println("Exception code: ${e.status.code.value()}")
            println("Exception code: ${e.status.description}")
            when(e.status.code.value()) {
                3 -> {
                    println("invalid arg - code is ${e.status.code.value()}")
                    throw e
                }
                6 -> { println("already exist - code is ${e.status.code.value()}")}
            }
        } catch(e: Exception) {
            throw e
        }
    }
}