package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.OnboardingActivity
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingVaultFragment(context: Context) : OnboardingComponent(R.layout.fragment_onboarding_vault) {

    init {
        nextButtonText = context.getString(R.string.onboarding_next)
        previousButtonText = context.getString(R.string.onboarding_previous)
        skipButtonText = context.getString(R.string.onboarding_skip)
        skipOnboardingFragment = OnboardingFinishedFragment(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
                        .addComponent(OnboardingPublishExampleFragment(view.context))
                activity?.runOnUiThread {
                    activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                            ?.performClick()
                }
            }
        }

        val onboardingLoginSignupVaultModalFragment =
                OnboardingLoginSignupVaultModalFragment(loginSuccessRunnable, loginSuccessRunnable)

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.add(onboardingLoginSignupVaultModalFragment, "login_signup_vault_tag")
        fragmentTransaction?.show(onboardingLoginSignupVaultModalFragment)
        fragmentTransaction?.commitNow()
    }
}