package com.example.sw0b_001.Data.GatewayClients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class GatewayClientAddModalFragment : BottomSheetDialogFragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gateway_client_add_modal, container, false)
    }

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
        gatewayClient.msisdn = contactTextView.text.toString()
        gatewayClient.alias = aliasTextView.text?.toString()
        gatewayClient.type = GatewayClient.TYPE_CUSTOM

        ThreadExecutorPool.executorService.execute(Runnable {
            Datastore.getDatastore(context).gatewayClientsDao().insert(gatewayClient)
            dismiss()
        })
    }
}