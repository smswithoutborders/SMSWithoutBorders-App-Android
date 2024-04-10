package com.example.sw0b_001.Models.GatewayClients

import com.example.sw0b_001.Models.BackendCommunications
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GatewayClientsCommunications {

    @Serializable
    data class GatewayClient(val msisdn: String, val country: String,
                             val protocol: ArrayList<String>)
    companion object {
        fun fetchRemote(url: String): ResponseResultOf<String> {
            return url.httpGet()
                    .responseString()
        }
    }
}