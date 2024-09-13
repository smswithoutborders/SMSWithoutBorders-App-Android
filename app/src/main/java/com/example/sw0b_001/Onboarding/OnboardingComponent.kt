package com.example.sw0b_001.Onboarding

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import at.favre.lib.armadillo.Armadillo
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

open class OnboardingComponent(val layout: Int)
    : Fragment(layout) {
    public interface ManageComponentsListing {
        fun removeComponent(index: Int)
        fun removeComponent(component: OnboardingComponent)
        fun addComponent(component: OnboardingComponent)

        fun getFragmentIndex(): Int
    }

    companion object {
        private const val PREF_USER_ONBOARDED = "PREF_USER_ONBOARDED"
        fun getOnboarded(context: Context): Boolean{
            val sharedPreferences = Armadillo.create(context, PREF_USER_ONBOARDED)
                    .encryptionFingerprint(context)
                    .build()
            return sharedPreferences.getBoolean("onboarded", false)
        }

        fun saveStateDone(context: Context) {
            val sharedPreferences = Armadillo.create(context, PREF_USER_ONBOARDED)
                    .encryptionFingerprint(context)
                    .build()

            sharedPreferences.edit()
                    .putBoolean("onboarded", true)
                    .apply()
        }
    }
}