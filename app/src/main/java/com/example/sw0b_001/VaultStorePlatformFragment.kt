package com.example.sw0b_001

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.Data.v2.Vault_V2
import com.example.sw0b_001.HomepageComposeNewFragment.Companion.TAG
import com.example.sw0b_001.Modules.Helpers
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.Modules.OAuth2
import com.google.android.material.button.MaterialButton
import kotlinx.serialization.json.Json
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthorizationServiceConfiguration.RetrieveConfigurationCallback
import net.openid.appauth.ResponseTypeValues
import java.net.URLDecoder


class VaultStorePlatformFragment : Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun storeToken(platform: String) {
        val url = getString(R.string.smswithoutborders_official_site_login)

        val phonenumber = "+237123456789"
        val password = "dummy_password"
        ThreadExecutorPool.executorService.execute {
            val networkResponseResults =
                    Vault_V2.login(phonenumber, password, url)
            val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid
            UserArtifactsHandler.storeCredentials(requireContext(), phonenumber, password, uid)

            val pairNetworkResponseResultsOauth = getGrant(platform, uid, phonenumber,
                    networkResponseResults)

            Vault_V2.storeOauthRequestCookies(requireContext(),
                    pairNetworkResponseResultsOauth.first.response.headers)

            Vault_V2.storeOauthRequestCodeVerifier(requireContext(),
                    pairNetworkResponseResultsOauth.second.code_verifier)

            println(pairNetworkResponseResultsOauth.second.url)

            val parameters = Helpers.extractParameters(pairNetworkResponseResultsOauth.second.url)

            parameters.forEach { println("${it.key}: ${it.value}")}

            val codeVerifier = pairNetworkResponseResultsOauth.second.code_verifier
            val redirectUrl: String = URLDecoder.decode(parameters["redirect_uri"]!!, "UTF-8")
            var scope: String = URLDecoder.decode(parameters["scope"]!!, "UTF-8")
            val responseType: String = URLDecoder.decode(parameters["response_type"]!!, "UTF-8")
            val state: String = URLDecoder.decode(parameters["state"]!!, "UTF-8")

            println("\ncode verifier: $codeVerifier")
            println("redirect url: $redirectUrl")
            println("response type: $responseType")
            println("state: $state")

            activity?.runOnUiThread {

                when(platform) {
                    "x" -> {
                        try {
                            val codeChallenge: String = URLDecoder.decode(parameters["code_challenge"]!!,
                                    "UTF-8")
                            val codeChallengeMethod: String = URLDecoder.decode(
                                    parameters["code_challenge_method"]!!, "UTF-8")

                            println("code challenge: $codeChallenge")
                            println("code challenge method: $codeChallengeMethod")
                            println("scope: $scope")
                            OAuth2.requestXAuth(requireContext(),
                                    codeVerifier,
                                    codeChallenge,
                                    codeChallengeMethod,
                                    redirectUrl,
                                    scope,
                                    state)
                        } catch(e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    "gmail" -> {
                        try {
                            val clientId: String = URLDecoder.decode(
                                    parameters["client_id"]!!, "UTF-8")
                            val accessType: String = URLDecoder.decode(
                                    parameters["access_type"]!!, "UTF-8")
                            println("scope: $scope")
                            OAuth2.requestGmailAuth(requireContext(),
                                    scope,
                                    redirectUrl,
                                    clientId,
                                    state)
                        } catch(e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    }

    fun getGrant(platform: String, uid: String, phonenumber: String,
                 networkResponseResults: Network.NetworkResponseResults) :
            Pair<Network.NetworkResponseResults, Vault_V2.OAuthGrantPayload> {
        val platformsUrl = getString(R.string.smswithoutborders_official_vault)

        val headers = networkResponseResults.response.headers
        headers["Origin"] = "https://" +
                context?.getString(R.string.oauth_openid_redirect_url_scheme_host)

        return when(platform) {
            "x" -> {
                Vault_V2.getXGrant(platformsUrl, headers, uid, phonenumber)
            }
            "gmail" -> {
                Vault_V2.getGmailGrant(platformsUrl, headers, uid, phonenumber)
            }
            else -> { throw Exception("Unknown platform: $platform")}
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboarding_vault_store, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.vault_store_btn_gmail)
                .setOnClickListener { storeToken("gmail") }

        view.findViewById<MaterialButton>(R.id.vault_store_btn_x)
                .setOnClickListener { storeToken("x") }
    }
}