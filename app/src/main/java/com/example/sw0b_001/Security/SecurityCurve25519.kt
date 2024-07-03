package com.example.sw0b_001.Security

import com.afkanerd.smswithoutborders.libsignal_doubleratchet.CryptoHelpers
import org.whispersystems.curve25519.Curve25519
import org.whispersystems.curve25519.Curve25519KeyPair

class SecurityCurve25519 {

    private val cipher: Curve25519 = Curve25519.getInstance(Curve25519.BEST)

    fun generateKey(): Curve25519KeyPair {
        return cipher.generateKeyPair()
    }

    fun calculateSharedSecret(peerPublicKey: ByteArray, keyPair: Curve25519KeyPair): ByteArray {
        val sharedKey = cipher.calculateAgreement(peerPublicKey, keyPair.privateKey)
        return CryptoHelpers.HKDF("HMACSHA256", sharedKey, null,
            "x25591_key_exchange".encodeToByteArray(), 32, 1)[0]
    }
}