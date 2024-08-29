package com.example.sw0b_001

import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sw0b_001.Security.Cryptography
import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import publisher.v1.PublisherGrpc.PublisherBlockingStub
import vault.v1.EntityGrpc.EntityBlockingStub

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

    private lateinit var deviceIdPubKey: ByteArray
    private lateinit var publishPubKey: ByteArray

    private val vault = com.example.sw0b_001.Models.Vault()

    var context = InstrumentationRegistry.getInstrumentation().targetContext
    private val device_id_keystoreAlias = "device_id_keystoreAlias"
    private val publisher_keystoreAlias = "publisher_keystoreAlias"

    @Before
    fun init() {
        deviceIdPubKey = Cryptography.generateKey(context, device_id_keystoreAlias)
        publishPubKey = Cryptography.generateKey(context, publisher_keystoreAlias)
    }

    @Test
    fun endToEndCompleteTest() {
        try {
            println("Starting")
            var response = vault.createEntity(globalPhoneNumber,
                globalCountryCode,
                globalPassword,
                Base64.encodeToString(publishPubKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey, Base64.DEFAULT))

            assertTrue(response.requiresOwnershipProof)

            var response1 = vault.createEntity(globalPhoneNumber,
                globalCountryCode,
                globalPassword,
                Base64.encodeToString(publishPubKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey, Base64.DEFAULT),
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
                Base64.encodeToString(publishPubKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey, Base64.DEFAULT))

            assertTrue(response4.requiresOwnershipProof)

            var response5 = vault.recoverEntityPassword(globalPhoneNumber,
                globalPassword,
                Base64.encodeToString(publishPubKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey, Base64.DEFAULT),
                "123456")

            var response2 = vault.authenticateEntity(context,
                globalPhoneNumber,
                globalPassword,
                Base64.encodeToString(publishPubKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey, Base64.DEFAULT))

            assertTrue(response2.first.requiresOwnershipProof)

            var response3 = vault.authenticateEntity(context,
                globalPhoneNumber,
                globalPassword,
                Base64.encodeToString(publishPubKey, Base64.DEFAULT),
                Base64.encodeToString(deviceIdPubKey, Base64.DEFAULT),
                "123456")

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