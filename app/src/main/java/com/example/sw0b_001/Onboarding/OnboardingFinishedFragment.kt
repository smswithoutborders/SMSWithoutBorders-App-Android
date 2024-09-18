package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import com.example.sw0b_001.BuildConfig
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingFinishedFragment : OnboardingComponent(R.layout.fragment_onboarding_skip_all) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        saveStateDone(requireContext())
    }
}