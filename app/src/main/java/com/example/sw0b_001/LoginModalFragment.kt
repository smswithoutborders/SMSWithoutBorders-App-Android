package com.example.sw0b_001

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityAES
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA
import com.example.sw0b_001.Data.Platforms.Platforms
import com.example.sw0b_001.Data.Platforms.PlatformsHandler
import com.example.sw0b_001.Data.Platforms.PlatformsViewModel
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.Data.v2.Vault_V2
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.serialization.json.Json

class LoginModalFragment : BottomSheetDialogFragment() {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    lateinit var phonenumberTextView: TextInputEditText
    lateinit var passwordTextView: TextInputEditText
    lateinit var loginProgressIndicator: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.login_btn).setOnClickListener {
            loginRecaptchaEnabled(view)
        }

        val customUrlView = view.findViewById<TextInputLayout>(R.id.login_url)
        view.findViewById<MaterialTextView>(R.id.login_advanced_toggle).setOnClickListener {
            if(customUrlView.visibility == View.VISIBLE)
                customUrlView.visibility = View.INVISIBLE
            else
                customUrlView.visibility = View.VISIBLE
        }

        val bottomSheet = view.findViewById<View>(R.id.login_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        phonenumberTextView = view.findViewById(R.id.login_phonenumber_text_input)
        passwordTextView = view.findViewById(R.id.login_password_text_input)
        loginProgressIndicator = view.findViewById(R.id.login_progress_bar)
    }

    private fun loginInputVerification(view: View) : Boolean {
        if(phonenumberTextView.text.isNullOrEmpty()) {
            phonenumberTextView.error = getString(R.string.signup_phonenumber_empty_error)
            return false
        }

        if(passwordTextView.text.isNullOrEmpty()) {
            passwordTextView.error = getString(R.string.login_password_empty_please_provide_a_password)
            return false
        }

        return true
    }

    private fun loginRecaptchaEnabled(view: View) {
        if(!loginInputVerification(view))
            return
        loginProgressIndicator.visibility = View.VISIBLE

        val phoneNumber = phonenumberTextView.text.toString()
        val password = passwordTextView.text.toString()

        val loginStatusCard = view.findViewById<MaterialCardView>(R.id.login_status_card)
        val loginStatusText = view.findViewById<MaterialTextView>(R.id.login_error_text)

        loginStatusCard.visibility = View.GONE
        loginStatusText.text = null

        SafetyNet.getClient(requireContext())
                .verifyWithRecaptcha(getString(R.string.recaptcha_client_side_key))
                .addOnSuccessListener(ThreadExecutorPool.executorService, OnSuccessListener {
                    login(view, phoneNumber, password, it.tokenResult!!)
                })
                .addOnFailureListener(ThreadExecutorPool.executorService,
                        OnFailureListener {e -> if(e is ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            Log.e(javaClass.name, "Error: " +
                                    "${ CommonStatusCodes .getStatusCodeString(e.statusCode)}")
                        } else {
                            // A different, unknown type of error occurred.
                            Log.e(HomepageComposeNewFragment.TAG, "Unknown Error: ${e.message}")
                        }
                            activity?.runOnUiThread {
                                loginProgressIndicator.visibility = View.GONE
                            }
                })
    }

    private fun login(view: View, phonenumber: String, password: String, code: String) {
        val loginStatusCard = view.findViewById<MaterialCardView>(R.id.login_status_card)
        val loginStatusText = view.findViewById<MaterialTextView>(R.id.login_error_text)

        val url = view.context.getString(R.string.smswithoutborders_official_site_login)
        ThreadExecutorPool.executorService.execute {
            try {
                val networkResponseResults = Vault_V2.login(phonenumber, password, url, code)
                val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid

                UserArtifactsHandler.storeCredentials(requireContext(), phonenumber, password, uid)

                val platformsUrl = requireContext()
                        .getString(R.string.smswithoutborders_official_vault)

                val platformsViewModel = ViewModelProvider(this)[PlatformsViewModel::class.java]
                PlatformsHandler.storePlatforms(requireContext(),
                        platformsViewModel,
                        uid,
                        platformsUrl,
                        networkResponseResults.response.headers)

                onSuccessCallback(view)
                dismiss()
            } catch(e: Exception) {
                Log.e(javaClass.name, "Exception login", e)
                when(e.message) {
                    Vault_V2.INVALID_CREDENTIALS_EXCEPTION -> {
                        activity?.runOnUiThread {
                            loginStatusCard.visibility = View.VISIBLE
                            loginStatusText.text = getString(R.string.login_wrong_credentials)
                        }
                    }
                    Vault_V2.SERVER_ERROR_EXCEPTION -> {
                        activity?.runOnUiThread {
                            loginStatusCard.visibility = View.VISIBLE
                            loginStatusText.text = getString(R.string.login_server_something_went_wrong_please_try_again)
                        }
                    }
                }
            } finally {
                activity?.runOnUiThread {
                    loginProgressIndicator.visibility = View.GONE
                }
            }
        }

    }

    private fun onSuccessCallback(view: View) {
        if(UserArtifactsHandler.isCredentials(view.context)) {
            activity?.runOnUiThread {
                activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                        ?.performClick()
            }
        }
    }


}