package com.example.sw0b_001.Onboarding

import android.content.Context
import androidx.fragment.app.Fragment
import at.favre.lib.armadillo.Armadillo
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.google.android.material.button.MaterialButton

open class OnboardingComponent(val layout: Int)
    : Fragment(layout) {
    var nextButtonText: String = ""
    var previousButtonText: String = ""
    var skipButtonText: String = ""
    var skipOnboardingFragment: OnboardingComponent? = null

    public interface ManageComponentsListing {
        fun removeComponent(index: Int)
        fun removeComponent(component: OnboardingComponent)
        fun addComponent(component: OnboardingComponent)

        fun getFragmentIndex(): Int
    }
    open fun getButtonText(context: Context) {}


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