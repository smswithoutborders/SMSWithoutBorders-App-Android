package com.example.sw0b_001

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.HomepageComposeNewFragment.Companion.TAG
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

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

    private fun configureRecaptcha(view: View) {
        view.findViewById<MaterialButton>(R.id.signup_btn)
                .setOnClickListener {
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
                                            TODO("Implement 2FA when needed")
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
                                })
                }

    }
}