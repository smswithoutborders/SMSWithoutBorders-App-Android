package com.example.sw0b_001.Data.v2

import android.content.Context
import com.example.sw0b_001.Modules.Network
import com.github.kittinunf.fuel.core.Headers
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

    @Serializable
    data class OAuthGrantPayload(val url: String,
                                 val body: String,
                                 val platform: String,
                                 val code_verifier: String)

    @Serializable
    data class OAuthGrantRequest(val phone_number: String)

    @Serializable
    data class OAuthGrantSubmission(val code: String)

    companion object {
        const val INVALID_CREDENTIALS_EXCEPTION = "INVALID_CREDENTIALS_EXCEPTION"
        const val SERVER_ERROR_EXCEPTION = "SERVER_ERROR_EXCEPTION"
        fun login(phoneNumber: String, password: String, url: String):
                Network.NetworkResponseResults {
            val payload = Json.encodeToString(LoginRequest(phoneNumber, password))
            val networkResponseResults = Network.jsonRequestPost(url, payload)
            when(networkResponseResults.response.statusCode) {
                in 400..500 -> throw Exception(INVALID_CREDENTIALS_EXCEPTION)
                in 500..600 -> throw Exception(SERVER_ERROR_EXCEPTION)
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

        fun getPlatforms(url: String, headers: Headers, uid: String) : Platforms {
            val platformsUrl = "${url}/v2/users/${uid}/platforms"
            val networkResponseResults = Network.requestGet(platformsUrl, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return Json.decodeFromString<Platforms>(networkResponseResults.result.get())
        }

        fun sendGmailCode(url: String, headers: Headers, uid: String, code: String) {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/gmail/protocols/oauth2"
            val payload = Json.encodeToString(OAuthGrantSubmission(code))
            val networkResponseResults = Network.jsonRequestPut(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
        }

        fun sendXCode(url: String, headers: Headers, uid: String, code: String) {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/twitter/protocols/oauth2"
            val payload = Json.encodeToString(OAuthGrantSubmission(code))
            val networkResponseResults = Network.jsonRequestPut(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
        }

        fun getXGrant(url: String, headers: Headers, uid: String, phone_number: String) :
                OAuthGrantPayload {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/twitter/protocols/oauth2"
            val payload = Json.encodeToString(OAuthGrantRequest(phone_number))
            val networkResponseResults = Network.jsonRequestPost(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return Json.decodeFromString<OAuthGrantPayload>(networkResponseResults.result.get())
        }

        fun getGmailGrant(url: String, headers: Headers, uid: String, phone_number: String) :
                OAuthGrantPayload {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/gmail/protocols/oauth2"
            val payload = Json.encodeToString(OAuthGrantRequest(phone_number))
            val networkResponseResults = Network.jsonRequestPost(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return Json.decodeFromString<OAuthGrantPayload>(networkResponseResults.result.get())
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