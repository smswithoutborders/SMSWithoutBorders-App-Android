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
import com.example.sw0b_001.HomepageComposeNewFragment.Companion.TAG
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthorizationServiceConfiguration.RetrieveConfigurationCallback
import net.openid.appauth.ResponseTypeValues


class VaultStorePlatformFragment : Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestGmailOAuth()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_modal_sheet_2fa_verification_code, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private val RC_AUTH = 0
    private fun requestGmailOAuth() {
        AuthorizationServiceConfiguration.fetchFromIssuer(
                Uri.parse("https://accounts.google.com"),
                RetrieveConfigurationCallback { serviceConfiguration, ex ->
                    if (ex != null) {
                        Log.e(TAG, "failed to fetch configuration", ex)
                        return@RetrieveConfigurationCallback
                    }

                    // use serviceConfiguration as needed
                    if(serviceConfiguration != null) {
                        val redirectUrl = "https://" +
                                getString(R.string.oauth_openid_redirect_url_scheme_host) +
                                getString(R.string.oauth_openid_redirect_url_scheme_path)
                        val authRequest = AuthorizationRequest.Builder(
                                serviceConfiguration,  // the authorization service configuration
                                getString(R.string.oauth_gmail_client_id),  // the client ID, typically pre-registered and static
                                ResponseTypeValues.CODE,  // the response_type value: we want a code
                                Uri.parse(redirectUrl)) //redirect url
                                .setScope("https://www.googleapis.com/auth/gmail.send profile email") // Gmail send scope
                                .setPrompt("consent")
                                .build()

                        val authService = AuthorizationService(requireContext());
                        val authIntent: Intent = authService.getAuthorizationRequestIntent(authRequest);
                        startActivityForResult(authIntent, RC_AUTH);
                    }
                })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_AUTH) {
            val resp = AuthorizationResponse.fromIntent(data!!)
            val ex = AuthorizationException.fromIntent(data)
            // ... process the response or exception ...
            if (resp != null) {
                // authorization completed
                Log.d(javaClass.name, "Authorization complete: $resp")
            } else {
                // authorization failed, check ex for more details
                Log.e(javaClass.name, "Authorization failed", ex)
            }
        } else {
            // ...
        }
    }
}