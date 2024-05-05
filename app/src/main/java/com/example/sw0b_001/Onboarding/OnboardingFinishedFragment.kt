package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import com.example.sw0b_001.BuildConfig
import com.example.sw0b_001.R

class OnboardingFinishedFragment : OnboardingComponent(R.layout.fragment_onboarding_skip_all) {

    override fun getButtonText(context: Context) {
        nextButtonText = context.getString(R.string.onboarding_finish)
        previousButtonText = context.getString(R.string.onboarding_previous)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        saveStateDone(requireContext())
    }
}