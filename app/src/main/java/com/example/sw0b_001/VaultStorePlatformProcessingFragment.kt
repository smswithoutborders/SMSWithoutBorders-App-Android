package com.example.sw0b_001

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Helpers
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.Modules.OAuth2
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.net.URLDecoder


class VaultStorePlatformProcessingFragment(val platformName: String,
                                           val networkResponseResults: Network.NetworkResponseResults)
    : Fragment(R.layout.fragment_onboarding_network_loading){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun storeToken() {
        val credentials = UserArtifactsHandler.fetchCredentials(requireContext())
        val uid = credentials[UserArtifactsHandler.USER_ID_KEY]!!
        val phonenumber = credentials[UserArtifactsHandler.PHONE_NUMBER]!!
        val password = credentials[UserArtifactsHandler.PASSWORD]!!
        val url = getString(R.string.smswithoutborders_official_vault)

        ThreadExecutorPool.executorService.execute {
            val networkResponseResults = Vault_V2.loginViaUID(url, uid, password)

            try{
                getGrant(uid, phonenumber, networkResponseResults).let {

                Vault_V2.storeOauthRequestCookies(requireContext(), it.first.response.headers)
                Vault_V2.storeOauthRequestCodeVerifier(requireContext(), it.second.code_verifier)

                val parameters = Helpers.extractParameters(it.second.url)
                parameters.forEach { value -> println("${value.key}: ${value.value}")}

                val codeVerifier = it.second.code_verifier
                val redirectUrl: String = URLDecoder.decode(parameters["redirect_uri"]!!, "UTF-8")
                var scope: String = URLDecoder.decode(parameters["scope"]!!, "UTF-8")
                val responseType: String = URLDecoder.decode(parameters["response_type"]!!, "UTF-8")
                val state: String = URLDecoder.decode(parameters["state"]!!, "UTF-8")

                activity?.runOnUiThread {
                    when(platformName) {
                        "x", "twitter" -> {
                            try {
                                val codeChallenge: String = URLDecoder.decode(parameters["code_challenge"]!!,
                                        "UTF-8")
                                val codeChallengeMethod: String = URLDecoder.decode(
                                        parameters["code_challenge_method"]!!, "UTF-8")
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
            } catch (e: Exception) {
                Log.e(javaClass.name, "Exception", e)
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getGrant(uid: String,
                         phonenumber: String,
                         networkResponseResults: Network.NetworkResponseResults) :
            Pair<Network.NetworkResponseResults, Vault_V2.OAuthGrantPayload> {
        val platformsUrl = getString(R.string.smswithoutborders_official_vault)

        val headers = networkResponseResults.response.headers
        headers.forEach { println("${it.key}: ${it.value}") }
        headers["Origin"] = "https://" +
                context?.getString(R.string.oauth_openid_redirect_url_scheme_host)
        headers.remove("Content-Type")

        return when(platformName) {
            "x", "twitter" -> {
                Vault_V2.getXGrant(platformsUrl, headers, uid, phonenumber)
            }
            "gmail" -> {
                Vault_V2.getGmailGrant(platformsUrl, headers, uid, phonenumber)
            }
            else -> { throw Exception("Unknown platform: $platformName")}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<CircularProgressIndicator>(R.id.network_loading_progress_indicator)
                .isIndeterminate = true
        storeToken()
    }
}