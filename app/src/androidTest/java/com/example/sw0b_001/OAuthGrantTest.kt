package com.example.sw0b_001

import androidx.test.platform.app.InstrumentationRegistry
import com.example.sw0b_001.Data.v2.Vault_V2
import com.example.sw0b_001.Modules.Network
import junit.framework.TestCase
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.util.Properties

class OAuthGrantTest {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val properties: Properties = Properties()
    val inputStream = context.resources.openRawResource(R.raw.v2)

    @Before
    fun initialize() {
        properties.load(inputStream)
    }

    private fun login(): Network.NetworkResponseResults {
        val phonenumber = "+237123456789"
        val password = "dummy_password"
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val networkResponseResults = Vault_V2.login(phonenumber, password, url)
        return networkResponseResults
    }

    @Test
    fun oAuthGmailGrantTest() {
        val url = context.getString(R.string.smswithoutborders_official_vault)
        val networkResponseResults = login()

        val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid
        val expectedUID = "a81d750e-a733-11ee-92f4-0242ac17000a"
        TestCase.assertEquals(expectedUID, Json.decodeFromString<Vault_V2.UID>(
                networkResponseResults.result.get()).uid)

        val phonenumber = "+237123456789"
        val oAuthGrantPayload = Vault_V2
                .getGmailGrant(url, networkResponseResults.response.headers, uid, phonenumber)
        println("${oAuthGrantPayload.url} - ${oAuthGrantPayload.platform}")
    }

    @Test
    fun oAuthXGrantTest() {
        val url = context.getString(R.string.smswithoutborders_official_vault)
        val networkResponseResults = login()

        val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid
        val expectedUID = "a81d750e-a733-11ee-92f4-0242ac17000a"
        TestCase.assertEquals(expectedUID, Json.decodeFromString<Vault_V2.UID>(
                networkResponseResults.result.get()).uid)

        val phonenumber = "+237123456789"

        val oAuthGrantPayload = Vault_V2
                .getXGrant(url, networkResponseResults.response.headers, uid, phonenumber)
        println("${oAuthGrantPayload.url} - ${oAuthGrantPayload.platform}")
    }
}