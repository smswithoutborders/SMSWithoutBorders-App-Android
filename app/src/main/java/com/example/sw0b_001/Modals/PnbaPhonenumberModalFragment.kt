package com.example.sw0b_001.Modals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Publisher
import com.example.sw0b_001.OTPVerificationActivity
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textview.MaterialTextView
import com.hbb20.CountryCodePicker
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PnbaPhonenumberModalFragment(val platforms: AvailablePlatforms,
                                   private val onSuccessCallback: Runnable) :
    BottomSheetDialogFragment(R.layout.fragment_modal_pnba_phonenumber){

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    onSuccessCallback.run()
                    dismiss()
                }
                else -> { }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.pnba_phonenumber_layout)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val countryCodePicker = view.findViewById<CountryCodePicker>(R.id.pnba_country_code_picker)

        val pnbaCountryText = view.findViewById<MaterialTextView>(R.id.pnba_country_code_textview)
        pnbaCountryText.text = countryCodePicker.selectedCountryCodeWithPlus

        countryCodePicker.setOnCountryChangeListener(object: OnCountryChangeListener {
            override fun onCountrySelected() {
                pnbaCountryText.text = countryCodePicker.selectedCountryCodeWithPlus
            }
        })

        view.findViewById<MaterialButton>(R.id.pnba_submit_btn).setOnClickListener {
            isCancelable = false
            it.isEnabled = false

            view.findViewById<LinearProgressIndicator>(R.id.pnba_linear_progress).visibility =
                View.VISIBLE
            view.findViewById<View>(R.id.pnba_error_text).visibility = View.VISIBLE

            CoroutineScope(Dispatchers.Default).launch {
                val phoneNumber = countryCodePicker.selectedCountryCodeWithPlus +
                        view.findViewById<EditText>(R.id.pnba_phonenumber_input).text.toString()

                val publisher = Publisher(requireContext())
                try {
                    val response = publisher.phoneNumberBaseAuthenticationRequest(phoneNumber,
                        platforms.name)

                    if(response.success) {
                        activity?.runOnUiThread {
                            val intent = Intent(requireContext(), OTPVerificationActivity::class.java)
                            intent.putExtra("phone_number", phoneNumber)
                            intent.putExtra("platform", platforms.name)
                            intent.putExtra("type", OTPVerificationActivity.Type.PNBA.type)
                            activityLauncher.launch(intent)
                        }
                    }
                } catch(e: StatusRuntimeException) {
                    e.printStackTrace()
                    view.findViewById<View>(R.id.pnba_error_text).visibility = View.GONE
                } catch(e: Exception) {
                    e.printStackTrace()
                } finally {
                    publisher.shutdown()
                    activity?.runOnUiThread {
                        view.findViewById<LinearProgressIndicator>(R.id.pnba_linear_progress).visibility =
                            View.GONE
                        it.isEnabled = true
                    }
                }
            }
        }
    }
}