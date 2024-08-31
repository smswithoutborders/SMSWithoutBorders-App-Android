package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import com.example.sw0b_001.Modals.LoginModalFragment
import com.example.sw0b_001.Modals.LoginSignupVaultModalFragment
import com.example.sw0b_001.Modals.SignupModalFragment
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingVaultFragment : OnboardingComponent(R.layout.fragment_onboarding_vault) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }
    override fun getButtonText(context: Context) {
        super.onAttach(context)
        nextButtonText = context.getString(R.string.onboarding_next)
        previousButtonText = context.getString(R.string.onboarding_previous)
        skipButtonText = context.getString(R.string.onboarding_skip)
        skipOnboardingFragment = OnboardingFinishedFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginSuccessRunnable = Runnable {
            if(activity is OnboardingComponent.ManageComponentsListing) {
                ((activity) as OnboardingComponent.ManageComponentsListing)
                    .addComponent(OnboardingVaultStorePlatformFragment())
            }
            activity?.runOnUiThread {
                activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                    ?.performClick()
            }
        }

        view.findViewById<MaterialButton>(R.id.onboarding_vault_login_btn)
            .setOnClickListener {
                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                val loginModalFragment = LoginModalFragment(loginSuccessRunnable)
                fragmentTransaction?.add(loginModalFragment, "login_signup_login_vault_tag")
                fragmentTransaction?.show(loginModalFragment)
                fragmentTransaction?.commit()
            }

        view.findViewById<MaterialButton>(R.id.onboarding_vault_signup_btn)
            .setOnClickListener {
                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                val signupModalFragment = SignupModalFragment(loginSuccessRunnable)
                fragmentTransaction?.add(signupModalFragment, "signup_tag")
                fragmentTransaction?.show(signupModalFragment)
                fragmentTransaction?.commit()
            }
    }
}