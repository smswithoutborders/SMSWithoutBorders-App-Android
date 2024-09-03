package com.example.sw0b_001.Modals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sw0b_001.BuildConfig
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.Vault
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
import io.grpc.StatusRuntimeException
import kotlinx.serialization.json.Json


class RecoverModalFragment(private val onSuccessRunnable: Runnable?) :
        BottomSheetDialogFragment(R.layout.fragment_recover_modal) {

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

    private fun populateDebugMode(view: View) {
        val globalPhoneNumber = "1123457528"
        val globalPassword = "dMd2Kmo9#"
        val globalCountryCode = "CM"

        view.findViewById<TextInputEditText>(R.id.recover_phonenumber_text_input).text =
            Editable.Factory().newEditable(globalPhoneNumber)

        view.findViewById<TextInputEditText>(R.id.recovery_password_text_input).text =
            Editable.Factory().newEditable(globalPassword)

        view.findViewById<TextInputEditText>(R.id.recovery_password_text_retry_input).text =
            Editable.Factory().newEditable(globalPassword)

        view.findViewById<CountryCodePicker>(R.id.recover_country_code_picker)
            .setCountryForNameCode(globalCountryCode)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vault = Vault(requireContext())

        val bottomSheet = view.findViewById<View>(R.id.recover_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        configureRecaptcha(view)

        if(BuildConfig.DEBUG) {
            populateDebugMode(view)
        }
    }

    private fun nullifyInputs(view: View) {
        view.findViewById<TextInputEditText>(R.id.recover_phonenumber_text_input).error = null
        view.findViewById<TextInputEditText>(R.id.recovery_password_text_input).error = null
        view.findViewById<TextInputEditText>(R.id.recovery_password_text_retry_input).error = null
        view.findViewById<View>(R.id.recover_status_card).visibility = View.GONE
    }

    private fun verifyInput(view: View): Boolean {
        val phoneNumberView = view.findViewById<TextInputEditText>(
            R.id.recover_phonenumber_text_input)
        if(phoneNumberView.text.isNullOrEmpty()) {
            phoneNumberView.error = getString(R.string.signup_phonenumber_empty_error)
            return false
        }

        val passwordView = view.findViewById<TextInputEditText>(R.id.recovery_password_text_input)
        if(passwordView.text.isNullOrEmpty()) {
            passwordView.error = getString(R.string.signup_please_provide_a_strong_password)
            return false
        }

        val passwordRetryView = view.findViewById<TextInputEditText>(
            R.id.recovery_password_text_retry_input)
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


    private fun recover(view: View, phonenumber: String, password: String ) {
        try {
            val response = vault.recoverEntityPassword(requireContext(),
                phonenumber, password)
            if(response.requiresOwnershipProof) {
                activity?.runOnUiThread {
                    val intent = Intent(requireContext(), OTPVerificationActivity::class.java)
                    intent.putExtra("phone_number", phonenumber)
                    intent.putExtra("password", password)
                    intent.putExtra("type", OTPVerificationActivity.Type.RECOVER.type)
                    activityLauncher.launch(intent)
                }
            }
        } catch(e: StatusRuntimeException) {
            activity?.runOnUiThread {
                view.findViewById<View>(R.id.recover_status_card)
                    .visibility = View.VISIBLE
                view.findViewById<MaterialTextView>(R.id.recover_error_text)
                    .text = e.status.description
            }
        } catch(e: Exception) {
            e.printStackTrace()
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        } finally {
            activity?.runOnUiThread {
                view.findViewById<View>(R.id.recovery_progress_bar)
                    .visibility = View.GONE
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

                    val signupProgressBar = view.findViewById<LinearProgressIndicator>(
                        R.id.recovery_progress_bar)
                    signupProgressBar.visibility = View.VISIBLE

                    val signupCountryCodePicker = view.findViewById<CountryCodePicker>(
                        R.id.recover_country_code_picker)
                    val dialingCode = signupCountryCodePicker.selectedCountryCodeWithPlus
                    val phoneNumber = dialingCode + view.findViewById<TextInputEditText>(
                        R.id.recover_phonenumber_text_input).text
                        .toString()
                        .replace(" ", "")
                    val password = view.findViewById<TextInputEditText>(
                        R.id.recovery_password_text_input).text.toString()

                    ThreadExecutorPool.executorService.execute {
                        recover(view, phoneNumber, password)
                    }
                }

    }

    override fun onDestroy() {
        super.onDestroy()
        vault.shutdown()
    }
}