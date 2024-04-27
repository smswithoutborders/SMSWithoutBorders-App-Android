package com.example.sw0b_001

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Network
import com.github.kittinunf.fuel.core.Headers
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class OTPVerificationFragment(val vaultHeaders: Headers,
                              var headers: Headers,
                              val phoneNumber: String,
                              val uid: String,
                              val password: String) : Fragment() {

    private val SMS_CONSENT_REQUEST = 2  // Set to an unused request code
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(requireContext(), smsVerificationReceiver, intentFilter,
                ContextCompat.RECEIVER_EXPORTED)
        configureVerificationListener()
    }


    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get consent intent
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            if (consentIntent != null) {
                                startActivityForResult(consentIntent, SMS_CONSENT_REQUEST)
                            }
                        } catch (e: ActivityNotFoundException) {
                            e.printStackTrace()
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }
                }
            }
        }
    }
    private fun configureVerificationListener() {
        // Start listening for SMS User Consent broadcasts from senderPhoneNumber
        // The Task<Void> will be successful if SmsRetriever was able to start
        // SMS User Consent, and will error if there was an error starting.
        val smsSenderNumber = "VERIFY"
        val task = SmsRetriever.getClient(requireContext()).startSmsUserConsent(smsSenderNumber)
        task.addOnSuccessListener {
            Log.d(javaClass.name, "Successfully showed user consent screen")
        }

        task.addOnFailureListener {
            Log.e(javaClass.name, "Exception showing user consent screen", it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_otp_verification_code, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.ownership_verification_btn).setOnClickListener {
            val codeTextView = view.findViewById<TextInputEditText>(R.id.ownership_verification_input)
            if(codeTextView.text.isNullOrEmpty()) {
                codeTextView.error = getString(R.string.owernship_otp_please_enter_a_valid_code)
                return@setOnClickListener
            }
            it.isEnabled = false
            submitOTPCode(view, it, codeTextView.text.toString())
        }

        view.findViewById<MaterialTextView>(R.id.ownership_resend_code_by_sms_btn)
                .setOnClickListener {
                    val linearProgressIndicator = view
                            .findViewById<LinearProgressIndicator>(R.id.ownership_progress_bar)
                    linearProgressIndicator.visibility = View.VISIBLE
                    val optNetworkResponseResults = resendCode(it)
                    when(optNetworkResponseResults.response.statusCode) {
                        in 400..600 -> throw Exception(String(optNetworkResponseResults.response.data))
                    }
                    headers = optNetworkResponseResults.response.headers
                    linearProgressIndicator.visibility = View.GONE
                }
    }

    private fun resendCode(view: View) : Network.NetworkResponseResults{
        println("Re-sending code: $phoneNumber")
        val otpRequestUrl = view.context.getString(R.string.smswithoutborders_official_vault)
        return Vault_V2.otpRequest(otpRequestUrl,
                vaultHeaders, phoneNumber, uid)

    }

    private fun submitOTPCode(view: View, submitBtnView: View, code: String) {
        val linearProgressIndicator = view
                .findViewById<LinearProgressIndicator>(R.id.ownership_progress_bar)
        linearProgressIndicator.visibility = View.VISIBLE

        val otpSubmissionUrl = view.context
                .getString(R.string.smswithoutborders_official_otp_submission)

        ThreadExecutorPool.executorService.execute {
            val networkResponseResultsOTP = Vault_V2.otpSubmit(otpSubmissionUrl, headers, code)

            when(networkResponseResultsOTP.response.statusCode) {
                in 200..300 -> {
                    println("All good, code submitted!")
                    val url = context?.getString(R.string.smswithoutborders_official_site_signup)
                    val completeNetworkResponseResults =
                            Vault_V2.signupOtpComplete(url!!, networkResponseResultsOTP.response.headers)

                    when(completeNetworkResponseResults.response.statusCode) {
                        200 -> {
                            UserArtifactsHandler.storeCredentials(requireContext(), phoneNumber,
                                    password, uid)

                            loginAndFetchPlatforms(password, uid)

                            linearProgressIndicator.visibility = View.GONE
                            onSuccessCallback(view)
                        } else -> {
                            view.isEnabled = true
                            Log.e(javaClass.name, "Signup completion error: " +
                                    "${String(completeNetworkResponseResults.response.data)}")
                            linearProgressIndicator.visibility = View.GONE
                        }
                    }
                }
                else -> {
                    submitBtnView.isEnabled = true
                    Log.e(javaClass.name, "status code: ${networkResponseResultsOTP.response.statusCode}")
                    Log.e(javaClass.name, "OTP submission error: " +
                            "${String(networkResponseResultsOTP.response.data)}")
                    linearProgressIndicator.visibility = View.GONE
                }
            }

        }
    }

    private fun loginAndFetchPlatforms(password: String, uid: String) {
        try {
            Vault_V2.loginSyncPlatformsFlow(requireContext(), phoneNumber, password,
                    "", uid)
        } catch(e: Exception) {
            e.printStackTrace()
            when(e.message) {
                Vault_V2.INVALID_CREDENTIALS_EXCEPTION -> {
                    TODO("Invalidate and delete all creds")
                }
                Vault_V2.SERVER_ERROR_EXCEPTION -> {
                }
                else -> {
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            SMS_CONSENT_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val message = data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val smsTemplate = getString(R.string.otp_verification_code_template);

                    val code = message?.split(smsTemplate.toRegex())
                    if(code != null && code?.size!! > 1)
                        view?.findViewById<TextInputEditText>(R.id.ownership_verification_input)
                                ?.setText(code[1].replace(" ".toRegex(), ""))
                }
            }
        }
    }
}