package com.example.sw0b_001.Modals

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.GatewayClients.GatewayClient
import com.example.sw0b_001.Models.GatewayClients.GatewayClientAddModalFragment
import com.example.sw0b_001.Models.GatewayClients.GatewayClientViewModel
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GatewayClientCardOptionsModalFragment(val gatewayClient: GatewayClient) : BottomSheetDialogFragment(R.layout.fragment_modal_gateway_client_card_options) {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val viewModel: GatewayClientViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.gateway_client_card_options_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val makeDefaultButton = view.findViewById<View>(R.id.make_default_button)
        val deleteButton = view.findViewById<View>(R.id.delete_button)
        val editButton = view.findViewById<View>(R.id.edit_button)

        if (gatewayClient.type == GatewayClient.TYPE_CUSTOM) {
            editButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
        } else {
            editButton.visibility = View.GONE
            deleteButton.visibility = View.GONE
        }

        makeDefaultButton.setOnClickListener {
            val gatewayClientsCommunications = GatewayClientsCommunications(requireContext())
            gatewayClientsCommunications.updateDefaultGatewayClient(gatewayClient.mSISDN!!)
            CoroutineScope(Dispatchers.Default).launch {
                Datastore.getDatastore(it.context).gatewayClientsDao().update(gatewayClient)
            }
            Toast.makeText(requireContext(), "Default gateway client updated", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        editButton.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val gatewayClientAddFragment = GatewayClientAddModalFragment.newInstance(gatewayClient.id)
            fragmentTransaction.add(gatewayClientAddFragment, "gateway_client_add_tag")
            fragmentTransaction.show(gatewayClientAddFragment)
            fragmentTransaction.commit()
            dismiss()
        }

        deleteButton.setOnClickListener {
            val defaultGatewayClientMsisdn = GatewayClientsCommunications(requireContext()).getDefaultGatewayClient()
            if (defaultGatewayClientMsisdn == gatewayClient.mSISDN) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.cannot_delete_selected_title)
                    .setMessage(R.string.cannot_delete_selected_message)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.delete_gateway_client_title)
                    .setMessage(R.string.delete_gateway_client_message)
                    .setPositiveButton(R.string.delete) { dialog, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.delete(requireContext(), gatewayClient)
                        }
                        dialog.dismiss()
                        dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }


    }
}