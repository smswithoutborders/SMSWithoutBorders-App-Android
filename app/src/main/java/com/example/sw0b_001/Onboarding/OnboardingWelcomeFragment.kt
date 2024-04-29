package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingWelcomeFragment(context: Context) : OnboardingComponent(R.layout.fragment_onboarding_welcome) {

    init {
        nextButtonText = context.getString(R.string.onboarding_next)
        previousButtonText = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}