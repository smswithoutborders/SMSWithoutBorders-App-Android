package com.example.sw0b_001.Security

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyPairGenerator

@RunWith(AndroidJUnit4::class)
class CryptographyTest {
    var context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun x25519Test() {
        val x = SecurityCurve25519()
        val xpk = x.generateKey()
        println("PK: ${Base64.encodeToString(xpk, Base64.DEFAULT)}")

        val peer = Base64.decode("EWQUXcsp47l6XFUcQZ2rPkeKmuCFipeCzf9w7IBBzlU=", Base64.DEFAULT)

        println("SK: ${Base64.encodeToString(x.calculateSharedSecret(peer), Base64.DEFAULT)}")
    }

}

