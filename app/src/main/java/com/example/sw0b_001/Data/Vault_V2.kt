package com.example.sw0b_001.Data

import android.content.Context
import android.util.Log
import com.example.sw0b_001.Modules.Network
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.httpGet
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Vault_V2(val uid: String) {

    @Serializable
    data class LoginRequest(val phone_number: String,
                            val password: String,
                            val captcha_token: String = "")

    @Serializable
    data class SignupRequest(val phone_number: String,
                             val name: String,
                             val country_code: String,
                             val password: String,
                             val captcha_token: String = "")
    @Serializable
    data class OTPRequest(val phone_number: String)

    @Serializable
    data class OTPSubmit(val code: String)

    @Serializable
    data class UID(val uid: String)

    @Serializable
    data class PlatformDescription(val en: String, val fr: String, val fa: String)

    @Serializable
    data class Platform(val name: String,
                        val description: PlatformDescription,
                        val logo: String,
                        val initialization_url: String,
                        val type: String,
                        val letter: String)

    @Serializable
    data class Platforms(val unsaved_platforms: ArrayList<Platform>,
                         val saved_platforms: ArrayList<Platform>)

    companion object {
        fun login(phoneNumber: String, password: String, url: String):
                Network.NetworkResponseResults {
            val payload = Json.encodeToString(LoginRequest(phoneNumber, password))
            val networkResponseResults = Network.jsonRequestPost(url, payload)
            when(networkResponseResults.response.statusCode) {
                in 400..500 -> throw Exception("Invalid Credentials")
                in 500..600 -> throw Exception("Server error")
            }
            return networkResponseResults
        }

        fun signup(url: String, phone_number: String, name: String, country_code: String,
                   password: String): Network.NetworkResponseResults {
            val payload = Json.encodeToString(SignupRequest(phone_number, name, country_code,
                    password))
            val networkResponseResults = Network.jsonRequestPost(url, payload)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return networkResponseResults
        }

        /**
         * headers come from signupOTPComplete.
         *
         * Note: Make sure dialing code is available in phone_number to avoid a 401.
         *
         */
        fun otpRequest(url: String, headers: Headers, phone_number: String, uid: String) :
                Network.NetworkResponseResults{
            val otpUrl = "$url/v2/users/$uid/OTP"
            val payload = Json.encodeToString(OTPRequest(phone_number))
            return Network.jsonRequestPost(otpUrl, payload, headers)
        }

        /**
         * headers come from OTPRequest
         */
        fun otpSubmit(url: String, headers: Headers, code: String) :
                Network.NetworkResponseResults{
            val payload = Json.encodeToString(OTPSubmit(code))
            return Network.jsonRequestPut(url, payload, headers)
        }

        /**
         * headers come from OTPSubmit
         */
        fun signupOtpComplete(url: String, headers: Headers): Network.NetworkResponseResults {
            return Network.jsonRequestPut(url, "", headers)
        }

        fun getPlatforms(url: String, headers: Headers, uid: String) : Platforms{
            val platformsUrl = "${url}/v2/users/${uid}/platforms"
            val networkResponseResults = Network.requestGet(platformsUrl, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return Json.decodeFromString<Platforms>(networkResponseResults.result.get())
        }
    }

    private val userDetailsFilename = "user_details_pref"
    fun storeUID(context: Context, url: String) {
//        val masterKey = MasterKey.Builder(context)
//                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//                .build()
//
//        val sharedPreferences = EncryptedSharedPreferences.create(
//                context,
//                userDetailsFilename,
//                masterKey,
//                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )

        // use the shared preferences and editor as you normally would

        // use the shared preferences and editor as you normally would
        val sharedPreferences = context.getSharedPreferences(userDetailsFilename,
                Context.MODE_PRIVATE)
        sharedPreferences.edit()
                .putString("uid", uid)
                .putString("uid_url", url)
                .apply()
    }
}