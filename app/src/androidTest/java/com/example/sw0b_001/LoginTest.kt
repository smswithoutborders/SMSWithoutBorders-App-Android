package com.example.sw0b_001

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA
import com.example.sw0b_001.Data.GatewayServers.GatewayServer_V2
import com.example.sw0b_001.Data.Vault_V2
import com.github.kittinunf.fuel.core.HttpException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith
import java.security.PublicKey


@RunWith(AndroidJUnit4::class)
class LoginTest {

    @Test
    fun loginTest() {
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val phonenumber = "+237123456789"
        val password = "dummy_password"

        val networkResponseResults = Vault_V2.login(phonenumber, password, url)
        Log.d(javaClass.name, "Result data: " + networkResponseResults.result.get());
        assertEquals(200, networkResponseResults.response?.statusCode)
    }

    @Test
    fun getPlatformsTest()  {
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val phonenumber = "+237123456789"
        val password = "dummy_password"

        val networkResponseResults = Vault_V2.login(phonenumber, password, url)
        Log.d(javaClass.name, "Result data: " + networkResponseResults.result.get())
        assertEquals(200, networkResponseResults.response?.statusCode)

        val obj = Json
                .decodeFromString<Vault_V2.UID>(networkResponseResults.result.get())
        val uid = "a81d750e-a733-11ee-92f4-0242ac17000a"
        assertEquals(uid, obj.uid)

        val user = Vault_V2(uid)
        val (_, response1, result1) = user
                .getPlatforms("https://staging.smswithoutborders.com:9000",
                        networkResponseResults.response!!.headers)
        assertEquals(200, response1.statusCode)
        Log.d(javaClass.name, "Platforms: " + result1.get())

        val platformsObjs = Json.decodeFromString<Vault_V2.Platforms>(result1.get())
        assertFalse(platformsObjs.saved_platforms.isNullOrEmpty())
        assertFalse(platformsObjs.unsaved_platforms.isNullOrEmpty())
    }

    @Test
    fun makeSyncTest() {
        val uid = "a81d750e-a733-11ee-92f4-0242ac17000a"
        val password = "dummy_password"
        val url = "https://staging.smswithoutborders.com:15000/v2/sync/users/${uid}/sessions/000/"

        val responsePayload = GatewayServer_V2.sync(url, uid, password)
        Log.d(javaClass.name, "Payload: ${responsePayload.msisdn_hash}, " +
                responsePayload.shared_key)
    }
}