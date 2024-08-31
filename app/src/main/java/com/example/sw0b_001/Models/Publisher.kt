package com.example.sw0b_001.Models

import android.content.Context
import android.widget.Toast
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.R
import com.example.sw0b_001.Security.Cryptography
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.serialization.json.Json
import publisher.v1.PublisherGrpc
import publisher.v1.PublisherGrpc.PublisherBlockingStub

class Publisher(val context: Context) {

    companion object {
        val PUBLISHER_ID_KEYSTORE_ALIAS = "PUBLISHER_ID_KEYSTORE_ALIAS"

        fun getAvailablePlatforms(context: Context): ArrayList<AvailablePlatforms> {
            try {
                val response = Network.requestGet(context.getString(R.string.publisher_get_platforms_url))
                return Json.decodeFromString<ArrayList<AvailablePlatforms>>(response.result.get())
            } catch(e: Exception) {
                e.printStackTrace()
                throw Throwable(e)
            }
        }
    }

    private var channel: ManagedChannel = ManagedChannelBuilder
        .forAddress(context.getString(R.string.publisher_grpc_url),
            context.getString(R.string.publisher_grpc_port).toInt())
        .useTransportSecurity()
        .build()

    private var publisherStub = PublisherGrpc.newBlockingStub(channel)
}