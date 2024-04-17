package com.example.sw0b_001.Onboarding

import androidx.fragment.app.Fragment

open class OnboardingComponent : Fragment() {
    var nextButtonText: String = ""
    var previousButtonText: String = ""
    var skipButtonText: String = ""
    var skipOnboardingFragment: OnboardingComponent? = null
}