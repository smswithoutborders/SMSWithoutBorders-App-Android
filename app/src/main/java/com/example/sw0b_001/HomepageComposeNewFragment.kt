package com.example.sw0b_001

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.PlatformsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView

class HomepageComposeNewFragment(private val bottomSheetViewLayout: Int =
        R.layout.fragment_modal_sheet_compose_platforms) : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modal_sheet_compose_platforms_layout,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewStub = view.findViewById<ViewStub>(R.id.homepage_compose_new_platform_layout)
        viewStub.layoutResource = bottomSheetViewLayout

        val inflatedView = viewStub.inflate()
        val bottomSheet = inflatedView.findViewById<View>(R.id.homepage_compose_new_platform_modal)
        val bottomSheetBehavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(bottomSheet)
        configureRecyclerView(view)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun configureRecyclerView(view: View) {
        val platformsRecyclerView = view.findViewById<RecyclerView>(R.id.homepage_compose_new_recycler_view)
        val linearLayoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        platformsRecyclerView.layoutManager = linearLayoutManager

        val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
        val platformsRecyclerAdapter = PlatformsRecyclerAdapter(fragmentTransaction)
        platformsRecyclerView.adapter = platformsRecyclerAdapter

        val platformsViewModel = ViewModelProvider(this)[PlatformsViewModel::class.java]
        context?.let { it ->
            platformsViewModel.get(it).observe(viewLifecycleOwner, Observer {
                platformsRecyclerAdapter.mDiffer.submitList(it)
                if(it.isNullOrEmpty()) {
                    view.findViewById<MaterialTextView>(R.id.homepage_no_platforms_saved)
                            .visibility = View.VISIBLE
                } else {
                    view.findViewById<MaterialTextView>(R.id.homepage_no_platforms_saved)
                            .visibility = View.GONE
                }
        }) }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

}