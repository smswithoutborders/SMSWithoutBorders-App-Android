package com.example.sw0b_001

import com.example.sw0b_001.Modules.Helpers
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.net.URL

class HelperTest {

    @Test
    fun extractFirstPathTest() {
        val data = "apps://site.example.com/index.html?state=1urkUIP2&code=ejV5eEYx"
        val output = Helpers.getPath(data)
        assertEquals("/index.html", output)
    }

    @Test
    fun extractParametersTest() {
        val data = "apps://site.example.com/index.html?state=1urkUIP2&code=ejV5eEYx"
        val mappedParameters = Helpers.extractParameters(data)
        val expectedMap = mapOf("state" to "1urkUIP2", "code" to "ejV5eEYx")
        assertEquals(expectedMap, mappedParameters)
    }
}