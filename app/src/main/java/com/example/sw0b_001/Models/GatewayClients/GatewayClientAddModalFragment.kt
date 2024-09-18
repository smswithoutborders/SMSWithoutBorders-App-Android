package com.example.sw0b_001.Models.GatewayClients

import android.os.Bundle
import android.view.View
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GatewayClientAddModalFragment :
        BottomSheetDialogFragment(R.layout.fragment_gateway_client_add_modal) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.findViewById<View>(R.id.gateway_client_add_modal)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<MaterialButton>(R.id.gateway_client_add_custom_btn)
                .setOnClickListener {
                    addGatewayClients(view)
                }
    }

    private fun addGatewayClients(view: View) {
        val contactTextView = view.findViewById<TextInputEditText>(R.id.gateway_client_add_contact)
        val aliasTextView = view.findViewById<TextInputEditText>(R.id.gateway_client_add_contact_alias)

        if(contactTextView.text.isNullOrEmpty()) {
            contactTextView.error = getString(R.string.gateway_client_settings_add_custom_empty_error)
            return
        }

        val gatewayClient = GatewayClient()
        gatewayClient.mSISDN = contactTextView.text.toString()
        gatewayClient.alias = aliasTextView.text?.toString()
        gatewayClient.type = GatewayClient.TYPE_CUSTOM

        CoroutineScope(Dispatchers.Default).launch {
            Datastore.getDatastore(context).gatewayClientsDao().insert(gatewayClient)
            dismiss()
        }
    }
}