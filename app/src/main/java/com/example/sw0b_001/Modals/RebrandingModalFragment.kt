package com.example.sw0b_001.Modals

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import at.favre.lib.armadillo.Armadillo
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class RebrandingModalFragment:
    BottomSheetDialogFragment(R.layout.fragment_modal_sheet_rebranding) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.rebranding_alerts_popups)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<MaterialButton>(R.id.rebranding_alert_read_more).setOnClickListener {
            val intentUri = Uri.parse(resources.getString(R.string.smswithoutborders_official_rebranding_blogpost))
            val intent = Intent(Intent.ACTION_VIEW, intentUri)
            startActivity(intent)
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.rebranding_close).setOnClickListener {
            dismiss()
        }
        saveShownRebranding()
    }

     private val rebrandingModalShown = "rebrandingModalShown"

    private fun saveShownRebranding() {
        val sharedPreferences = Armadillo.create(requireContext(), rebrandingModalShown)
            .encryptionFingerprint(requireContext())
            .build()

        sharedPreferences.edit()
            .putBoolean("shown", true)
            .apply()
    }

    fun shownRebranding(context: Context): Boolean {
        val sharedPreferences = Armadillo.create(context, rebrandingModalShown)
            .encryptionFingerprint(context)
            .build()

        return sharedPreferences.getBoolean("shown", false)
    }
}