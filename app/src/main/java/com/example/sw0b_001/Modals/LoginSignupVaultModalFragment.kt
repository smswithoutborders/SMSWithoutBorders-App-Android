package com.example.sw0b_001.Modals

import android.os.Bundle
import android.view.View
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class LoginSignupVaultModalFragment(private val onSuccessRunnable: Runnable?,
                                    private val onSignupSuccessRunnable: Runnable) :
        BottomSheetDialogFragment(R.layout.fragment_onboarding_login_signup_vault_modal_sheet) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
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
                    val loginModalFragment = LoginModalFragment(onSuccessRunnable)
                    fragmentTransaction?.add(loginModalFragment, "login_signup_login_vault_tag")
                    fragmentTransaction?.show(loginModalFragment)
                    fragmentTransaction?.commit()
                }

        view.findViewById<MaterialButton>(R.id.onboarding_login_signup_signup_btn)
                .setOnClickListener {
                    dismiss()
                    val signupModalFragment = SignupModalFragment(onSignupSuccessRunnable)
                    fragmentTransaction?.add(signupModalFragment, "signup_tag")
                    fragmentTransaction?.show(signupModalFragment)
                    fragmentTransaction?.commit()
                }
    }
}