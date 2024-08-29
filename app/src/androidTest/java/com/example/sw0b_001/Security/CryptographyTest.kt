package com.example.sw0b_001.Security

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CryptographyTest {
    var context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun x25519Test() {
        val aliceKeystoreAlias: String = "aliceKeystoreAlias"
        val bobKeystoreAlias: String = "bobKeystoreAlias"

        val alice = Cryptography.generateKey(context, aliceKeystoreAlias)
        val bob = Cryptography.generateKey(context, bobKeystoreAlias)

        assertArrayEquals(Cryptography.calculateSharedSecret(context, aliceKeystoreAlias, bob),
            Cryptography.calculateSharedSecret(context, bobKeystoreAlias, alice))
    }

}

