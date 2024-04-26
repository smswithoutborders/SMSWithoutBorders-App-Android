package com.example.sw0b_001.Onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.PlatformsModalFragment
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class OnboardingPublishExampleFragment : OnboardingComponent() {
    init {
        nextButtonText = "Next"
        previousButtonText = "Previous"
        skipButtonText = "skip"
        skipOnboardingFragment = OnboardingSkippedAllFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_publish_example, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tryExampleButton = view.findViewById<MaterialButton>(
                R.id.onboarding_welcome_vaults_instructions_try_example_btn)

        tryExampleButton?.setOnClickListener {
            showPlatformsModal()
        }

        if(!UserArtifactsHandler.isCredentials(view.context)) {
            activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                    ?.performClick()
            if(activity is OnboardingComponent.ManageComponentsListing)
                (activity as ManageComponentsListing).removeComponent(this)
        }
    }

    private fun showPlatformsModal() {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        val platformsModalFragment = PlatformsModalFragment()
        fragmentTransaction?.add(platformsModalFragment, "store_platforms_tag")
        fragmentTransaction?.show(platformsModalFragment)
        activity?.runOnUiThread { fragmentTransaction?.commitNow() }
    }
}