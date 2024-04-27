package com.example.sw0b_001.Onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OnboardingSkippedAllFragment : OnboardingComponent(R.layout.fragment_onboarding_skip_all) {

    init {
        nextButtonText = "Finish"
        previousButtonText = "Previous"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}