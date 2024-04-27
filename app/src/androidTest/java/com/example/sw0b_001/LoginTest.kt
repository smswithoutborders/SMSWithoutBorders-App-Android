package com.example.sw0b_001

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sw0b_001.Models.v2.GatewayServer_V2
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Network
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Properties


@RunWith(AndroidJUnit4::class)
class LoginTest {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val properties: Properties = Properties()
    val inputStream = context.resources.openRawResource(R.raw.v2)

    @Before
    fun initialize() {
        properties.load(inputStream)
    }

    @Test
    fun loginTest() {
        login()
    }

    private fun login(): Network.NetworkResponseResults {
        val phonenumber = "+237123456789"
        val password = "dummy_password"
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val networkResponseResults = Vault_V2.login(phonenumber, password, url, "")
        val expectedUID = "a81d750e-a733-11ee-92f4-0242ac17000a"
        assertEquals(expectedUID, Json.decodeFromString<Vault_V2.UID>(
                networkResponseResults.result.get()).uid)

        return networkResponseResults
    }

    @Test
    fun getPlatformsTest()  {
        val networkResponseResults = login()

        val url = context.getString(R.string.smswithoutborders_official_vault)
        val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid
        val platforms = Vault_V2.getPlatforms(url, networkResponseResults.response.headers, uid)
        platforms.saved_platforms.forEach {
            println("Saved: ${it.name}")
        }
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

    @Test
    private fun testLoginFlow() {

    }
}