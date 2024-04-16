package com.example.sw0b_001.Onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingVaultFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_vault, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tryExampleButton = view
                .findViewById<MaterialButton>(R.id.onboarding_welcome_vaults_description_try_example_btn)

        tryExampleButton.setOnClickListener {
            val onboardingLoginSignupVaultModalFragment = OnboardingLoginSignupVaultModalFragment()

            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.add(onboardingLoginSignupVaultModalFragment, "login_signup_vault_tag")
            fragmentTransaction?.show(onboardingLoginSignupVaultModalFragment)
            fragmentTransaction?.commitNow()
        }
    }
}