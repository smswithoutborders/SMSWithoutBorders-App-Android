package com.example.sw0b_001.Models

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackendCommunications(val uid: String) {

    @Serializable
    data class UserCredentials(val phone_number: String,
                               val password: String,
                               val captcha_token: String = "")

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
        fun login(phoneNumber: String, password: String, url: String) : ResponseResultOf<String> {
            val payload = Json.encodeToString(UserCredentials(phoneNumber, password))

            return Fuel.post(url)
                    .jsonBody(payload)
                    .responseString()
        }
    }

    fun getPlatforms(url: String, responseHeaders: Headers) : ResponseResultOf<String>{
        val platformsUrl = "${url}/v2/users/${uid}/platforms"
        return platformsUrl
                .httpGet()
                .header(Headers.COOKIE to responseHeaders["Set-Cookie"].first())
                .responseString()
    }
}