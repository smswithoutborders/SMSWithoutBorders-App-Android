package com.example.sw0b_001.Modals

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Modals.PlatformComposers.EmailComposeModalFragment
import com.example.sw0b_001.Modals.PlatformComposers.TextComposeModalFragment
import com.example.sw0b_001.Models.Platforms.AccountsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.AccountsViewModel
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val availablePlatforms = Datastore.getDatastore(requireContext())
                .availablePlatformsDao().fetch(platformName)
            activity?.runOnUiThread {
                viewModel.get(requireContext(), platformName).observe(viewLifecycleOwner, Observer {
                    accountsRecyclerAdapter.mDiffer.submitList(it)
                })

                accountsRecyclerAdapter.onClickListener.observe(viewLifecycleOwner, Observer {
                    dismiss()
                    when(availablePlatforms.service_type) {
                        "email" -> {
                            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                            val emailComposeModalFragment = EmailComposeModalFragment(it) {
                                activity?.finish()
                            }
                            fragmentTransaction?.add(emailComposeModalFragment, "email_compose_tag")
                            fragmentTransaction?.show(emailComposeModalFragment)
                            fragmentTransaction?.commitNow()
                        }
                        "text" -> {
                            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                            val textComposeModalFragment = TextComposeModalFragment(it) {
                                activity?.finish()
                            }
                            fragmentTransaction?.add(textComposeModalFragment, "text_compose_tag")
                            fragmentTransaction?.show(textComposeModalFragment)
                            fragmentTransaction?.commitNow()
                        }
                        "message" -> {

                        }
                        else -> {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(),
                                    getString(R.string.unknown_service_type), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })

            }
        }
    }
}