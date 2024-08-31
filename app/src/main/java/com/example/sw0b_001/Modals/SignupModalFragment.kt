package com.example.sw0b_001.Modals

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sw0b_001.BuildConfig
import com.example.sw0b_001.HomepageComposeNewFragment.Companion.TAG
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.Vault
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.OTPVerificationActivity
import com.example.sw0b_001.R
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
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
import io.grpc.StatusRuntimeException
import kotlinx.serialization.json.Json


class SignupModalFragment(private val onSuccessRunnable: Runnable?) :
        BottomSheetDialogFragment(R.layout.fragment_signup_modal) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var vault: Vault

    private val activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when(it.resultCode) {
                    Activity.RESULT_OK -> {
                        onSuccessRunnable?.run()
                        dismiss()
                    }
                    else -> { }
                }
            }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vault = Vault(requireContext())

        view.findViewById<MaterialCheckBox>(R.id.signup_read_privacy_policy_checkbox)
            .setOnCheckedChangeListener { _, isChecked ->
                view.findViewById<MaterialButton>(R.id.signup_btn).isEnabled = isChecked
            }

        val bottomSheet = view.findViewById<View>(R.id.signup_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<MaterialButton>(R.id.signup_already_have_account).setOnClickListener {
            dismiss()
            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            val loginModalFragment = LoginModalFragment(onSuccessRunnable)
            fragmentTransaction?.add(loginModalFragment, "login_signup_login_vault_tag")
            fragmentTransaction?.show(loginModalFragment)
            fragmentTransaction?.commit()
        }

        configurePrivacyPolicyCheckbox(view)
        configureRecaptcha(view)

        if(BuildConfig.DEBUG) {
            populateDebugMode(view)
        }
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

    private fun populateDebugMode(view: View) {
        val globalPhoneNumber = "1123457528"
        val globalCountryCode = "CM"
        val globalPassword = "dMd2Kmo9#"

        view.findViewById<TextInputEditText>(R.id.signup_phonenumber_text_input).text =
            Editable.Factory().newEditable(globalPhoneNumber)

        view.findViewById<TextInputEditText>(R.id.signup_password_text_input).text =
            Editable.Factory().newEditable(globalPassword)

        view.findViewById<TextInputEditText>(R.id.signup_password_text_retry_input).text =
            Editable.Factory().newEditable(globalPassword)

        view.findViewById<CountryCodePicker>(R.id.signup_country_code_picker)
            .setCountryForNameCode(globalCountryCode)

        view.findViewById<MaterialCheckBox>(R.id.signup_read_privacy_policy_checkbox).isChecked = true
        view.findViewById<View>(R.id.signup_status_card).visibility = View.GONE
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
        try {
            val response = vault.createEntity(requireContext(), phonenumber, countryCode, password)
            if(response.requiresOwnershipProof) {
                activity?.runOnUiThread {
                    val intent = Intent(requireContext(), OTPVerificationActivity::class.java)
                    intent.putExtra("phone_number", phonenumber)
                    intent.putExtra("password", password)
                    intent.putExtra("country_code", countryCode)
                    intent.putExtra("type", OTPVerificationActivity.Type.CREATE.type)
                    activityLauncher.launch(intent)
                }
            }
        } catch(e: StatusRuntimeException) {
            activity?.runOnUiThread {
                view.findViewById<View>(R.id.signup_status_card).visibility = View.VISIBLE
                view.findViewById<MaterialTextView>(R.id.login_error_text).text = e.status.description
            }
        } catch(e: Exception) {
            e.printStackTrace()
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        } finally {
            activity?.runOnUiThread {
                view.findViewById<MaterialButton>(R.id.signup_btn).isEnabled = true
                view.findViewById<View>(R.id.signup_progress_bar).visibility = View.GONE
            }
        }
    }

    private fun configureRecaptcha(view: View) {
        view.findViewById<MaterialButton>(R.id.signup_btn)
                .setOnClickListener {
                    nullifyInputs(view)
                    if(!verifyInput(view))
                        return@setOnClickListener

                    val signupProgressBar = view.findViewById<LinearProgressIndicator>(
                        R.id.signup_progress_bar)
                    signupProgressBar.visibility = View.VISIBLE
                    view.findViewById<MaterialButton>(R.id.signup_btn).isEnabled = false

                    val signupCountryCodePicker = view.findViewById<CountryCodePicker>(
                        R.id.signup_country_code_picker)
                    val countryCode = signupCountryCodePicker.selectedCountryNameCode
                    val dialingCode = signupCountryCodePicker.selectedCountryCodeWithPlus
                    val phonenumber = dialingCode + view.findViewById<TextInputEditText>(
                        R.id.signup_phonenumber_text_input).text
                        .toString()
                        .replace(" ", "")
                    val password = view.findViewById<TextInputEditText>(
                        R.id.signup_password_text_input).text.toString()

                    ThreadExecutorPool.executorService.execute {
                        signup(view, phonenumber, countryCode, password)
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()
        vault.shutdown()
    }
}