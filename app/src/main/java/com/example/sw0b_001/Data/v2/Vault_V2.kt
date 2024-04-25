package com.example.sw0b_001.Data.v2

import android.content.Context
import android.util.Log
import at.favre.lib.armadillo.Armadillo
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.Modules.OAuth2
import com.example.sw0b_001.R
import com.github.kittinunf.fuel.core.Headers
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URL
import java.net.URLDecoder


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
    data class OAuthGrantSubmissionGmail(val code: String,
                                         val scope: String,
                                         val state: String)

    @Serializable
    data class OAuthGrantSubmissionX(val code: String, val code_verifier: String)

    companion object {
        const val INVALID_CREDENTIALS_EXCEPTION = "INVALID_CREDENTIALS_EXCEPTION"
        const val SERVER_ERROR_EXCEPTION = "SERVER_ERROR_EXCEPTION"
        fun login(phoneNumber: String, password: String, url: String):
                Network.NetworkResponseResults {
            Log.d(javaClass.name, "phonenumber: $phoneNumber, password: $password")
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

        //https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=https%3A%2F%2Foauth.afkanerd.com%2Fplatforms%2Fgmail%2Fprotocols%2Foauth2%2Fredirect_codes%2F&client_id=86878463881-3miiph6l8e8almabu5mat1gun3aaumrv.apps.googleusercontent.com&response_type=code&state=Tfx9xGx7bnWIB7GXB03rjDeDGTagqL&nonce=azAG6pu1qxjgzsF6SlfS3w&scope=openid%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fgmail.send%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email
        //https://accounts.google.com/o/oauth2/v2/auth/oauthchooseaccount?redirect_uri=https%3A%2F%2Foauth.afkanerd.com%2Fplatforms%2Fgmail%2Fprotocols%2Foauth2%2Fredirect_codes%2F&client_id=86878463881-3miiph6l8e8almabu5mat1gun3aaumrv.apps.googleusercontent.com&response_type=code&state=Tfx9xGx7bnWIB7GXB03rjDeDGTagqL&nonce=azAG6pu1qxjgzsF6SlfS3w&scope=openid%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fgmail.send%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&service=lso&o2v=2&ddm=0&flowName=GeneralOAuthFlow
        fun sendGmailCode(context: Context,
                          url: String,
                          headers: Headers,
                          uid: String,
                          code: String,
                          code_verifier: String,
                          scope: String,
                          state: String) {
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
        }

        fun sendXCode(context: Context,
                      url: String,
                      headers: Headers,
                      uid: String,
                      code: String,
                      code_verifier: String,
                      state: String) {
            val platformsUrl = "${url}/v2/users/${uid}/platforms/twitter/protocols/oauth2"
            headers["Origin"] = "https://" +
                    context.getString(R.string.oauth_openid_redirect_url_scheme_host)

            val payload = Json.encodeToString(OAuthGrantSubmissionX(code, code_verifier))
            val networkResponseResults = Network.jsonRequestPut(platformsUrl, payload, headers)
            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }
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
    }

}