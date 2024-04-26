package com.example.sw0b_001

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Data.v2.Vault_V2
import com.example.sw0b_001.HomepageComposeNewFragment.Companion.TAG
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker
import kotlinx.serialization.json.Json
import kotlin.math.sign

class SignupModalFragment : BottomSheetDialogFragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_modal, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.signup_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        configureRecaptcha(view)
    }

    private fun replaceFragment(vaultHeaders: Headers,
                                headers: Headers,
                                phonenumber: String,
                                uid: String,
                                password: String) {
        activity?.runOnUiThread {
            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            val otpVerificationFragment = OTPVerificationFragment(
                    vaultHeaders,
                    headers,
                    phonenumber,
                    uid, password)

            fragmentTransaction?.replace(R.id.onboarding_fragment_container,
                    otpVerificationFragment)
            fragmentTransaction?.commitNow()
        }
    }

    private fun verifyInput(view: View): Boolean {
        val phoneNumberView = view.findViewById<TextInputEditText>(R
                .id.signup_phonenumber_text_input)
        if(phoneNumberView.text.isNullOrEmpty()) {
            phoneNumberView.error = getString(R.string.signup_phonenumber_empty_error)
            return false
        }

        val passwordView = view.findViewById<TextInputEditText>(R
                .id.signup_password_text_input)
        if(passwordView.text.isNullOrEmpty()) {
            passwordView.error = getString(R.string.signup_please_provide_a_strong_password)
            return false
        }

        val passwordRetryView = view.findViewById<TextInputEditText>(R
                .id.signup_password_text_retry_input)
        if(passwordRetryView.text.isNullOrEmpty()) {
            passwordRetryView.error = getString(R.string.signup_please_re_enter_your_password)
            return false
        }

        if(passwordView.text.toString() != passwordRetryView.text.toString()) {
            passwordRetryView.error = getString(R.string.signup_your_passwords_do_not_match)
            return false
        }

        return true
    }


    private fun signup(view: View, phonenumber: String, countryCode: String, password: String,
                       captcha_token: String) {
        val url = getString(R.string.smswithoutborders_official_site_signup)
        try {
            val networkResponseResults = Vault_V2.signup(url, phonenumber, "", countryCode,
                    password, captcha_token)
            val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid

            val otpRequestUrl = view.context.getString(R.string.smswithoutborders_official_vault)
            val completePhoneNumber = countryCode + phonenumber

            println("Complete phone number: $completePhoneNumber")
            val optNetworkResponseResults = Vault_V2.otpRequest(otpRequestUrl,
                    networkResponseResults.response.headers, completePhoneNumber, uid)

            when(networkResponseResults.response.statusCode) {
                in 400..600 -> throw Exception(String(networkResponseResults.response.data))
            }

            // TODO: do something in case the request fails to go out

            replaceFragment(networkResponseResults.response.headers,
                    optNetworkResponseResults.response.headers,
                    completePhoneNumber,
                    uid,
                    password)
            dismiss()
        } catch(e: Exception) {
            e.printStackTrace()
            activity?.runOnUiThread {
                view.findViewById<MaterialButton>(R.id.signup_btn)
                        .isEnabled = true
            }
        }
    }

    private fun configureRecaptcha(view: View) {
        view.findViewById<MaterialButton>(R.id.signup_btn)
                .setOnClickListener {

                    if(!verifyInput(view))
                        return@setOnClickListener

                    val signupProgressBar = view.findViewById<LinearProgressIndicator>(R
                            .id.signup_progress_bar)
                    signupProgressBar.visibility = View.VISIBLE

                    val signupCountryCodePicker = view.findViewById<CountryCodePicker>(R
                            .id.signup_country_code_picker)
                    val countryCode = "+" + signupCountryCodePicker.selectedCountryCode
                    SafetyNet.getClient(requireContext())
                            .verifyWithRecaptcha(getString(R.string.recaptcha_client_side_key))
                            .addOnSuccessListener(ThreadExecutorPool.executorService,
                                    OnSuccessListener { response ->
                                        // Indicates communication with reCAPTCHA service was
                                        // successful.
                                        val userResponseToken = response.tokenResult
                                        if (response.tokenResult?.isNotEmpty() == true) {
                                            // Validate the user response token using the
                                            // reCAPTCHA siteverify API.
                                            Log.d(javaClass.name, "Recaptcha code: " +
                                                    "$userResponseToken")


                                            activity?.runOnUiThread {
                                                view.findViewById<MaterialButton>(R.id.signup_btn)
                                                        .isEnabled = false
                                            }

                                            val phonenumber = view.findViewById<TextInputEditText>(R
                                                    .id.signup_phonenumber_text_input).text.toString()
                                            val password = view.findViewById<TextInputEditText>(R
                                                    .id.signup_password_text_input).text.toString()

                                            signup(view, phonenumber, countryCode, password,
                                                    userResponseToken!!)
                                        }
                                    })
                            .addOnFailureListener(ThreadExecutorPool.executorService,
                                    OnFailureListener { e -> if (e is ApiException) {
                                        // An error occurred when communicating with the
                                        // reCAPTCHA service. Refer to the status code to
                                        // handle the error appropriately.
                                        Log.d(TAG,
                                                "Error: ${CommonStatusCodes
                                                        .getStatusCodeString(e.statusCode)}")
                                    } else {
                                        // A different, unknown type of error occurred.
                                        Log.d(TAG, "Error: ${e.message}")
                                    }
                                        activity?.runOnUiThread {
                                            signupProgressBar.visibility = View.GONE
                                        }
                                })
                }

    }

    private fun beginOTP() {

    }
}