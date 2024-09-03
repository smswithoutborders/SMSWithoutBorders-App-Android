package com.example.sw0b_001.Modals

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.PlatformsViewModel
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.Models.Publisher
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AvailablePlatformsModalFragment(val type: Type):
    BottomSheetDialogFragment(R.layout.fragment_modal_sheet_store_platforms) {

    enum class Type(val type: String) {
        SAVED("SAVED"),
        AVAILABLE("AVAILABLE"),
        ALL("ALL"),
        REVOKE("REVOKE")
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var savedPlatformsAdapter: PlatformsRecyclerAdapter
    private lateinit var availablePlatformsAdapter: PlatformsRecyclerAdapter

    private lateinit var progress: LinearProgressIndicator

    private lateinit var availableLinearLayoutManager: LinearLayoutManager
    private lateinit var availablePlatformsRecyclerView: RecyclerView

    private lateinit var savedPlatformsRecyclerView: RecyclerView
    private lateinit var savedLinearLayoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.store_platforms_constraint)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        progress = view.findViewById(R.id.store_platforms_loader)

        val viewModel: PlatformsViewModel by viewModels()

        when(type) {
            Type.SAVED, Type.REVOKE -> {
                view.findViewById<View>(R.id.store_platform_unsaved_layout).visibility = View.GONE

                savedPlatformsRecyclerView = view.findViewById(
                    R.id.store_platforms_saved_recycler_view)
                savedLinearLayoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false)
                savedPlatformsRecyclerView.layoutManager = savedLinearLayoutManager
                savedPlatformsAdapter = PlatformsRecyclerAdapter(type)
                savedPlatformsRecyclerView.adapter = savedPlatformsAdapter

                CoroutineScope(Dispatchers.Default).launch {
                    savedPlatformsAdapter.availablePlatforms = Datastore.getDatastore(requireContext())
                        .availablePlatformsDao().fetchAllList()

                    activity?.runOnUiThread {
                        viewModel.getSaved(requireContext()).observe(viewLifecycleOwner, Observer {
                            savedPlatformsAdapter.storedMDiffer.submitList(it)
                            if(it.isNullOrEmpty())
                                view.findViewById<View>(R.id.store_platforms_saved_empty)
                                    .visibility = View.VISIBLE
                            else
                                view.findViewById<View>(R.id.store_platforms_saved_empty)
                                    .visibility = View.INVISIBLE
                        })
                    }
                }
                configureStoredClickListener(type)
            }
            Type.AVAILABLE -> {
                progress.visibility = View.VISIBLE
                view.findViewById<View>(R.id.store_platform_saved_layout).visibility = View.GONE

                availablePlatformsRecyclerView = view.findViewById(
                    R.id.store_platforms_unsaved_recycler_view)
                availableLinearLayoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false)
                availablePlatformsRecyclerView.layoutManager = availableLinearLayoutManager
                availablePlatformsAdapter = PlatformsRecyclerAdapter(type)
                availablePlatformsRecyclerView.adapter = availablePlatformsAdapter

                configureAvailableClickListener()

                viewModel.getAvailablePlatforms(requireContext()).observe(this, Observer { it ->
                    availablePlatformsAdapter.availableMDiffer.submitList(it)
                    availablePlatformsAdapter.isClickable = false

                    if(it.isNullOrEmpty())
                        view.findViewById<View>(R.id.store_platforms_unsaved_empty)
                            .visibility = View.VISIBLE
                    else view.findViewById<View>(R.id.store_platforms_unsaved_empty)
                        .visibility = View.INVISIBLE

                    progress.visibility = View.GONE
                    availablePlatformsAdapter.isClickable = true
                })
            }
            Type.ALL -> TODO()
        }

    }

    private fun configureStoredClickListener(type: Type) {
        savedPlatformsAdapter.savedPlatformsMutableData.observe(viewLifecycleOwner) {
            dismiss()
            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            val accountsModalFragment = AccountsModalFragment(it.name!!, type)
            fragmentTransaction?.add(accountsModalFragment, "accounts_fragment")
            fragmentTransaction?.show(accountsModalFragment)
            fragmentTransaction?.commit()
        }
    }

    private fun configureAvailableClickListener() {
        availablePlatformsAdapter.availablePlatformsMutableLiveData.observe(viewLifecycleOwner) {
            isCancelable = false

            val scope = CoroutineScope(Dispatchers.Default)
            scope.launch {
                val publisher = Publisher(requireContext())
                activity?.runOnUiThread {
                    progress.visibility = View.VISIBLE
                }

                try {
                    val response = publisher.getOAuthURL(it, true,
                        it.support_url_scheme!!)

                    Publisher.storeOauthRequestCodeVerifier(requireContext(),
                        response.codeVerifier)

                    publisher.shutdown()

                    activity?.runOnUiThread {
                        val intentUri = Uri.parse(response.authorizationUrl)
                        val intent = Intent(Intent.ACTION_VIEW, intentUri)
                        startActivity(intent)
                    }
                    dismiss()
                } catch(e: StatusRuntimeException) {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), e.status.description,
                            Toast.LENGTH_SHORT).show()
                    }
                } catch(e: Exception) {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    activity?.runOnUiThread {
                        progress.visibility = View.GONE
                    }
                    availablePlatformsAdapter.isClickable = true
                    isCancelable = true
                }
            }
        }
    }
}