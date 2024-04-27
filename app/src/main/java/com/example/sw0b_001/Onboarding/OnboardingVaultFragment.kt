package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.Data.Platforms.PlatformsHandler
import com.example.sw0b_001.Data.Platforms._PlatformsHandler
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.OnboardingActivity
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingVaultFragment : OnboardingComponent(R.layout.fragment_onboarding_vault) {

    init {
        nextButtonText = ""
        previousButtonText = "Previous"
        skipButtonText = "skip"
        skipOnboardingFragment = OnboardingSkippedAllFragment()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val tryExampleButton = activity
                ?.findViewById<MaterialButton>(R.id.onboarding_welcome_vaults_description_try_example_btn)

        tryExampleButton?.setOnClickListener { nextFragment() }


        if(UserArtifactsHandler.isCredentials(view.context)) {
            activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                    ?.performClick()
            if(activity is OnboardingComponent.ManageComponentsListing)
                (activity as ManageComponentsListing).removeComponent(this)
        }
    }

    private fun nextFragment() {
        val onboardingLoginSignupVaultModalFragment = OnboardingLoginSignupVaultModalFragment()

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.add(onboardingLoginSignupVaultModalFragment, "login_signup_vault_tag")
        fragmentTransaction?.show(onboardingLoginSignupVaultModalFragment)
        fragmentTransaction?.commitNow()
    }
}