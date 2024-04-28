package com.example.sw0b_001.Models.v2

import android.content.Context
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.R

object GatewayClients_V2 {

    fun populateGatewayClients(context: Context) {
        val url = context.getString(R.string.gateway_client_seeding_url)
        GatewayClientsCommunications.fetchRemote(url)
    }
}