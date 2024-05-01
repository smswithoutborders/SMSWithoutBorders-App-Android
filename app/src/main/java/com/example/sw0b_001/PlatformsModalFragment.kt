package com.example.sw0b_001

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.PlatformComposers.EmailComposeModalFragment
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.PlatformsViewModel
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class PlatformsModalFragment(val showType: Int = SHOW_TYPE_ALL,
                             val networkResponseResults: Network.NetworkResponseResults? = null)
    : BottomSheetDialogFragment(R.layout.fragment_modal_sheet_store_platforms) {

    companion object {
        public const val SHOW_TYPE_SAVED = 0
        public const val SHOW_TYPE_UNSAVED = 1
        public const val SHOW_TYPE_ALL = 2
        public const val SHOW_TYPE_SAVED_REVOKE = 3
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.store_platforms_constraint)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        configureRecyclerView(view)
    }

    lateinit var savedPlatformsAdapter: PlatformsRecyclerAdapter
    lateinit var unSavedPlatformsAdapter: PlatformsRecyclerAdapter
    private fun configureRecyclerView(view: View) {
        val unsavedPlatformsRecyclerView =
                view.findViewById<RecyclerView>(R.id.store_platforms_unsaved_recycler_view)

        val savedPlatformsRecyclerView =
                view.findViewById<RecyclerView>(R.id.store_platforms_saved_recycler_view)

        val savedLinearLayout = view.findViewById<LinearLayout>(R.id.store_platform_saved_layout)
        val unsavedLinearLayout = view.findViewById<LinearLayout>(R.id.store_platform_unsaved_layout)

        when(showType) {
            SHOW_TYPE_SAVED, SHOW_TYPE_SAVED_REVOKE -> unsavedLinearLayout.visibility = View.GONE
            SHOW_TYPE_UNSAVED -> savedLinearLayout.visibility = View.GONE
        }

        val savedLinearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)

        val unsavedLinearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)

        unsavedPlatformsRecyclerView.layoutManager = unsavedLinearLayoutManager
        savedPlatformsRecyclerView.layoutManager = savedLinearLayoutManager

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()

        savedPlatformsAdapter = PlatformsRecyclerAdapter(fragmentTransaction)
        unSavedPlatformsAdapter = PlatformsRecyclerAdapter(fragmentTransaction)

        savedPlatformsRecyclerView.adapter = savedPlatformsAdapter
        unsavedPlatformsRecyclerView.adapter = unSavedPlatformsAdapter

        val progress = view.findViewById<LinearProgressIndicator>(R.id.store_platforms_loader)
        savedPlatformsAdapter.savedOnClickListenerLiveData.observe(this, Observer {
            if(it != null) {
                progress.visibility = View.VISIBLE
                savedPlatformsAdapter.savedOnClickListenerLiveData = MutableLiveData();
                when(it.type) {
                    "email", "text" -> {
                        if(showType == SHOW_TYPE_SAVED_REVOKE) {
                            savedLinearLayout.visibility = View.INVISIBLE
                            val credentials = UserArtifactsHandler.fetchCredentials(requireContext())
                            ThreadExecutorPool.executorService.execute {
                                val networkResponseResults = Vault_V2.revoke(requireContext(),
                                        credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                                        credentials[UserArtifactsHandler.PASSWORD]!!,
                                        it.name,
                                        "oauth2")
                                when(networkResponseResults.response.statusCode) {
                                    200 -> {
                                        Datastore.getDatastore(requireContext()).platformDao()
                                                .delete(it)
                                        activity?.runOnUiThread {
                                            Toast.makeText(requireContext(), it.name +
                                                    getString(R.string.platforms_revoked_successfully),
                                                    Toast.LENGTH_SHORT).show()
                                            progress.visibility = View.GONE
                                        }
                                    } else -> {
                                        activity?.runOnUiThread {
                                            Toast.makeText(requireContext(),
                                                    String(networkResponseResults.response.data),
                                                    Toast.LENGTH_SHORT)
                                                    .show()
                                            progress.visibility = View.GONE
                                        }
                                    }
                                }
                                dismiss()
                            }
                        } else {
                            val emailComposeModalFragment = EmailComposeModalFragment(it)
                            fragmentTransaction?.add(emailComposeModalFragment, "compose_fragment_email")
                            fragmentTransaction?.show(emailComposeModalFragment)
                            activity?.runOnUiThread { fragmentTransaction?.commitNow() }
                            dismiss()
                        }
                    }
                }

            }
        })

        unSavedPlatformsAdapter.unSavedOnClickListenerLiveData.observe(this, Observer {
            if(it != null) {
                unSavedPlatformsAdapter.unSavedOnClickListenerLiveData = MutableLiveData();
                if(networkResponseResults != null) {
                    storePlatform(it)
                    dismiss()
                }
            }
        })

        val viewModel: PlatformsViewModel by viewModels()
        context?.let { it ->
            viewModel.getSeparated(it).observe(this, Observer {
                savedPlatformsAdapter.mDiffer.submitList(it.first)
                unSavedPlatformsAdapter.mDiffer.submitList(it.second)
                if(it.first.isNullOrEmpty())
                    view.findViewById<View>(R.id.store_platforms_saved_empty)
                            .visibility = View.VISIBLE
                else
                    view.findViewById<View>(R.id.store_platforms_saved_empty)
                            .visibility = View.GONE
            })
        }
    }

    private fun storePlatform(platforms: Platforms) {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        var fragment = Fragment()

        fragment = VaultStorePlatformProcessingFragment(platforms.name, networkResponseResults!!)
        activity?.runOnUiThread {
            fragmentTransaction?.replace(R.id.onboarding_fragment_container, fragment)
            fragmentTransaction?.commitNow()
        }
    }
}