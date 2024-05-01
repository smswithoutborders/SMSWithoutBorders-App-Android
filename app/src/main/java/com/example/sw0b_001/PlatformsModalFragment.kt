package com.example.sw0b_001

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class PlatformsModalFragment(val showType: Int = SHOW_TYPE_ALL,
                             val networkResponseResults: Network.NetworkResponseResults? = null)
    : BottomSheetDialogFragment(R.layout.fragment_modal_sheet_store_platforms) {

    companion object {
        public const val SHOW_TYPE_SAVED = 0
        public const val SHOW_TYPE_UNSAVED = 1
        public const val SHOW_TYPE_ALL = 2
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
            SHOW_TYPE_SAVED -> unsavedLinearLayout.visibility = View.GONE
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

        savedPlatformsAdapter.savedOnClickListenerLiveData.observe(this, Observer {
            if(it != null) {
                savedPlatformsAdapter.savedOnClickListenerLiveData = MutableLiveData();
                when(it) {
                    Platforms.TYPE_EMAIL -> {
                        // TODO: remove as sloopy, very sloopy
                        ThreadExecutorPool.executorService.execute {
                            val platform = Datastore.getDatastore(view.context)
                                    .platformDao().getType("email");
                            val emailComposeModalFragment = EmailComposeModalFragment(platform)
                            fragmentTransaction?.add(emailComposeModalFragment, "compose_fragment_email")
                            fragmentTransaction?.show(emailComposeModalFragment)
                            activity?.runOnUiThread { fragmentTransaction?.commitNow() }
                        }
                    }
                }
                dismiss()
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

    private fun storePlatform(platformType: Int) {
        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        var fragment = Fragment()

        var platform = ""
        when(platformType) {
            Platforms.TYPE_EMAIL -> {
                platform = "gmail"
            }
            Platforms.TYPE_TEXT -> {
                platform = "twitter"
            }
        }
        fragment = VaultStorePlatformProcessingFragment(platform, networkResponseResults!!)
//        fragmentTransaction?.add(fragment, "email_compose_platform_type")
//        fragmentTransaction?.show(fragment)
        activity?.runOnUiThread {
            fragmentTransaction?.replace(R.id.onboarding_fragment_container, fragment)
            fragmentTransaction?.commitNow()
        }
    }
}