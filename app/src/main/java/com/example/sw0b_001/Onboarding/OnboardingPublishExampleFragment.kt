package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import com.example.sw0b_001.Modals.AvailablePlatformsModalFragment
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingPublishExampleFragment :
        OnboardingComponent(R.layout.fragment_onboarding_publish_example) {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
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
        val platformsModalFragment = AvailablePlatformsModalFragment(
            AvailablePlatformsModalFragment.Type.SAVED)
        fragmentTransaction?.add(platformsModalFragment, "store_platforms_tag")
        fragmentTransaction?.show(platformsModalFragment)
        activity?.runOnUiThread { fragmentTransaction?.commit() }
    }
}