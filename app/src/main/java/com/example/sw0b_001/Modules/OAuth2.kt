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

        public fun requestXAuth(context: Context) {
            // scope=tweet.write+users.read+tweet.read+offline.access
            val scope = "tweet.write users.read tweet.read offline.access"
            val clientId = context.getString(R.string.oauth_x_client_id)
            val authorizationUri = "https://twitter.com/i/oauth2/authorize"
            val tokenUri = "https://api.twitter.com/2/oauth2/token"
            val path = context.getString(R.string.oauth_openid_redirect_url_scheme_path_x)

            appAuthRequestManually(context, scope, clientId, authorizationUri, tokenUri, path)
        }
        public fun requestGmailAuth(context: Context) {
            val serviceUri = "https://accounts.google.com"
            val scope = "https://www.googleapis.com/auth/gmail.send profile email"
            val clientId = context.getString(R.string.oauth_gmail_client_id)
            val path = context.getString(R.string.oauth_openid_redirect_url_scheme_path_gmail)

            appAuthRequestWithDocument(context, serviceUri, scope, clientId, path)
        }

        private fun appAuthRequestManually(context: Context,
                                           scope: String,
                                           clientId: String,
                                           authorizationUri: String,
                                           tokenUri: String,
                                           path: String) {
            val serviceConfig = AuthorizationServiceConfiguration(
                    android.net.Uri.parse(authorizationUri),  // authorization endpoint
                    android.net.Uri.parse(tokenUri))

            executeAuthRequest(context, clientId, scope, path, serviceConfig)
        }

        private fun appAuthRequestWithDocument(context: Context, serviceUri: String, scope: String,
                                   clientId: String, path: String) {
            AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse(serviceUri),
                    AuthorizationServiceConfiguration
                            .RetrieveConfigurationCallback { serviceConfiguration, ex ->
                                if (ex != null) {
                                    Log.e(HomepageComposeNewFragment.TAG,
                                            "failed to fetch configuration", ex)
                                    return@RetrieveConfigurationCallback
                                }
                                if(serviceConfiguration != null)
                                    executeAuthRequest(context, clientId, scope, path,
                                            serviceConfiguration)

                            })
        }

        private fun executeAuthRequest(context: Context,
                                       clientId: String,
                                       scope: String,
                                       path: String,
                                       serviceConfiguration: AuthorizationServiceConfiguration) {
            val redirectUrl = "https://" +
                    context.getString(
                            R.string.oauth_openid_redirect_url_scheme_host) + path

            val authRequest = AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    ResponseTypeValues.CODE,
                    Uri.parse(redirectUrl)) //redirect url
                    .setScope(scope) // Gmail send scope
                    .setPrompt("consent")
                    .build()

            val authService = AuthorizationService(context);
            val authIntent: Intent = authService.getAuthorizationRequestIntent(authRequest);
            context.startActivity(authIntent);
        }
    }
}