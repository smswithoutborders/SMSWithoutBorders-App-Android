package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
import androidx.core.util.component1
import androidx.core.util.component2
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.CryptoHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityCurve25519
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.Headers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.Ratchets
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.States
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import java.security.KeyPair
import java.security.KeyPairGenerator

class MessageComposer(val context: Context, val state: States) {
    private val AD = Publisher.fetchPublisherPublicKey(context)

    init {
        if(state.DHs == null) {
            val SK = Publisher.fetchPublisherSharedKey(context)
            Ratchets.ratchetInitAlice(state, SK, AD)
        }
    }

    fun compose(availablePlatforms: AvailablePlatforms, content: String): String {
        val (header, cipherMk) = Ratchets.ratchetEncrypt(state, content.encodeToByteArray(), AD)
        return formatTransmission(header,  cipherMk,
            availablePlatforms.shortcode!!.encodeToByteArray()[0])
    }

    private fun formatTransmission(headers: Headers, cipherText: ByteArray, platformLetter: Byte): String {
        val sHeader = headers.serialized

        val headerLen = sHeader.size
        val encryptedContentPayload = byteArrayOf(headerLen.toByte()) + sHeader + cipherText

        val payloadLen = encryptedContentPayload.size
        var data = byteArrayOf(payloadLen.toByte()) + platformLetter + encryptedContentPayload
        data += Vault.fetchDeviceId(context)!!
        return Base64.encodeToString(data, Base64.DEFAULT)
    }
}