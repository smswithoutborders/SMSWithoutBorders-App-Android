package com.example.sw0b_001.Onboarding

import android.os.Bundle
import android.view.View
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.PlatformsModalFragment
import com.example.sw0b_001.R
import com.google.android.material.button.MaterialButton

class OnboardingVaultStorePlatformFragment : OnboardingComponent(R.layout.fragment_onboarding_vault_store){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.onboarding_welcome_vaults_store_description_try_example_btn)
                .setOnClickListener {
                    showPlatformsModal()
                }

        ThreadExecutorPool.executorService.execute {
            if(Datastore.getDatastore(view.context).platformDao().countSaved() > 0) {
                activity?.runOnUiThread {
                    activity?.findViewById<MaterialButton>(R.id.onboard_next_button)
                            ?.performClick()
                    if(activity is OnboardingComponent.ManageComponentsListing)
                        (activity as OnboardingComponent.ManageComponentsListing).removeComponent(this)
                }
            }
        }
    }


    private fun showPlatformsModal() {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        val platformsModalFragment = PlatformsModalFragment(PlatformsModalFragment.SHOW_TYPE_UNSAVED)
        fragmentTransaction?.add(platformsModalFragment, "store_platforms_tag")
        fragmentTransaction?.show(platformsModalFragment)
        activity?.runOnUiThread { fragmentTransaction?.commitNow() }
    }
}