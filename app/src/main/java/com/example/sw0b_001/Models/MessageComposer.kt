package com.example.sw0b_001.Models

import android.content.Context
import android.util.Base64
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.preference.PreferenceManager
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.CryptoHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.KeystoreHelpers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.SecurityCurve25519
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.Headers
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.Ratchets
import com.afkanerd.smswithoutborders.libsignal_doubleratchet.libsignal.States
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Modules.Helpers.toBytes
import java.nio.ByteBuffer
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

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val usePhoneNumber = sharedPreferences.getBoolean("use_phone_number_switch", false)

        val deviceID = if(!usePhoneNumber) Vault.fetchDeviceId(context) else null
        return formatTransmission(header,  cipherMk,
            availablePlatforms.shortcode!!.encodeToByteArray()[0], deviceID)
    }

    companion object {
        fun formatTransmission(headers: Headers,
                               cipherText: ByteArray,
                               platformLetter: Byte, deviceID: ByteArray? = null): String {
            val sHeader = headers.serialized

            val bytesLen = sHeader.size.toBytes()
            val encryptedContentPayload = bytesLen + sHeader + cipherText
            val payloadBytesLen = encryptedContentPayload.size.toBytes()
            var data = payloadBytesLen + platformLetter + encryptedContentPayload

            deviceID?.let {
                println("DeviceID: $it")
                data += it
            }
            return Base64.encodeToString(data, Base64.DEFAULT)
        }
    }
}