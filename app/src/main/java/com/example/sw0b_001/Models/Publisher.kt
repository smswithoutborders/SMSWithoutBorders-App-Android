package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
import android.widget.Toast
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.R
import com.example.sw0b_001.Security.Cryptography
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.serialization.json.Json
import publisher.v1.PublisherGrpc
import publisher.v1.PublisherOuterClass
import publisher.v1.PublisherGrpc.PublisherBlockingStub
import publisher.v1.PublisherGrpc.PublisherStub
import vault.v1.Vault

class Publisher(val context: Context) {

     var REDIRECT_URL_SCHEME = "relaysms://relaysms.com/android/"

    companion object {
        val PUBLISHER_ID_KEYSTORE_ALIAS = "PUBLISHER_ID_KEYSTORE_ALIAS"

        fun getAvailablePlatforms(context: Context): ArrayList<AvailablePlatforms> {
            try {
                val response = Network.requestGet(context.getString(R.string.publisher_get_platforms_url))
                return Json.decodeFromString<ArrayList<AvailablePlatforms>>(response.result.get())
            } catch(e: Exception) {
                e.printStackTrace()
                throw Throwable(e)
            }
        }
    }

    private var channel: ManagedChannel = ManagedChannelBuilder
        .forAddress(context.getString(R.string.publisher_grpc_url),
            context.getString(R.string.publisher_grpc_port).toInt())
        .useTransportSecurity()
        .build()

    private var publisherStub = PublisherGrpc.newBlockingStub(channel)

    private fun getRedirectUrl(platform: String): String{
        return "https://oauth.afkanerd.com/platforms/$platform/protocols/oauth2/redirect_codes/"
    }

    fun getOAuthURL(availablePlatforms: AvailablePlatforms,
                    autogenerateCodeVerifier: Boolean = true,
                    supportsUrlScheme: Boolean = true) : PublisherOuterClass.GetOAuth2AuthorizationUrlResponse {
        val scheme = if (supportsUrlScheme) "true" else "false"
        val request = PublisherOuterClass
            .GetOAuth2AuthorizationUrlRequest.newBuilder().apply {
                setPlatform(availablePlatforms.name)
                setState(Base64.encodeToString("${availablePlatforms.name},$scheme".encodeToByteArray(),
                    Base64.DEFAULT))
                setRedirectUrl(if (supportsUrlScheme) REDIRECT_URL_SCHEME else
                    getRedirectUrl(availablePlatforms.name))
                setAutogenerateCodeVerifier(autogenerateCodeVerifier)
        }.build()

        try {
            return publisherStub.getOAuth2AuthorizationUrl(request)
        } catch(e: Exception) {
            throw Throwable(e)
        }
    }
}