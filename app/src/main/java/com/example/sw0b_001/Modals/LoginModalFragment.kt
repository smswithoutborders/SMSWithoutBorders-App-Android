package com.example.sw0b_001.Modals

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.sw0b_001.BuildConfig
import com.example.sw0b_001.HomepageComposeNewFragment
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.hbb20.CountryCodePicker

class LoginModalFragment(private val onSuccessRunnable: Runnable?) :
        BottomSheetDialogFragment(R.layout.fragment_login_modal) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var phonenumberTextView: TextInputEditText
    private lateinit var passwordTextView: TextInputEditText
    private lateinit var loginProgressIndicator: LinearProgressIndicator
    private lateinit var countryCodePickerView: CountryCodePicker

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.login_btn).setOnClickListener {
            loginRecaptchaEnabled(view)
        }

//        val customUrlView = view.findViewById<TextInputLayout>(R.id.login_url)
//        view.findViewById<MaterialTextView>(R.id.login_advanced_toggle).setOnClickListener {
//            if(customUrlView.visibility == View.VISIBLE)
//                customUrlView.visibility = View.INVISIBLE
//            else
//                customUrlView.visibility = View.VISIBLE
//        }

        val bottomSheet = view.findViewById<View>(R.id.login_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        countryCodePickerView = view.findViewById(R.id.login_country_code_picker)
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

        val countryCode = "+" + countryCodePickerView.selectedCountryCode
        val phoneNumber = countryCode + phonenumberTextView.text.toString()
                .replace(" ", "")
        val password = passwordTextView.text.toString()

        val loginStatusCard = view.findViewById<MaterialCardView>(R.id.login_status_card)
        val loginStatusText = view.findViewById<MaterialTextView>(R.id.login_error_text)

        loginStatusCard.visibility = View.GONE
        loginStatusText.text = null

        if(BuildConfig.IS_RECAPTCHA)
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
                                activity?.runOnUiThread {
                                    Toast.makeText(requireContext(),
                                            CommonStatusCodes.getStatusCodeString(e.statusCode),
                                            Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // A different, unknown type of error occurred.
                                Log.e(HomepageComposeNewFragment.TAG, "Unknown Error: ${e.message}")
                                activity?.runOnUiThread {
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                                e.printStackTrace()
                                activity?.runOnUiThread {
                                    loginProgressIndicator.visibility = View.GONE
                                }
                    })
        else {
            try {
                login(view, phoneNumber, password, "")
            } catch (e: Exception) {
                Log.e(HomepageComposeNewFragment.TAG, "Unknown Error: ${e.message}")
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun login(view: View, phonenumber: String, password: String, code: String) {
        val loginStatusCard = view.findViewById<MaterialCardView>(R.id.login_status_card)
        val loginStatusText = view.findViewById<MaterialTextView>(R.id.login_error_text)

        val url = view.context.getString(R.string.smswithoutborders_official_site_login)
        ThreadExecutorPool.executorService.execute {
            try {
                Vault_V2.loginSyncPlatformsFlow(requireContext(), phonenumber, password, code, fragment = this)
                onSuccessRunnable?.run()
                dismiss()
            } catch(e: Exception) {
                e.printStackTrace()
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
                    else -> {
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

}