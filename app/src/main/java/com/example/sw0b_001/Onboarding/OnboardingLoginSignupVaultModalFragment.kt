package com.example.sw0b_001.Onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver
import com.example.sw0b_001.LoginModalFragment
import com.example.sw0b_001.PlatformsModalFragment
import com.example.sw0b_001.R
import com.example.sw0b_001.SignupModalFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class OnboardingLoginSignupVaultModalFragment : BottomSheetDialogFragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboarding_login_signup_vault_modal_sheet, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.findViewById<View>(R.id.onboarding_login_signup_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()

        view.findViewById<MaterialButton>(R.id.onboarding_login_signup_login_btn)
                .setOnClickListener {
                    dismiss()

                    val loginModalFragment = LoginModalFragment()
                    fragmentTransaction?.add(loginModalFragment, "login_signup_login_vault_tag")
                    fragmentTransaction?.show(loginModalFragment)
                    fragmentTransaction?.commit()
                }

        view.findViewById<MaterialButton>(R.id.onboarding_login_signup_signup_btn)
                .setOnClickListener {
                    dismiss()

                    val signupModalFragment = SignupModalFragment()
                    fragmentTransaction?.add(signupModalFragment, "login_signup_signup_vault_tag")
                    fragmentTransaction?.show(signupModalFragment)
                    fragmentTransaction?.commit()
                }
    }
}