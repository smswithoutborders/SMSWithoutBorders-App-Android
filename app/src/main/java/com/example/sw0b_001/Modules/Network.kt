package com.example.sw0b_001.Modules

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result

class Network {
    data class NetworkResponseResults(val response: Response,
                                      val result: Result<String, java.lang.Exception>)
    companion object {
        fun jsonRequest(url: String, payload: String) : NetworkResponseResults {
            val (_, response, result) = Fuel.post(url)
                    .jsonBody(payload)
                    .responseString()
            return when(result) {
                is Result.Failure -> {
//                    Log.w(javaClass.name, "Response text - ${String(response.data)}")
                    NetworkResponseResults(response, Result.Failure(result.error))
                }

                is Result.Success -> {
                    NetworkResponseResults(response, Result.Success(result.get()))
                }
            }
        }
    }
}