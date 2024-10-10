package com.example.sw0b_001.Modals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sw0b_001.BuildConfig
import com.example.sw0b_001.Models.Vault
import com.example.sw0b_001.OTPVerificationActivity
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.hbb20.CountryCodePicker
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginModalFragment(private val onSuccessRunnable: Runnable?) :
        BottomSheetDialogFragment(R.layout.fragment_login_modal) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var phonenumberTextView: TextInputEditText
    private lateinit var passwordTextView: TextInputEditText
    private lateinit var loginProgressIndicator: LinearProgressIndicator
    private lateinit var countryCodePickerView: CountryCodePicker
    private lateinit var forgotPasswordBtn: MaterialButton

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
        val globalCountryCode = "CM"
        val globalPassword = "dMd2Kmo9#"

        phonenumberTextView.text = Editable.Factory().newEditable(globalPhoneNumber)
        passwordTextView.text = Editable.Factory().newEditable(globalPassword)
        countryCodePickerView.setCountryForNameCode(globalCountryCode)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vault = Vault(requireContext())

        view.findViewById<MaterialButton>(R.id.login_btn).setOnClickListener {
            loginRecaptchaEnabled(view)
        }

        view.findViewById<MaterialButton>(R.id.login_already_have_account).setOnClickListener {
            dismiss()
            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            val signupModalFragment = SignupModalFragment(onSuccessRunnable)
            fragmentTransaction?.add(signupModalFragment, "signup_tag")
            fragmentTransaction?.show(signupModalFragment)
            fragmentTransaction?.commit()
        }


        val bottomSheet = view.findViewById<View>(R.id.login_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        countryCodePickerView = view.findViewById(R.id.login_country_code_picker)
        phonenumberTextView = view.findViewById(R.id.login_phonenumber_text_input)
        passwordTextView = view.findViewById(R.id.login_password_text_input)
        loginProgressIndicator = view.findViewById(R.id.login_progress_bar)

        forgotPasswordBtn = view.findViewById(R.id.login_forgot_password)
        forgotPasswordBtn.setOnClickListener {
            dismiss()
            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            val recoverModalFragment = RecoverModalFragment(onSuccessRunnable)
            fragmentTransaction?.add(recoverModalFragment, "recovery_tag")
            fragmentTransaction?.show(recoverModalFragment)
            fragmentTransaction?.commit()
        }

        if(BuildConfig.DEBUG) {
            populateDebugMode(view)
        }
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

    private fun disableComponents(view: View) {
        isCancelable = false

        view.findViewById<MaterialButton>(R.id.login_btn).isEnabled = false
        phonenumberTextView.isEnabled = false
        passwordTextView.isEnabled = false
        countryCodePickerView.isEnabled = false
        forgotPasswordBtn.isEnabled = false

        view.findViewById<MaterialButton>(R.id.login_already_have_account).isEnabled = false
    }

    private fun enableComponents(view: View) {
        isCancelable = true

        view.findViewById<MaterialButton>(R.id.login_btn).isEnabled = true
        phonenumberTextView.isEnabled = true
        passwordTextView.isEnabled = true
        countryCodePickerView.isEnabled = true
        forgotPasswordBtn.isEnabled = true

        view.findViewById<MaterialButton>(R.id.login_already_have_account).isEnabled = true
    }

    private fun loginRecaptchaEnabled(view: View) {
        if(!loginInputVerification(view))
            return
        loginProgressIndicator.visibility = View.VISIBLE
        disableComponents(view)

        val dialingCode = countryCodePickerView.selectedCountryCodeWithPlus
        val phoneNumber = dialingCode + phonenumberTextView.text.toString()
                .replace(" ", "")
        val password = passwordTextView.text.toString()

        val loginStatusCard = view.findViewById<MaterialCardView>(R.id.login_status_card)
        val loginStatusText = view.findViewById<MaterialTextView>(R.id.login_error_text)

        loginStatusCard.visibility = View.GONE
        loginStatusText.text = null

        try {
            login(view, phoneNumber, password)
        } catch (e: Exception) {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(view: View,
                      phoneNumber: String,
                      password: String) {

        val loginStatusCard = view.findViewById<MaterialCardView>(R.id.login_status_card)
        val loginStatusText = view.findViewById<MaterialTextView>(R.id.login_error_text)

        CoroutineScope(Dispatchers.Default).launch{
            try {
                val response = vault.authenticateEntity(requireContext(),
                    phoneNumber, password)

                if(response.requiresOwnershipProof) {
                    activity?.runOnUiThread {
                        val intent = Intent(requireContext(), OTPVerificationActivity::class.java)
                        intent.putExtra("phone_number", phoneNumber)
                        intent.putExtra("password", password)
                        intent.putExtra("next_attempt_timestamp", response.nextAttemptTimestamp.toString())
                        intent.putExtra("type",
                            OTPVerificationActivity.Type.AUTHENTICATE.type)
                        activityLauncher.launch(intent)
                    }
                }
            } catch(e: StatusRuntimeException) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    loginStatusCard.visibility = View.VISIBLE
                    loginStatusText.text = e.status.description
                }
            } catch(e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                activity?.runOnUiThread {
                    loginProgressIndicator.visibility = View.GONE
                    enableComponents(view)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        vault.shutdown()
    }

}