package com.example.sw0b_001

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sw0b_001.Models.BackendCommunications
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginTest {

    @Test
    fun loginTest() {
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val phonenumber = "+237123456789"
        val password = "dummy_password"

        val (_, response, result) = BackendCommunications.login(phonenumber, password, url)
        Log.d(javaClass.name, "Result data: " + result.get())
        assertEquals(200, response.statusCode)
    }

    @Test
    fun getPlatformsTest()  {
        val url = "https://staging.smswithoutborders.com:9000/v2/login"

        val phonenumber = "+237123456789"
        val password = "dummy_password"

        val (_, response, result) = BackendCommunications.login(phonenumber, password, url)
        Log.d(javaClass.name, "Result data: " + result.get())
        assertEquals(200, response.statusCode)

        val obj = Json.decodeFromString<BackendCommunications.UID>(result.get())
        val uid = "a81d750e-a733-11ee-92f4-0242ac17000a"
        assertEquals(uid, obj.uid)

        val user = BackendCommunications(uid)
        val (_, response1, result1) = user
                .getPlatforms("https://staging.smswithoutborders.com:9000", response.headers)
        assertEquals(200, response1.statusCode)
        Log.d(javaClass.name, "Platforms: " + result1.get())

        val platformsObjs = Json.decodeFromString<BackendCommunications.Platforms>(result1.get())
        assertFalse(platformsObjs.saved_platforms.isNullOrEmpty())
        assertFalse(platformsObjs.unsaved_platforms.isNullOrEmpty())
    }
}