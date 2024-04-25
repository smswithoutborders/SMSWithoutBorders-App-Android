package com.example.sw0b_001

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityAES
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityRSA
import com.example.sw0b_001.Data.Platforms.Platforms
import com.example.sw0b_001.Data.Platforms.PlatformsViewModel
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Data.UserArtifactsHandler
import com.example.sw0b_001.Data.v2.Vault_V2
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.github.kittinunf.fuel.core.Headers
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import kotlinx.serialization.json.Json

class LoginModalFragment : BottomSheetDialogFragment() {

    public lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.login_btn).setOnClickListener {
            login(view)
        }

        val customUrlView = view.findViewById<TextInputLayout>(R.id.login_url)
        view.findViewById<MaterialTextView>(R.id.login_advanced_toggle).setOnClickListener {
            if(customUrlView.visibility == View.VISIBLE)
                customUrlView.visibility = View.INVISIBLE
            else
                customUrlView.visibility = View.VISIBLE
        }

        val bottomSheet = view.findViewById<View>(R.id.login_constraint)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun login(view: View) {
        val phonenumber = view.findViewById<TextInputEditText>(R.id.login_phonenumber_text_input)
                .text.toString()

        val password = view.findViewById<TextInputEditText>(R.id.login_password_text_input)
                .text.toString()

        val url = view.context.getString(R.string.smswithoutborders_official_site_login)
        ThreadExecutorPool.executorService.execute {
            activity?.runOnUiThread {
                view.findViewById<LinearProgressIndicator>(R.id.login_progress_bar)
                        .visibility = View.VISIBLE
            }

            try {
                val networkResponseResults = Vault_V2.login(phonenumber, password, url)
                val uid = Json.decodeFromString<Vault_V2.UID>(networkResponseResults.result.get()).uid

                UserArtifactsHandler.storeCredentials(requireContext(), phonenumber, password, uid)

                val platformsUrl = requireContext()
                        .getString(R.string.smswithoutborders_official_vault)

                storePlatforms(uid, platformsUrl, networkResponseResults.response.headers)

                onSuccessCallback(view)
                dismiss()
            } catch(e: Exception) {
                e.printStackTrace()
                Log.e(javaClass.name, "Exception login", e)
                when(e.message) {
                    Vault_V2.INVALID_CREDENTIALS_EXCEPTION -> {
                        TODO("Implement inform user invalid credentials")
                    }
                    Vault_V2.SERVER_ERROR_EXCEPTION -> {
                        TODO("Implement inform user server error")
                    }
                }
            } finally {
                activity?.runOnUiThread {
                    view.findViewById<LinearProgressIndicator>(R.id.login_progress_bar)
                            .visibility = View.GONE
                }
            }
        }
    }

    private fun onSuccessCallback(view: View) {
        if(UserArtifactsHandler.isCredentials(view.context)) {
            activity?.runOnUiThread {
                activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                        ?.performClick()
            }
        }
    }

    private fun storePlatforms(uid: String, url: String, headers: Headers) {
        val platforms = Vault_V2.getPlatforms(url, headers, uid)

        val platformsViewModel = ViewModelProvider(this)[PlatformsViewModel::class.java]
        platformsViewModel.deleteAll(requireContext())

        val listPlatforms = ArrayList<Platforms>()
        platforms.saved_platforms.forEach {
            val platform = Platforms()
            platform.name = it.name
            platform.description = ""
            platform.type = it.type
            platform.letter = it.letter
            platform.isSaved = true
//            platform.logo = PlatformsHandler
//                    .hardGetLogoByName(applicationContext, it.name)
//            platformsViewModel.store(requireContext(), platform)
            listPlatforms.add(platform)
        }

        platforms.unsaved_platforms.forEach {
            val platform = Platforms()
            platform.name = it.name
            platform.description = ""
            platform.type = it.type
            platform.letter = it.letter
//            platform.logo = PlatformsHandler
//                    .hardGetLogoByName(applicationContext, it.name)
//            platformsViewModel.store(requireContext(), platform)
            listPlatforms.add(platform)
        }
        platformsViewModel.storeAll(requireContext(), listPlatforms)
        Log.d(javaClass.name, "Platforms stored")
    }

}