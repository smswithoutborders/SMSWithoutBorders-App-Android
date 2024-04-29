package com.example.sw0b_001.Onboarding

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.example.sw0b_001.R
import com.example.sw0b_001.Settings.SettingsFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton

class OnboardingWelcomeFragment :
        OnboardingComponent(R.layout.fragment_onboarding_welcome) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialButton>(R.id.onboarding_welcome_get_started_language_btn)
                .setOnClickListener {
                    populateLanguagePopup(view)
                }
    }

    private fun populateLanguagePopup(view: View) {
        val builder = AlertDialog.Builder(view.context)
//        builder.setTitle(getString(R.string.messages_thread_delete_confirmation_title))
//        builder.setMessage(getString(R.string.messages_thread_delete_confirmation_text))

        val layout = layoutInflater.inflate(R.layout.welcome_language_layout, null)
        builder.setView(layout)
        val radioGroup = layout.findViewById<RadioGroup>(R.id.custom_language_select_popup_layout)

        val values = view.context.resources.getStringArray(R.array.language_values)
        view.context.resources.getStringArray(R.array.language_options).forEachIndexed { index, s ->
            val option = MaterialRadioButton(view.context)
            option.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            option.layoutDirection = View.LAYOUT_DIRECTION_RTL
            option.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            option.gravity = Gravity.START
            option.text = s
            option.id = index
            if(values[index] == SettingsFragment.getCurrentLocale(view.context))
                option.isChecked = true

            option.setOnClickListener {
                SettingsFragment.changeLanguageLocale(it.context, values[index])
            }
            radioGroup.addView(option)
        }

        val dialog = builder.create()
        dialog.show()
    }

}