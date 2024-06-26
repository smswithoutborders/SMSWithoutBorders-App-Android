package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import com.example.sw0b_001.Modals.LoginSignupVaultModalFragment
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

        val tryExampleButton = view.findViewById<MaterialButton>(
                R.id.onboarding_welcome_vaults_description_try_example_btn)

        tryExampleButton?.setOnClickListener { nextFragment(view) }
    }

    private fun nextFragment(view: View) {
        val loginSuccessRunnable = Runnable {
            if(activity is OnboardingComponent.ManageComponentsListing) {
                ((activity) as OnboardingComponent.ManageComponentsListing)
                        .addComponent(OnboardingVaultStorePlatformFragment())
                activity?.runOnUiThread {
                    activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                            ?.performClick()
                }
            }
        }

        val loginSignupVaultModalFragment =
                LoginSignupVaultModalFragment(loginSuccessRunnable, loginSuccessRunnable)

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.add(loginSignupVaultModalFragment, "login_signup_vault_tag")
        fragmentTransaction?.show(loginSignupVaultModalFragment)
        fragmentTransaction?.commitNow()
    }
}