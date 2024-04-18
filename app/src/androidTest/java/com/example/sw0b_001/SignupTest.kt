package com.example.sw0b_001

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sw0b_001.Data.Vault_V2
import com.github.kittinunf.fuel.core.Headers
import junit.framework.TestCase
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Properties


@RunWith(AndroidJUnit4::class)
class SignupTest {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val properties: Properties = Properties()
    val inputStream = context.resources.openRawResource(R.raw.v2)

    @Before
    fun initialize() {
        properties.load(inputStream)
    }

    @Test
    fun signupTest() {

        val phonenumber = properties["phonenumber"].toString()
        val password = properties["password"].toString()
        val name = "dummy_user"
        val countryCode = "+237"

        val url = context.getString(R.string.smswithoutborders_official_site_signup)
        val networkResponseResults = Vault_V2.signup(url, phonenumber, name, countryCode, password)
        Log.d(javaClass.name, "Result data: " + networkResponseResults.result.get());
        TestCase.assertEquals(200, networkResponseResults.response?.statusCode)
        val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid
        Log.d(javaClass.name, "Users UID: $uid")

        val otpRequestUrl = context.getString(R.string.smswithoutborders_official_vault) +
                "/v2/users/$uid/OTP"
        Log.d(javaClass.name, "OTP request Url $otpRequestUrl")
        val optNetworkResponseResults = Vault_V2.otpRequest(otpRequestUrl,
                networkResponseResults.response.headers, countryCode + phonenumber)
        Log.d(javaClass.name, "OTP Request data: " + optNetworkResponseResults.result.get());
        Log.d(javaClass.name, "OTP Cookies: " + optNetworkResponseResults.response.headers
                .getValue(Headers.COOKIE))
        optNetworkResponseResults.response.headers.forEach{
            Log.d(javaClass.name, "${it.key}: ${it.value}")
        }
        TestCase.assertEquals(201, optNetworkResponseResults.response?.statusCode)
    }

    @Test
    fun signupTestOTPComplete() {
        val otpSubmissionUrl = context.getString(R.string.smswithoutborders_official_otp_submission)
        val code = properties["otp_code"].toString()
        val cookie = properties["otp_cookie"].toString()

        val headers = Headers()
                .set("OTP Request data", "{\"expires\":${properties["otp_expires"]}}")
                .set("OTP Cookies", "[]")
                .set("Set-Cookie", cookie)
        val networkResponseResults = Vault_V2.otpSubmit(otpSubmissionUrl, headers, code)
        Log.d(javaClass.name, "OTP Submission data: " + networkResponseResults.result.get());
        TestCase.assertEquals(200, networkResponseResults.response?.statusCode)

        val url = context.getString(R.string.smswithoutborders_official_site_signup)
        val completeNetworkResponseResults =
                Vault_V2.signupOtpComplete(url, networkResponseResults.response.headers)
        TestCase.assertEquals(200, completeNetworkResponseResults.response?.statusCode)
    }
}