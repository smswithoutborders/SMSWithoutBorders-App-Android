package com.example.sw0b_001.Onboarding

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.LoadingFragment
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.PlatformsModalFragment
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton
import kotlin.math.log

class OnboardingVaultStorePlatformFragment : OnboardingComponent(R.layout.fragment_onboarding_vault_store){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var fetchPlatforms = false
    var showPlatforms = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.onboarding_welcome_vaults_store_description_try_example_btn)
                .setOnClickListener {
                    if(fetchPlatforms)
                        loginAndFetchPlatforms(view)
                    else
                        showPlatformsModal(view)
                }

        ThreadExecutorPool.executorService.execute {
            checkCanNext(view)
        }
    }

    private fun checkCanNext(view: View) {
        if(Datastore.getDatastore(view.context).platformDao().countSaved() > 0) {
            activity?.runOnUiThread {
                activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                        ?.performClick()
                if(activity is OnboardingComponent.ManageComponentsListing)
                    (activity as OnboardingComponent.ManageComponentsListing)
                            .removeComponent(this)
            }
        } else {
            fetchPlatforms = true
        }
    }

    private fun loginAndFetchPlatforms(view: View) {
        val loadingFragment = LoadingFragment()
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.add(loadingFragment, "loading_fragment_tag")
        fragmentTransaction?.show(loadingFragment)
        fragmentTransaction?.commit()

        val credentials = UserArtifactsHandler.fetchCredentials(view.context)
        val phoneNumber = credentials[UserArtifactsHandler.PHONE_NUMBER]!!
        val password = credentials[UserArtifactsHandler.PASSWORD]!!
        val  uid = credentials[UserArtifactsHandler.USER_ID_KEY]!!
        ThreadExecutorPool.executorService.execute {
            try {
                Vault_V2.loginSyncPlatformsFlow(requireContext(), phoneNumber, password,
                        "", uid)

                fetchPlatforms = false
                checkCanNext(view)
                if(fetchPlatforms)
                    showPlatformsModal(view)
                loadingFragment.dismiss()
            } catch(e: Exception) {
                e.printStackTrace()
                when(e.message) {
                    Vault_V2.INVALID_CREDENTIALS_EXCEPTION -> {
                        TODO("Invalidate and delete all creds")
                    }
                    Vault_V2.SERVER_ERROR_EXCEPTION -> {
                    }
                    else -> {
                    }
                }
            }
        }
    }


    private fun showPlatformsModal(view: View) {
        if(Datastore.getDatastore(view.context).platformDao().count() > 0) {
            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            val platformsModalFragment = PlatformsModalFragment(PlatformsModalFragment.SHOW_TYPE_UNSAVED)
            fragmentTransaction?.add(platformsModalFragment, "store_platforms_tag")
            fragmentTransaction?.show(platformsModalFragment)
            activity?.runOnUiThread { fragmentTransaction?.commit() }
        }
        else {
            loginAndFetchPlatforms(view)
        }
    }
}