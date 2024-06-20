package com.example.sw0b_001.Security

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyPairGenerator

@RunWith(AndroidJUnit4::class)
class CryptographyTest {
    var context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun cryptographyTest() {
        val keyPairGenerator = KeyPairGenerator.getInstance("x25519")
        val keyPair = keyPairGenerator.generateKeyPair()
    }
}

