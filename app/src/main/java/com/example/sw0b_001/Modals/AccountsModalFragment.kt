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
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Platforms.PlatformsRecyclerAdapter
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.Models.Publisher
import com.example.sw0b_001.Models.Vault
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsModalFragment(val platformName: String, val type: AvailablePlatformsModalFragment.Type) :
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

        CoroutineScope(Dispatchers.Default).launch {
            val availablePlatforms = Datastore.getDatastore(requireContext())
                .availablePlatformsDao().fetch(platformName)
            activity?.runOnUiThread {
                viewModel.get(requireContext(), platformName).observe(viewLifecycleOwner, Observer {
                    accountsRecyclerAdapter.mDiffer.submitList(it)
                })

                accountsRecyclerAdapter.onClickListener.observe(viewLifecycleOwner, Observer {
                    when(type) {
                        AvailablePlatformsModalFragment.Type.SAVED -> {
                            dismiss()
                            savedPlatformsClicked(availablePlatforms, it)
                        }
                        AvailablePlatformsModalFragment.Type.AVAILABLE -> TODO()
                        AvailablePlatformsModalFragment.Type.ALL -> TODO()
                        AvailablePlatformsModalFragment.Type.REVOKE -> {
                            val onSuccessRunnable = Runnable {
                                activity?.runOnUiThread {
                                    view.findViewById<CircularProgressIndicator>(
                                        R.id.account_progress_view).visibility = View.VISIBLE
                                    accountsRecyclerView.visibility = View.GONE
                                }
                                revokePlatformsClick(view, it)
                            }
                            val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                            val loginModalFragment = LogoutDeleteConfirmationModalFragment(onSuccessRunnable)
                            fragmentTransaction?.add(loginModalFragment, "logout_delete_fragment")
                            fragmentTransaction?.show(loginModalFragment)
                            fragmentTransaction?.commit()
                        }
                    }
                })

            }
        }
    }

    private fun revokePlatformsClick(view: View, storedPlatformsEntity: StoredPlatformsEntity) {
        val llt = Vault.fetchLongLivedToken(requireContext())

        CoroutineScope(Dispatchers.Default).launch {
            val availablePlatforms = Datastore.getDatastore(requireContext())
                .availablePlatformsDao().fetch(storedPlatformsEntity.name!!)

            val publisher = Publisher(requireContext())
            try {
                when(availablePlatforms.protocol_type) {
                    "oauth2" -> {
                        publisher.revokeOAuthPlatforms(llt, storedPlatformsEntity.name,
                            storedPlatformsEntity.account!!)
                    }
                    "pnba" -> {
                        publisher.revokePNBAPlatforms(llt, storedPlatformsEntity.name,
                            storedPlatformsEntity.account!!)
                    }
                    else -> {
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "Unknown protocol...",
                                Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }
                }

                Datastore.getDatastore(requireContext()).storedPlatformsDao()
                    .delete(storedPlatformsEntity.id)
                dismiss()
            } catch(e: StatusRuntimeException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.status.description, Toast.LENGTH_SHORT).show()
                }
            } catch(e: Exception) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            } finally {
                publisher.shutdown()
                activity?.runOnUiThread {
                    view.findViewById<CircularProgressIndicator>(
                        R.id.account_progress_view).visibility = View.GONE
                    view.findViewById<RecyclerView>(
                        R.id.account_recyclerview).visibility = View.VISIBLE
                }
            }
        }
    }

    private fun savedPlatformsClicked(availablePlatforms: AvailablePlatforms,
                                      storedPlatformsEntity: StoredPlatformsEntity) {
        when(availablePlatforms.service_type) {
            "email" -> {
                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                val emailComposeModalFragment = EmailComposeModalFragment(storedPlatformsEntity) {
                    activity?.finish()
                }
                fragmentTransaction?.add(emailComposeModalFragment, "email_compose_tag")
                fragmentTransaction?.show(emailComposeModalFragment)
                fragmentTransaction?.commitNow()
            }
            "text" -> {
                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                val textComposeModalFragment = TextComposeModalFragment(storedPlatformsEntity) {
                    activity?.finish()
                }
                fragmentTransaction?.add(textComposeModalFragment, "text_compose_tag")
                fragmentTransaction?.show(textComposeModalFragment)
                fragmentTransaction?.commitNow()
            }
            "message" -> {
                val fragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
                val emailComposeModalFragment = EmailComposeModalFragment(storedPlatformsEntity) {
                    activity?.finish()
                }
                fragmentTransaction?.add(emailComposeModalFragment, "text_compose_tag")
                fragmentTransaction?.show(emailComposeModalFragment)
                fragmentTransaction?.commitNow()
            }
            else -> {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(),
                        getString(R.string.unknown_service_type), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}