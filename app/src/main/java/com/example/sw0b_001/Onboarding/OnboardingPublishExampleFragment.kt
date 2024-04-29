package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.PlatformsModalFragment
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingPublishExampleFragment(context: Context) :
        OnboardingComponent(R.layout.fragment_onboarding_publish_example) {
    init {
        nextButtonText = context.getString(R.string.onboarding_next)
        previousButtonText = context.getString(R.string.onboarding_previous)
        skipButtonText = context.getString(R.string.onboarding_skip)
        skipOnboardingFragment = OnboardingFinishedFragment(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tryExampleButton = view.findViewById<MaterialButton>(
                R.id.onboarding_welcome_vaults_instructions_try_example_btn)

        tryExampleButton?.setOnClickListener {
            showPlatformsModal()
        }
    }

    private fun showPlatformsModal() {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        val platformsModalFragment = PlatformsModalFragment(PlatformsModalFragment.SHOW_TYPE_SAVED)
        fragmentTransaction?.add(platformsModalFragment, "store_platforms_tag")
        fragmentTransaction?.show(platformsModalFragment)
        activity?.runOnUiThread { fragmentTransaction?.commitNow() }
    }
}