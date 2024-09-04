package com.example.sw0b_001.Models

import android.util.Base64
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.Headers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.Ratchets
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms

class MessageComposer(val keystoreAlias: String) {

    init {

    }

    fun textComposer(availablePlatforms: AvailablePlatforms, sender: String, text: String): String {
        val content = "$sender:$text"

        val (header, cipherText_mk) = Ratchets.ratchetEncrypt(state, content, AD)
    }

    private fun formatTransmission(headers: Headers, cipherText: ByteArray, platformLetter: Byte): String {
        val sHeader = headers.serialized

        val headerLen = sHeader.size
        val encryptedContentPayload = byteArrayOf(headerLen.toByte()) + sHeader + cipherText

        val payloadLen = encryptedContentPayload.size
        val data = byteArrayOf(payloadLen.toByte()) + platformLetter + encryptedContentPayload
        TODO("Add deviceID here")

        return Base64.encodeToString(data, Base64.DEFAULT)
    }
}