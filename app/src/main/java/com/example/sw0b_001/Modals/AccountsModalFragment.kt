package com.example.sw0b_001.Modals

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Models.Platforms.AccountsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.AccountsViewModel
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AccountsModalFragment(val platformName: String) :
    BottomSheetDialogFragment(R.layout.fragment_modal_accounts) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.accounts_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED


        val accountsRecyclerView = view.findViewById<RecyclerView>(R.id.account_recyclerview)
        val accountsLinearLayoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)
        accountsRecyclerView.layoutManager = accountsLinearLayoutManager

        val accountsRecyclerAdapter = AccountsRecyclerAdapter()
        accountsRecyclerView.adapter = accountsRecyclerAdapter

        val viewModel: AccountsViewModel by viewModels()
        viewModel.get(requireContext(), platformName).observe(viewLifecycleOwner, Observer {
            accountsRecyclerAdapter.mDiffer.submitList(it)
        })
    }
}