package com.example.sw0b_001.Models.v2

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import at.favre.lib.armadillo.Armadillo
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.Models.Platforms.PlatformsHandler
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.R
import com.github.kittinunf.fuel.core.Headers
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Vault_V2(val uid: String) {

    @Serializable
    data class LoginRequest(val phone_number: String,
                            val password: String,
                            val captcha_token: String)

    @Serializable
    data class LoginRequestViaUID(val password: String)

    @Serializable
    data class SignupRequest(val phone_number: String,
                             val name: String,
                             val country_code: String,
                             val password: String,
                             val captcha_token: String)
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
    data class OAuthGrantSubmissionGmail(val code: String,
                                         val scope: String,
                                         val state: String)

    @Serializable
    data class OAuthGrantSubmissionX(val code: String, val code_verifier: String)

    companion object {
        const val INVALID_CREDENTIALS_EXCEPTION = "INVALID_CREDENTIALS_EXCEPTION"
        const val SERVER_ERROR_EXCEPTION = "SERVER_ERROR_EXCEPTION"

        fun loginViaUID(_url: String, uid: String, password: String):
                Network.NetworkResponseResults {
            val url = "$_url/v2/users/$uid/verify"
            val payload = Json.encodeToString(LoginRequestViaUID(password))
            val networkResponseResults = Network.jsonRequestPost(url, payload)
            when(networkResponseResults.response.statusCode) {
                in 400..500 -> throw Exception(INVALID_CREDENTIALS_EXCEPTION)
                in 500..600 -> throw Exception(SERVER_ERROR_EXCEPTION)
            }
            return networkResponseResults
        }

        fun login(phoneNumber: String, password: String, url: String, captcha_token: String):
                Network.NetworkResponseResults {
            val payload = Json.encodeToString(LoginRequest(phoneNumber, password, captcha_token))
            val networkResponseResults = Network.jsonRequestPost(url, payload)
            when(networkResponseResults.response.statusCode) {
                in 400..500 -> throw Exception(INVALID_CREDENTIALS_EXCEPTION)
                in 500..600 -> throw Exception(SERVER_ERROR_EXCEPTION)
            }
            return networkResponseResults
        }

        fun signup(url: String, phone_number: String, name: String, country_code: String,
                   password: String, captcha_token: String): Network.NetworkResponseResults {
            val payload = Json.encodeToString(SignupRequest(phone_number, name, country_code,
                    password, captcha_token))
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

        fun sendGmailCode(context: Context,
                          url: String,
                          headers: Headers,
                          uid: String,
                          code: String,
                          code_verifier: String,
                          scope: String,
                          state: String) : Network.NetworkResponseResults {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/gmail/protocols/oauth2"
            headers["Origin"] = "https://" +
                    context.getString(R.string.oauth_openid_redirect_url_scheme_host)

            val payload = Json.encodeToString(
                    OAuthGrantSubmissionGmail(code,
                            scope,
                            state))
            val networkResponseResults = Network.jsonRequestPut(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return networkResponseResults
        }

        fun sendXCode(context: Context,
                      url: String,
                      headers: Headers,
                      uid: String,
                      code: String,
                      code_verifier: String,
                      state: String) : Network.NetworkResponseResults {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/twitter/protocols/oauth2"

            headers["Origin"] = "https://" +
                    context.getString(R.string.oauth_openid_redirect_url_scheme_host)

            val payload = Json.encodeToString(OAuthGrantSubmissionX(code, code_verifier))
            val networkResponseResults = Network.jsonRequestPut(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return networkResponseResults
        }

        fun getXGrant(url: String, headers: Headers, uid: String, phone_number: String) :
                Pair<Network.NetworkResponseResults, OAuthGrantPayload> {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/twitter/protocols/oauth2"
            Log.d(javaClass.name, "uid: $uid, url: $platformsUrl")
            val payload = Json.encodeToString(OAuthGrantRequest(phone_number))
            val networkResponseResults = Network.jsonRequestPost(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return Pair(networkResponseResults,
                    Json.decodeFromString<OAuthGrantPayload>(networkResponseResults.result.get()))
        }

        fun getGmailGrant(url: String, headers: Headers, uid: String, phone_number: String) :
                Pair<Network.NetworkResponseResults, OAuthGrantPayload> {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/gmail/protocols/oauth2"
            val payload = Json.encodeToString(OAuthGrantRequest(phone_number))
            println("payload: $payload")
            val networkResponseResults = Network.jsonRequestPost(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
            return Pair(networkResponseResults,
                    Json.decodeFromString<OAuthGrantPayload>(networkResponseResults.result.get()))
        }

        private const val OAUTH_COOKES_PREF = "OAUTH_COOKES_PREF"


        fun fetchOauthRequestCookies(context: Context) : String {
            val sharedPreferences = Armadillo.create(context, OAUTH_COOKES_PREF)
                    .encryptionFingerprint(context)
                    .build()
            return sharedPreferences.getString("cookies", "")!!
        }
        fun fetchOauthRequestVerifier(context: Context) : String {
            val sharedPreferences = Armadillo.create(context, OAUTH_COOKES_PREF)
                    .encryptionFingerprint(context)
                    .build()
            return sharedPreferences.getString("code_verifier", "")!!
        }

        fun storeOauthRequestCookies(context: Context, headers: Headers) {
            val sharedPreferences = Armadillo.create(context, OAUTH_COOKES_PREF)
                    .encryptionFingerprint(context)
                    .build()

            sharedPreferences.edit()
                    .putString("cookies", headers["Set-Cookie"].first())
                    .apply()
        }

        fun storeOauthRequestCodeVerifier(context: Context, code_verifier: String) {
            val sharedPreferences = Armadillo.create(context, OAUTH_COOKES_PREF)
                    .encryptionFingerprint(context)
                    .build()

            sharedPreferences.edit()
                    .putString("code_verifier", code_verifier)
                    .apply()
        }

        fun loginSyncPlatformsFlow(context: Context,
                                   phoneNumber: String,
                                   password: String,
                                   captcha_token: String,
                                   _uid: String? = null, fragment: Fragment? = null):
                Network.NetworkResponseResults {
            val networkResponseResults = if(_uid.isNullOrEmpty()) {
                val url = context.getString(R.string.smswithoutborders_official_site_login)
                login(phoneNumber, password, url, captcha_token)
            } else {
                val url = context.getString(R.string.smswithoutborders_official_vault)
                loginViaUID(url, _uid, password)
            }

            fragment?.let {
                it.activity?.runOnUiThread {
                    Toast.makeText(context, context.getString(R.string.login_successful),
                            Toast.LENGTH_SHORT).show()
                }
            }
            val uid = if(_uid.isNullOrEmpty())
                Json.decodeFromString<UID>(networkResponseResults.result.get()).uid
            else _uid

            if(_uid.isNullOrEmpty())
                UserArtifactsHandler.storeCredentials(context, phoneNumber, password, uid)

            val responsePayload = GatewayServer_V2.sync(context, uid, password)

            fragment?.let {
                it.activity?.runOnUiThread {
                    Toast.makeText(context, context.getString(R.string.synced_successfully),
                            Toast.LENGTH_SHORT).show()
                }
            }
            UserArtifactsHandler.storeSharedKey(context, responsePayload.shared_key)

            GatewayClientsCommunications.populateDefaultGatewayClientsSetDefaults(context)

            val platformsUrl = context.getString(R.string.smswithoutborders_official_vault)

            PlatformsHandler.storePlatforms(context,
                    uid,
                    platformsUrl,
                    networkResponseResults.response.headers)

            fragment?.let {
                it.activity?.runOnUiThread {
                    Toast.makeText(context,
                            context.getString(R.string.login_platforms_stored_successfully),
                            Toast.LENGTH_SHORT).show()
                }
            }

            return networkResponseResults
        }
    }

}