package com.example.sw0b_001.Modals

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sw0b_001.HomepageComposeNewFragment.Companion.TAG
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.OTPVerificationActivity
import com.example.sw0b_001.R
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.hbb20.CountryCodePicker
import kotlinx.serialization.json.Json


class SignupModalFragment(private val onSuccessRunnable: Runnable?) :
        BottomSheetDialogFragment(R.layout.fragment_signup_modal) {

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

        val bottomSheet = view.findViewById<View>(R.id.signup_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        configurePrivacyPolicyCheckbox(view)
        configureRecaptcha(view)
    }

    private fun linkPrivacyPolicy(view: View?) {
        // TODO: check for production
        val intentUri = Uri.parse(resources.getString(R.string.smswithoutborders_official_privacy_policy))
        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        startActivity(intent)
    }

    private fun configurePrivacyPolicyCheckbox(view: View) {
        val checkbox = view.findViewById<MaterialCheckBox>(R.id.signup_read_privacy_policy_checkbox)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.cancelPendingInputEvents()
                linkPrivacyPolicy(widget)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }

        val linkText = SpannableString(getString(R.string.signup_i_have_read_the_privacy_policy_end))
        linkText.setSpan(clickableSpan, 0, linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val cs = TextUtils.expandTemplate(
                getString(R.string.signup_i_have_read_the_privacy_policy_start) + " ^1", linkText)

        checkbox.text = cs
        checkbox.movementMethod = LinkMovementMethod.getInstance()

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
        view.findViewById<TextInputEditText>(R.id.signup_phonenumber_text_input).error = null
        view.findViewById<TextInputEditText>(R.id.signup_password_text_input).error = null
        view.findViewById<TextInputEditText>(R.id.signup_password_text_retry_input).error = null
        view.findViewById<MaterialCheckBox>(R.id.signup_read_privacy_policy_checkbox).error = null
        view.findViewById<View>(R.id.signup_status_card).visibility = View.GONE
    }
    private fun verifyInput(view: View): Boolean {
        val phoneNumberView = view.findViewById<TextInputEditText>(R.id.signup_phonenumber_text_input)
        if(phoneNumberView.text.isNullOrEmpty()) {
            phoneNumberView.error = getString(R.string.signup_phonenumber_empty_error)
            return false
        }

        val passwordView = view.findViewById<TextInputEditText>(R.id.signup_password_text_input)
        if(passwordView.text.isNullOrEmpty()) {
            passwordView.error = getString(R.string.signup_please_provide_a_strong_password)
            return false
        }

        val passwordRetryView = view.findViewById<TextInputEditText>(R.id.signup_password_text_retry_input)
        if(passwordRetryView.text.isNullOrEmpty()) {
            passwordRetryView.error = getString(R.string.signup_please_re_enter_your_password)
            return false
        }

        if(passwordView.text.toString() != passwordRetryView.text.toString()) {
            passwordRetryView.error = getString(R.string.signup_your_passwords_do_not_match)
            return false
        }

        val checkbox = view.findViewById<MaterialCheckBox>(R.id.signup_read_privacy_policy_checkbox)
        if(!checkbox.isChecked) {
            checkbox.error = getString(R.string.signup_you_have_to_read_the_privacy_policy_to_proceed)
            return false
        }

        return true
    }


    private fun signup(view: View, phonenumber: String, countryCode: String, password: String ) {
        val url = getString(R.string.smswithoutborders_official_site_signup)
        try {
            val networkResponseResults = Vault_V2.signup(url, phonenumber, "", countryCode,
                    password, "")
            when(networkResponseResults.response.statusCode) {
                400 -> {
                    Log.e(javaClass.name, String(networkResponseResults.response.data))
                    activity?.runOnUiThread {
                        view.findViewById<MaterialButton>(R.id.signup_btn)
                                .isEnabled = true
                        view.findViewById<View>(R.id.signup_status_card)
                                .visibility = View.VISIBLE
                        view.findViewById<View>(R.id.signup_progress_bar)
                                .visibility = View.GONE
                        view.findViewById<MaterialTextView>(R.id.login_error_text)
                                .text = String(networkResponseResults.response.data)
                    }
                }
                409 -> {
                    activity?.runOnUiThread {
                        view.findViewById<MaterialButton>(R.id.signup_btn)
                                .isEnabled = true
                        view.findViewById<View>(R.id.signup_status_card)
                                .visibility = View.VISIBLE
                        view.findViewById<View>(R.id.signup_progress_bar)
                                .visibility = View.GONE
                        view.findViewById<MaterialTextView>(R.id.login_error_text)
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
                view.findViewById<MaterialButton>(R.id.signup_btn)
                        .isEnabled = true
            }
        }
    }

    private fun configureRecaptcha(view: View) {
        view.findViewById<MaterialButton>(R.id.signup_btn)
                .setOnClickListener {
                    nullifyInputs(view)
                    if(!verifyInput(view))
                        return@setOnClickListener

                    val signupProgressBar = view.findViewById<LinearProgressIndicator>(R.id.signup_progress_bar)
                    signupProgressBar.visibility = View.VISIBLE

                    val signupCountryCodePicker = view.findViewById<CountryCodePicker>(R.id.signup_country_code_picker)
                    val countryCode = "+" + signupCountryCodePicker.selectedCountryCode
                    val phonenumber = view.findViewById<TextInputEditText>(R.id.signup_phonenumber_text_input).text
                        .toString()
                        .replace(" ", "")
                    val password = view.findViewById<TextInputEditText>(R.id.signup_password_text_input).text.toString()

                    signup(view, phonenumber, countryCode, password)
                }

    }
}