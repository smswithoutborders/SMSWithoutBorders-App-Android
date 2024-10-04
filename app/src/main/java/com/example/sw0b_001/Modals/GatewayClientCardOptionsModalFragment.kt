package com.example.sw0b_001.Modals

import android.os.Bundle
import android.view.View
import com.example.sw0b_001.Models.GatewayClients.GatewayClient
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GatewayClientCardOptionsModalFragment(val gatewayClient: GatewayClient) : BottomSheetDialogFragment(R.layout.fragment_modal_gateway_client_card_options) {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.gateway_client_card_options_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val makeDefaultButton = view.findViewById<View>(R.id.make_default_button)
        val deleteButton = view.findViewById<View>(R.id.delete_button)
        val editButton = view.findViewById<View>(R.id.edit_button)


    }
}