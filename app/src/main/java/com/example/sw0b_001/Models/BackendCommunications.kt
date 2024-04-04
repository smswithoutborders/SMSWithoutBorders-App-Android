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