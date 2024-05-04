package com.example.sw0b_001.Onboarding

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.transition.TransitionInflater
import com.example.sw0b_001.R
import com.example.sw0b_001.Settings.SettingsFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton


class OnboardingWelcomeFragment :
        OnboardingComponent(R.layout.fragment_onboarding_welcome) {

    private lateinit var languageValues: Array<String>
    private lateinit var languageOptions: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }
    override fun getButtonText(context: Context) {
        nextButtonText = context.getString(R.string.onboarding_next)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        languageValues = view.context.resources.getStringArray(R.array.language_values)
        languageOptions = view.context.resources.getStringArray(R.array.language_options)

        val changeLanguageBtn = view.findViewById<MaterialButton>(R.id
                .onboarding_welcome_get_started_language_btn)
        var index = 0
        languageValues.forEachIndexed { _index, s ->
            if(SettingsFragment.getCurrentLocale(view.context) == s) {
                index = _index
            }

        }
        changeLanguageBtn.text = languageOptions[index]
        changeLanguageBtn.setOnClickListener { populateLanguagePopup(view) }
        view.findViewById<View>(R.id.onboarding_welcome_privacy_policy)
                .setOnClickListener { linkPrivacyPolicy(it) }
    }

    private fun linkPrivacyPolicy(view: View?) {
        // TODO: check for production
        val intentUri = Uri.parse(resources.getString(R.string.privacy_policy))
        val intent = Intent(Intent.ACTION_VIEW, intentUri)
        startActivity(intent)
    }

    private fun populateLanguagePopup(view: View) {
        val builder = AlertDialog.Builder(view.context)
        val layout = layoutInflater.inflate(R.layout.layout_welcome_language, null)
        builder.setView(layout)
        val radioGroup = layout.findViewById<RadioGroup>(R.id.custom_language_select_popup_layout)

        languageOptions.forEachIndexed { index, s ->
            val option = MaterialRadioButton(view.context)
            option.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            option.layoutDirection = View.LAYOUT_DIRECTION_RTL
            option.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            option.gravity = Gravity.START
            option.text = s
            option.id = index
            if(languageValues[index] == SettingsFragment.getCurrentLocale(view.context))
                option.isChecked = true

            option.setOnClickListener {
                SettingsFragment.changeLanguageLocale(it.context, languageValues[index])
            }
            radioGroup.addView(option)
        }

        val dialog = builder.create()
        dialog.show()
    }

}