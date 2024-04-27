package com.example.sw0b_001.Onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingWelcomeFragment : OnboardingComponent(R.layout.fragment_onboarding_welcome) {
    init {
        nextButtonText = "Next"
        previousButtonText = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}