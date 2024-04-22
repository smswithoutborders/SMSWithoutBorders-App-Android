package com.example.sw0b_001

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Data.Platforms.Platforms
import com.example.sw0b_001.Data.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Data.Platforms.PlatformsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlatformsModalFragment(val showType: Int = SHOW_TYPE_ALL) : BottomSheetDialogFragment() {

    companion object {
        public const val SHOW_TYPE_SAVED = 0
        public const val SHOW_TYPE_UNSAVED = 1
        public const val SHOW_TYPE_ALL = 2
    }

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

    lateinit var savedPlatformsAdapter: PlatformsRecyclerAdapter
    lateinit var unSavedPlatformsAdapter: PlatformsRecyclerAdapter
    private fun configureRecyclerView(view: View) {
        val unsavedPlatformsRecyclerView =
                view.findViewById<RecyclerView>(R.id.store_platforms_unsaved_recycler_view)

        val savedPlatformsRecyclerView =
                view.findViewById<RecyclerView>(R.id.store_platforms_saved_recycler_view)

        when(showType) {
            SHOW_TYPE_SAVED -> unsavedPlatformsRecyclerView.visibility = View.GONE
            SHOW_TYPE_UNSAVED -> savedPlatformsRecyclerView.visibility = View.GONE
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

        savedPlatformsAdapter.onClickListenerLiveData.observe(this, Observer {
            Log.d(javaClass.name, "Yes saved platform clicked")
            if(it != null) {
                savedPlatformsAdapter.onClickListenerLiveData = MutableLiveData();
                when(it) {
                    Platforms.TYPE_EMAIL -> {
                        val emailComposeModalFragment = EmailComposeModalFragment()
                        fragmentTransaction?.add(emailComposeModalFragment, "compose_fragment_email")
                        fragmentTransaction?.show(emailComposeModalFragment)
                        activity?.runOnUiThread { fragmentTransaction?.commitNow() }
                    }
                }
                dismiss()
            }
        })

        val platformsViewModel = ViewModelProvider(this)[PlatformsViewModel::class.java]
        context?.let { it ->
            platformsViewModel.getSeparated(it).observe(this, Observer {
                savedPlatformsAdapter.mDiffer.submitList(it.first)
                unSavedPlatformsAdapter.mDiffer.submitList(it.second)
            })
        }
    }
}