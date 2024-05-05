package com.example.sw0b_001

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Properties


@RunWith(AndroidJUnit4::class)
class UserManagementTest {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val properties: Properties = Properties()
    val inputStream = context.resources.openRawResource(R.raw.v2)

    @Before
    fun initialize() {
        properties.load(inputStream)
    }

    @Test
    fun deleteAccountTest() {
        val password = properties["password"].toString()
    }

}