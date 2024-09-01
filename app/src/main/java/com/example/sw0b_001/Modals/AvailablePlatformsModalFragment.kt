package com.example.sw0b_001.Modals

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.PlatformsViewModel
import com.example.sw0b_001.Models.Publisher
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.LinearProgressIndicator
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

    private lateinit var availableLinearLayout: LinearLayout
    private lateinit var availableLinearLayoutManager: LinearLayoutManager
    private lateinit var availablePlatformsRecyclerView: RecyclerView

    private lateinit var savedLinearLayout: LinearLayout
    private lateinit var savedPlatformsRecyclerView: RecyclerView
    private lateinit var savedLinearLayoutManager: LinearLayoutManager

    private lateinit var publisher: Publisher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publisher = Publisher(requireContext())

        val bottomSheet = view.findViewById<View>(R.id.store_platforms_constraint)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        progress = view.findViewById(R.id.store_platforms_loader)

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        val viewModel: PlatformsViewModel by viewModels()

        when(type) {
            Type.SAVED, Type.REVOKE -> {
                savedPlatformsRecyclerView = view.findViewById(
                    R.id.store_platforms_saved_recycler_view)
                savedLinearLayoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false)
                savedPlatformsRecyclerView.layoutManager = savedLinearLayoutManager
                savedLinearLayout = view.findViewById(R.id.store_platform_saved_layout)
                savedPlatformsAdapter = PlatformsRecyclerAdapter(fragmentTransaction)
                savedPlatformsRecyclerView.adapter = savedPlatformsAdapter

//                viewModel.getSaved(requireContext()).observe(this, Observer {
//                    savedPlatformsAdapter.mDiffer.submitList(it)
//                    if(it.isNullOrEmpty())
//                        view.findViewById<View>(R.id.store_platforms_saved_empty)
//                            .visibility = View.VISIBLE
//                    else
//                        view.findViewById<View>(R.id.store_platforms_saved_empty)
//                            .visibility = View.INVISIBLE
//                })
            }
            Type.AVAILABLE -> {
                progress.visibility = View.VISIBLE
                view.findViewById<View>(R.id.store_platform_saved_layout).visibility = View.GONE

                availablePlatformsRecyclerView = view.findViewById(
                    R.id.store_platforms_unsaved_recycler_view)
                availableLinearLayoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false)
                availablePlatformsRecyclerView.layoutManager = availableLinearLayoutManager
                availableLinearLayout = view.findViewById(R.id.store_platform_unsaved_layout)
                availablePlatformsAdapter = PlatformsRecyclerAdapter(fragmentTransaction)
                availablePlatformsRecyclerView.adapter = availablePlatformsAdapter

                availablePlatformsAdapter.availablePlatformsMutableLiveData.observeForever {
                    val scope = CoroutineScope(Dispatchers.Default)
                    scope.launch {
                        activity?.runOnUiThread {
                            progress.visibility = View.VISIBLE
                        }

                        val response = publisher.getOAuthURL(it, true,
                            it.support_url_scheme!!)

                        activity?.runOnUiThread {
                            val intentUri = Uri.parse(response.authorizationUrl)
                            val intent = Intent(Intent.ACTION_VIEW, intentUri)
                            startActivity(intent)
                        }
                    }
                }

                viewModel.getAvailablePlatforms(requireContext()).observe(this, Observer { it ->
                    availablePlatformsAdapter.mDiffer.submitList(it)
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
}