package com.example.sw0b_001.Modals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.OTPVerificationActivity
import com.example.sw0b_001.R
import com.github.kittinunf.fuel.core.Headers
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.hbb20.CountryCodePicker
import kotlinx.serialization.json.Json


class RecoverModalFragment(private val onSuccessRunnable: Runnable?) :
        BottomSheetDialogFragment(R.layout.fragment_recover_modal) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>


    private val activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when(it.resultCode) {
                    Activity.RESULT_OK -> onSuccessRunnable?.run()
                    else -> { }
                }
            }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.recover_constraint)

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
            val intent = Intent(requireContext(), OTPVerificationActivity::class.java)
            intent.putExtra("phone_number", phonenumber)
            intent.putExtra("password", password)
            intent.putExtra("uid", uid)
            intent.putExtra("opt_request_cookie", headers["Set-Cookie"].first())
            intent.putExtra("signup_request_cookie", vaultHeaders["Set-Cookie"].first())
            activityLauncher.launch(intent)
        }
    }

    private fun nullifyInputs(view: View) {
        view.findViewById<TextInputEditText>(R.id.recover_phonenumber_text).error = null
        view.findViewById<TextInputEditText>(R.id.recovery_password_text_input).error = null
        view.findViewById<TextInputEditText>(R.id.recovery_password_text_retry).error = null
        view.findViewById<View>(R.id.recover_status_card).visibility = View.GONE
    }

    private fun verifyInput(view: View): Boolean {
        val phoneNumberView = view.findViewById<TextInputEditText>(R.id.recover_phonenumber_text)
        if(phoneNumberView.text.isNullOrEmpty()) {
            phoneNumberView.error = getString(R.string.signup_phonenumber_empty_error)
            return false
        }

        val passwordView = view.findViewById<TextInputEditText>(R.id.recovery_password_text_input)
        if(passwordView.text.isNullOrEmpty()) {
            passwordView.error = getString(R.string.signup_please_provide_a_strong_password)
            return false
        }

        val passwordRetryView = view.findViewById<TextInputEditText>(R.id.recovery_password_text_retry)
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


    private fun recover(view: View, phonenumber: String, countryCode: String, password: String ) {
        val url = getString(R.string.smswithoutborders_official_site_signup)
        try {
            val networkResponseResults = Vault_V2.signup(url, phonenumber, "", countryCode,
                    password, "")
            activity?.run {
                view.findViewById<MaterialButton>(R.id.recovery_continue)
                    .isEnabled = true
                view.findViewById<View>(R.id.recover_status_card)
                    .visibility = View.VISIBLE
                view.findViewById<View>(R.id.recovery_progress_bar)
                    .visibility = View.GONE
            }
            when(networkResponseResults.response.statusCode) {
                400 -> {
                    Log.e(javaClass.name, String(networkResponseResults.response.data))
                    activity?.runOnUiThread {
                        view.findViewById<MaterialTextView>(R.id.recover_error_text)
                                .text = String(networkResponseResults.response.data)
                    }
                }
                409 -> {
                    activity?.runOnUiThread {
                        view.findViewById<MaterialTextView>(R.id.recover_error_text)
                                .text = getString(R.string.signup_something_went_wrong_please_check_this_account_does_not_already_exist)
                    }
                }
            }
            val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid

            val otpRequestUrl = view.context.getString(R.string.smswithoutborders_official_vault)
            val completePhoneNumber = countryCode + phonenumber

            println("Complete phone number: $completePhoneNumber")
            val optNetworkResponseResults = Vault_V2.otpRequest(otpRequestUrl,
                    networkResponseResults.response.headers, completePhoneNumber, uid)

            when(optNetworkResponseResults.response.statusCode) {
                in 400..600 -> {
                    throw Exception(String(optNetworkResponseResults.response.data))
                }
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
                view.findViewById<MaterialButton>(R.id.recovery_continue)
                        .isEnabled = true
            }
        }
    }

    private fun configureRecaptcha(view: View) {
        view.findViewById<MaterialButton>(R.id.recovery_continue)
                .setOnClickListener {
                    nullifyInputs(view)
                    if(!verifyInput(view))
                        return@setOnClickListener

                    val signupProgressBar = view.findViewById<LinearProgressIndicator>(R.id.recovery_progress_bar)
                    signupProgressBar.visibility = View.VISIBLE

                    val signupCountryCodePicker = view.findViewById<CountryCodePicker>(R.id.recover_country_code_picker)
                    val countryCode = "+" + signupCountryCodePicker.selectedCountryCode
                    val phonenumber = view.findViewById<TextInputEditText>(R.id.recover_phonenumber_text_input).text
                        .toString()
                        .replace(" ", "")
                    val password = view.findViewById<TextInputEditText>(R.id.recovery_password_text_input).text.toString()

                    ThreadExecutorPool.executorService.execute {
                        recover(view, phonenumber, countryCode, password)
                    }
                }

    }
}