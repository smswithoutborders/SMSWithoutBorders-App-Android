package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
import android.widget.Toast
import at.favre.lib.armadillo.Armadillo
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
import java.net.UnknownHostException

class Publisher(val context: Context) {

     private var REDIRECT_URL_SCHEME = "relaysms://oauth.afkanerd.com/android/"

    private var channel: ManagedChannel = ManagedChannelBuilder
        .forAddress(context.getString(R.string.publisher_grpc_url),
            context.getString(R.string.publisher_grpc_port).toInt())
        .useTransportSecurity()
        .build()

    private var publisherStub = PublisherGrpc.newBlockingStub(channel)

    private var oAuthRedirectUrl = "https://oauth.afkanerd.com/android/"

    fun getOAuthURL(availablePlatforms: AvailablePlatforms,
                    autogenerateCodeVerifier: Boolean = true,
                    supportsUrlScheme: Boolean = true) : PublisherOuterClass.GetOAuth2AuthorizationUrlResponse {
        val scheme = if (supportsUrlScheme) "true" else "false"
        val request = PublisherOuterClass
            .GetOAuth2AuthorizationUrlRequest.newBuilder().apply {
                setPlatform(availablePlatforms.name)
                setState(Base64.encodeToString("${availablePlatforms.name},$scheme".encodeToByteArray(),
                    Base64.DEFAULT))
                setRedirectUrl(if (supportsUrlScheme) REDIRECT_URL_SCHEME else oAuthRedirectUrl)
                setAutogenerateCodeVerifier(autogenerateCodeVerifier)
            }.build()

        return publisherStub.getOAuth2AuthorizationUrl(request)
    }

    fun revokeOAuthPlatforms(llt: String, platform: String, account: String) :
            PublisherOuterClass.RevokeAndDeleteOAuth2TokenResponse {
        val request = PublisherOuterClass.RevokeAndDeleteOAuth2TokenRequest.newBuilder().apply {
            setPlatform(platform)
            setLongLivedToken(llt)
            setAccountIdentifier(account)
        }.build()

        return publisherStub.revokeAndDeleteOAuth2Token(request)
    }

    fun revokePNBAPlatforms(llt: String, platform: String, account: String) :
            PublisherOuterClass.RevokeAndDeletePNBATokenResponse {
        val request = PublisherOuterClass.RevokeAndDeletePNBATokenRequest.newBuilder().apply {
            setPlatform(platform)
            setLongLivedToken(llt)
            setAccountIdentifier(account)
        }.build()

        return publisherStub.revokeAndDeletePNBAToken(request)
    }

    fun sendOAuthAuthorizationCode(llt: String,
                                   platform: String,
                                   code: String,
                                   codeVerifier: String,
                                   supportsUrlScheme: Boolean):
            PublisherOuterClass.ExchangeOAuth2CodeAndStoreResponse {
        val request = PublisherOuterClass.ExchangeOAuth2CodeAndStoreRequest.newBuilder().apply {
            setLongLivedToken(llt)
            setPlatform(platform)
            setAuthorizationCode(code)
            setCodeVerifier(codeVerifier)
            setRedirectUrl(if (supportsUrlScheme) REDIRECT_URL_SCHEME else oAuthRedirectUrl)
        }.build()

        return publisherStub.exchangeOAuth2CodeAndStore(request)
    }

    fun phoneNumberBaseAuthenticationRequest(phoneNumber: String, platform: String):
            PublisherOuterClass.GetPNBACodeResponse {
        val request = PublisherOuterClass.GetPNBACodeRequest.newBuilder().apply {
            setPlatform(platform)
            setPhoneNumber(phoneNumber)
        }.build()

        return publisherStub.getPNBACode(request)
    }

    fun revokePNBAPlatform(llt: String, platform: String, account: String) :
            PublisherOuterClass.RevokeAndDeletePNBATokenResponse {
        val request = PublisherOuterClass.RevokeAndDeletePNBATokenRequest.newBuilder().apply {
            setPlatform(platform)
            setLongLivedToken(llt)
            setAccountIdentifier(account)
        }.build()

        return publisherStub.revokeAndDeletePNBAToken(request)
    }

    fun phoneNumberBaseAuthenticationExchange(authorizationCode: String,
                                              llt: String,
                                              phoneNumber: String,
                                              platform: String) :
            PublisherOuterClass.ExchangePNBACodeAndStoreResponse {
        val request = PublisherOuterClass.ExchangePNBACodeAndStoreRequest.newBuilder().apply {
            setPlatform(platform)
            setLongLivedToken(llt)
            setAuthorizationCode(authorizationCode)
            setPassword("")
            setPhoneNumber(phoneNumber)
        }.build()

        return publisherStub.exchangePNBACodeAndStore(request)
    }

    fun shutdown() {
        channel.shutdown()
    }

    companion object {
        const val PUBLISHER_ID_KEYSTORE_ALIAS = "PUBLISHER_ID_KEYSTORE_ALIAS"
        const val OAUTH2_PARAMETERS_FILE = "OAUTH2_PARAMETERS_FILE"

        private const val PUBLISHER_ATTRIBUTE_FILES =
            "com.afkanerd.relaysms.PUBLISHER_ATTRIBUTE_FILES"

        private const val PUBLISHER_PUBLIC_KEY =
            "com.afkanerd.relaysms.PUBLISHER_PUBLIC_KEY"

        fun getAvailablePlatforms(context: Context,
                                  exceptionRunnable: Runnable): ArrayList<AvailablePlatforms> {
            val response = Network.requestGet(context.getString(R.string.publisher_get_platforms_url))
            return Json.decodeFromString<ArrayList<AvailablePlatforms>>(response.result.get())
        }

        fun fetchOauthRequestVerifier(context: Context) : String {
            val sharedPreferences = Armadillo.create(context, OAUTH2_PARAMETERS_FILE)
                .encryptionFingerprint(context)
                .build()
            return sharedPreferences.getString("code_verifier", "")!!
        }

        fun storeOauthRequestCodeVerifier(context: Context, codeVerifier: String) {
            val sharedPreferences = Armadillo.create(context, OAUTH2_PARAMETERS_FILE)
                .encryptionFingerprint(context)
                .build()

            sharedPreferences.edit()
                .putString("code_verifier", codeVerifier)
                .apply()
        }

        fun fetchPublisherPublicKey(context: Context) : ByteArray? {
            val sharedPreferences = Armadillo.create(context, PUBLISHER_ATTRIBUTE_FILES)
                .encryptionFingerprint(context)
                .build()
            return Base64.decode(sharedPreferences.getString(PUBLISHER_PUBLIC_KEY, ""),
                Base64.DEFAULT)
        }

        fun fetchPublisherSharedKey(context: Context) : ByteArray {
            val pubKey = fetchPublisherPublicKey(context)
            println("Public key: $pubKey")
            println("Public key: ${Base64.encodeToString(pubKey, Base64.DEFAULT)}")
            return Cryptography.calculateSharedSecret(context, PUBLISHER_ID_KEYSTORE_ALIAS,
                pubKey!!)
        }

        fun storeArtifacts(context: Context, publisherPubKey: String) {
            val sharedPreferences = Armadillo.create(context, PUBLISHER_ATTRIBUTE_FILES)
                .encryptionFingerprint(context)
                .build()

            sharedPreferences.edit()
                .putString(PUBLISHER_PUBLIC_KEY, publisherPubKey)
                .apply()
        }
    }

}