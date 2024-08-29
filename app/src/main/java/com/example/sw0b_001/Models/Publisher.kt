package com.example.sw0b_001.Models

import com.example.sw0b_001.Security.Cryptography
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import publisher.v1.PublisherGrpc
import publisher.v1.PublisherGrpc.PublisherBlockingStub

class Publisher {
    private lateinit var channel: ManagedChannel

    private lateinit var publisherStub: PublisherBlockingStub

    fun init() {
        channel = ManagedChannelBuilder
            .forAddress("staging.smswithoutborders.com", 9060)
            .useTransportSecurity()
            .build()

        publisherStub = PublisherGrpc.newBlockingStub(channel)
    }
}