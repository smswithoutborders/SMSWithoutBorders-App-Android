package com.example.sw0b_001.Modules

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.sw0b_001.HomepageComposeNewFragment
import com.example.sw0b_001.R
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

class OAuth2 {
    companion object {

        public fun requestXAuth(context: Context,
                                codeVerifier: String,
                                codeChallenge: String,
                                codeChallengeMethod: String,
                                redirectUrl: String,
                                scope: String, state: String) {
            // scope=tweet.write+users.read+tweet.read+offline.access
//            val scope = "tweet.write users.read tweet.read offline.access"
            val clientId = context.getString(R.string.oauth_x_client_id)
            val authorizationUri = "https://twitter.com/i/oauth2/authorize"
            val tokenUri = "https://api.twitter.com/2/oauth2/token"

            appAuthRequestManually(context,
                    scope,
                    clientId,
                    authorizationUri,
                    tokenUri,
                    codeVerifier, codeChallenge, codeChallengeMethod, redirectUrl, state)
        }
        public fun requestGmailAuth(context: Context,
                                    scope: String,
                                    redirectUrl: String,
                                    clientId: String,
                                    state: String) {
            val serviceUri = "https://accounts.google.com"
            appAuthRequestWithDocument(context, serviceUri, scope, clientId, redirectUrl, state)
        }

        private fun appAuthRequestManually(context: Context,
                                           scope: String,
                                           clientId: String,
                                           authorizationUri: String,
                                           tokenUri: String,
                                           codeVerifier: String,
                                           codeChallenge: String,
                                           codeChallengeMethod: String,
                                           redirectUrl: String,
                                           state: String) {
            val serviceConfig = AuthorizationServiceConfiguration(
                    android.net.Uri.parse(authorizationUri),  // authorization endpoint
                    android.net.Uri.parse(tokenUri))

            executeAuthRequest(context,
                    clientId,
                    scope,
                    serviceConfig,
                    codeVerifier,
                    codeChallenge,
                    codeChallengeMethod,
                    redirectUrl,
                    state)
        }

        private fun appAuthRequestWithDocument(context: Context,
                                               serviceUri: String,
                                               scope: String,
                                               clientId: String,
                                               redirectUrl: String,
                                               state: String) {
            AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(serviceUri),
                    AuthorizationServiceConfiguration
                            .RetrieveConfigurationCallback { serviceConfiguration, ex ->
                                if (ex != null) {
                                    Log.e(HomepageComposeNewFragment.TAG,
                                            "failed to fetch configuration", ex)
                                    return@RetrieveConfigurationCallback
                                }
                                if(serviceConfiguration != null)
                                    executeAuthRequest(context,
                                            clientId,
                                            scope,
                                            serviceConfiguration,
                                            "", "", "",
                                            redirectUrl, state)

                            })
        }

        //https://twitter.com/i/oauth2/authorize?redirect_uri=https%3A%2F%2Foauth.afkanerd.com%2Fplatforms%2Ftwitter%2Fprotocols%2Foauth2%2Fredirect_codes%2F&client_id=WGptX1N5dkJiS1lVUFZUSC1wYkk6MTpjaQ&response_type=code&state=9nKkCduwu4gr0B3VNcCdgdE8GkGg3R&nonce=lnb8q1I036k65o_Klf_mhg&scope=tweet.write%20users.read%20tweet.read%20offline.access&code_challenge=rO5DnAe7weQ6GcTa5JtD6Y194yZwVzbwX8HtCtI9OVEnMdJ65guQ&code_challenge_method=S256
        private fun executeAuthRequest(context: Context,
                                       clientId: String,
                                       scope: String,
                                       serviceConfiguration: AuthorizationServiceConfiguration,
                                       codeVerifier: String,
                                       codeChallenge: String,
                                       codeChallengeMethod: String, redirectUrl: String, state: String) {
            Log.d(javaClass.name,
                    "Auth endpoint: ${serviceConfiguration.authorizationEndpoint}")

            val authRequest = AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    ResponseTypeValues.CODE,
                    Uri.parse(redirectUrl)) //redirect url
                    .setScope(scope) // Gmail send scope)
                    .setState(state)
//                    .setPrompt("consent")

            if(!codeVerifier.isNullOrEmpty()) {
                println("Code verifier: $codeVerifier")
                if (!codeChallenge.isNullOrEmpty() && !codeChallengeMethod.isNullOrEmpty()) {
                    println("Code challenge: $codeChallenge")
                    println("Code challenge method: $codeChallengeMethod")
                    authRequest.setCodeVerifier(codeVerifier, codeChallenge, codeChallengeMethod)
                } else authRequest.setCodeVerifier(codeVerifier)
            }
            else
                authRequest.setCodeVerifier(null)

            val authService = AuthorizationService(context);
            val authIntent: Intent = authService.getAuthorizationRequestIntent(authRequest.build())
            context.startActivity(authIntent);
        }
    }
}