package com.example.sw0b_001

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sw0b_001.Models.BackendCommunications
import com.example.sw0b_001.Models.GatewayServers.GatewayServerHandler
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginTest {

    @Test
    fun loginTest() {
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val phonenumber = "+237123456789"
        val password = "dummy_password"

        val networkResponseResults = BackendCommunications.login(phonenumber, password, url)
        Log.d(javaClass.name, "Result data: " + networkResponseResults.result.get());
        assertEquals(200, networkResponseResults.response?.statusCode)
    }

    @Test
    fun getPlatformsTest()  {
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val phonenumber = "+237123456789"
        val password = "dummy_password"

        val networkResponseResults = BackendCommunications.login(phonenumber, password, url)
        Log.d(javaClass.name, "Result data: " + networkResponseResults.result.get())
        assertEquals(200, networkResponseResults.response?.statusCode)

        val obj = Json
                .decodeFromString<BackendCommunications.UID>(networkResponseResults.result.get())
        val uid = "a81d750e-a733-11ee-92f4-0242ac17000a"
        assertEquals(uid, obj.uid)

        val user = BackendCommunications(uid)
        val (_, response1, result1) = user
                .getPlatforms("https://staging.smswithoutborders.com:9000",
                        networkResponseResults.response!!.headers)
        assertEquals(200, response1.statusCode)
        Log.d(javaClass.name, "Platforms: " + result1.get())

        val platformsObjs = Json.decodeFromString<BackendCommunications.Platforms>(result1.get())
        assertFalse(platformsObjs.saved_platforms.isNullOrEmpty())
        assertFalse(platformsObjs.unsaved_platforms.isNullOrEmpty())
    }

    @Test
    fun makeSyncTest() {
        val uid = "a81d750e-a733-11ee-92f4-0242ac17000a"
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val password = "dummy_password"
        val url = "https://staging.smswithoutborders.com:15000/v2/sync/users/${uid}/sessions/000/"

        val gatewayServerPublicKey =
                SyncHandshakeActivity
                        .getGatewayServerPublicKey(GatewayServerHandler.getBaseUrl(url))
        val publicKey = SyncHandshakeActivity.getNewPublicKey(context,
                GatewayServerHandler.getBaseUrl(url))

        val networkResponseResults = GatewayServerHandler.sync(context,
                password.toByteArray(),
                gatewayServerPublicKey,
                url,
                publicKey)
        assertEquals(200, networkResponseResults.response?.statusCode)
        Log.d(javaClass.name, "Response: ${networkResponseResults.result.get()}")
    }
}