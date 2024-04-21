package com.example.sw0b_001

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Data.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Data.Platforms.PlatformsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlatformsModalFragment : BottomSheetDialogFragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_modal_sheet_store_platforms, container,
                false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.store_platforms_constraint)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        configureRecyclerView(view)
    }

    private fun configureRecyclerView(view: View) {
        val unsavedPlatformsRecyclerView =
                view.findViewById<RecyclerView>(R.id.store_platforms_unsaved_recycler_view)

        val savedPlatformsRecyclerView =
                view.findViewById<RecyclerView>(R.id.store_platforms_saved_recycler_view)

        val savedLinearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)

        val unsavedLinearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false)

        unsavedPlatformsRecyclerView.layoutManager = unsavedLinearLayoutManager
        savedPlatformsRecyclerView.layoutManager = savedLinearLayoutManager

        val savedPlatformsAdapter = PlatformsRecyclerAdapter()
        val unSavedPlatformsAdapter = PlatformsRecyclerAdapter()
        unsavedPlatformsRecyclerView.adapter = savedPlatformsAdapter
        savedPlatformsRecyclerView.adapter = unSavedPlatformsAdapter

        val platformsViewModel = ViewModelProvider(this)[PlatformsViewModel::class.java]

        context?.let { it ->
            platformsViewModel.getSeparated(it).observe(this, Observer {
                savedPlatformsAdapter.mDiffer.submitList(it.first)
                unSavedPlatformsAdapter.mDiffer.submitList(it.second)
            })
        }
    }
}