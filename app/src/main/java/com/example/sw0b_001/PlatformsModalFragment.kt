package com.example.sw0b_001

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.PlatformComposers.EmailComposeModalFragment
import com.example.sw0b_001.Models.PlatformComposers.TextComposeModalFragment
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.PlatformsViewModel
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.GatewayServer_V2
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.Onboarding.OnboardingComponent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class PlatformsModalFragment(private val showType: Int = SHOW_TYPE_ALL)
    : BottomSheetDialogFragment(R.layout.fragment_modal_sheet_store_platforms) {

    companion object {
        public const val SHOW_TYPE_SAVED = 0
        public const val SHOW_TYPE_UNSAVED = 1
        public const val SHOW_TYPE_ALL = 2
        public const val SHOW_TYPE_SAVED_REVOKE = 3
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var savedPlatformsAdapter: PlatformsRecyclerAdapter
    private lateinit var unSavedPlatformsAdapter: PlatformsRecyclerAdapter

    private lateinit var progress: LinearProgressIndicator

    private lateinit var savedLinearLayout: LinearLayout
    private lateinit var unsavedLinearLayout: LinearLayout

    private lateinit var unsavedPlatformsRecyclerView: RecyclerView
    private lateinit var savedPlatformsRecyclerView: RecyclerView

    private lateinit var savedLinearLayoutManager: LinearLayoutManager
    private lateinit var unsavedLinearLayoutManager: LinearLayoutManager

    private lateinit var networkResponseResults: Network.NetworkResponseResults
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.store_platforms_constraint)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        progress = view.findViewById(R.id.store_platforms_loader)
        unsavedPlatformsRecyclerView = view.findViewById(R.id.store_platforms_unsaved_recycler_view)
        savedPlatformsRecyclerView = view.findViewById(R.id.store_platforms_saved_recycler_view)
        savedLinearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)
        unsavedLinearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)
        unsavedPlatformsRecyclerView.layoutManager = unsavedLinearLayoutManager
        savedPlatformsRecyclerView.layoutManager = savedLinearLayoutManager

        savedLinearLayout = view.findViewById(R.id.store_platform_saved_layout)
        unsavedLinearLayout = view.findViewById(R.id.store_platform_unsaved_layout)

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        configureView()
        configureRecyclerView(view, fragmentTransaction)
        configureClickListeners(view, fragmentTransaction)
    }

    private fun configureView() {
        when(showType) {
            SHOW_TYPE_SAVED, SHOW_TYPE_SAVED_REVOKE -> {
                unsavedLinearLayout.visibility = View.GONE
            }
            SHOW_TYPE_UNSAVED -> {
                savedLinearLayout.visibility = View.GONE
            }
        }
    }

    private fun configureRecyclerView(view: View, fragmentTransaction: FragmentTransaction?) {
        savedPlatformsAdapter = PlatformsRecyclerAdapter(fragmentTransaction)
        unSavedPlatformsAdapter = PlatformsRecyclerAdapter(fragmentTransaction)

        savedPlatformsRecyclerView.adapter = savedPlatformsAdapter
        unsavedPlatformsRecyclerView.adapter = unSavedPlatformsAdapter

        val viewModel: PlatformsViewModel by viewModels()
        when(showType) {
            SHOW_TYPE_SAVED, SHOW_TYPE_SAVED_REVOKE -> {
                context?.let { it ->
                    viewModel.getSaved(it).observe(this, Observer {
                        savedPlatformsAdapter.mDiffer.submitList(it)
                        if(it.isNullOrEmpty())
                            view.findViewById<View>(R.id.store_platforms_saved_empty)
                                    .visibility = View.VISIBLE
                        else
                            view.findViewById<View>(R.id.store_platforms_saved_empty)
                                    .visibility = View.INVISIBLE
                    })
                }
            } else -> {
                context?.let { it ->
                    val runnable = Runnable {
                        activity?.runOnUiThread {
                            progress.visibility = View.GONE
                            unsavedLinearLayout.visibility = View.VISIBLE
                        }
                    }

                    unsavedLinearLayout.visibility = View.INVISIBLE
                    progress.visibility = View.VISIBLE

                    val credentials = UserArtifactsHandler.fetchCredentials(view.context)

                    viewModel.getUnsaved(it,
                            credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                            credentials[UserArtifactsHandler.PASSWORD]!!, runnable)
                            .observe(this, Observer { it1 ->
                                viewModel.networkResponseResults?.let {
                                    this.networkResponseResults = it
                                }

                        unSavedPlatformsAdapter.mDiffer.submitList(it1)
                        if(it1.isNullOrEmpty())
                            view.findViewById<View>(R.id.store_platforms_unsaved_empty)
                                    .visibility = View.VISIBLE
                        else view.findViewById<View>(R.id.store_platforms_unsaved_empty)
                                .visibility = View.INVISIBLE
                    })
                }
            }
        }
    }

    private fun configureClickListeners(view: View, fragmentTransaction: FragmentTransaction?) {
        savedPlatformsAdapter.savedOnClickListenerLiveData.observe(this, Observer {
            if(it != null) {
                progress.visibility = View.VISIBLE
                savedPlatformsAdapter.savedOnClickListenerLiveData = MutableLiveData();
                when(it.type) {
                    "email", "text" -> {
                        if(showType == SHOW_TYPE_SAVED_REVOKE) {
                            savedLinearLayout.visibility = View.INVISIBLE
                            val credentials = UserArtifactsHandler.fetchCredentials(view.context)
                            ThreadExecutorPool.executorService.execute {
                                val networkResponseResults = Vault_V2.revoke(requireContext(),
                                        credentials[UserArtifactsHandler.USER_ID_KEY]!!,
                                        credentials[UserArtifactsHandler.PASSWORD]!!,
                                        it.name,
                                        "oauth2")
                                when(networkResponseResults.response.statusCode) {
                                    200 -> {
//                                        val credentials = UserArtifactsHandler
//                                                .fetchCredentials(view.context)
//                                        val responsePayload =
//                                                GatewayServer_V2.sync(view.context,
//                                                        credentials[UserArtifactsHandler.USER_ID_KEY]!!,
//                                                        credentials[UserArtifactsHandler.PASSWORD]!!)
//                                        UserArtifactsHandler.storeSharedKey(view.context,
//                                                responsePayload.shared_key)
                                        activity?.runOnUiThread {
                                            Toast.makeText(view.context,
                                                    getString(R.string.platforms_re_synced_gateway_server),
                                                    Toast.LENGTH_SHORT)
                                                    .show()
                                        }
                                        Datastore.getDatastore(requireContext()).platformDao()
                                                .delete(it)
                                        activity?.runOnUiThread {
                                            Toast.makeText(view.context, it.name +
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
                        }
                        else if(it.type == "email"){
                            val emailComposeModalFragment = EmailComposeModalFragment(it)
                            fragmentTransaction?.add(emailComposeModalFragment,
                                    "email_compose_fragment")
                            fragmentTransaction?.show(emailComposeModalFragment)
                            activity?.runOnUiThread { fragmentTransaction?.commitNow() }
                            dismiss()
                        }
                        else if(it.type == "text") {
                            val textComposeModalFragment = TextComposeModalFragment(it)
                            fragmentTransaction?.add(textComposeModalFragment,
                                    "text_compose_fragment")
                            fragmentTransaction?.show(textComposeModalFragment)
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
                storePlatform(it)
                dismiss()
            }
        })


    }


    private fun storePlatform(platforms: Platforms) {
        val intent = Intent(requireContext(), VaultStoreActivity::class.java)
        intent.putExtra("platform_name", platforms.name)
        intent.putExtra("callback_activity", activity?.localClassName)
        requireContext().startActivity(intent)
    }
}