package com.example.sw0b_001.Modules

import android.content.Intent
import android.util.Log
import java.net.URL

class Helpers {
    companion object {

        fun extractParameters(data: String) : Map<String, String> {
            val query = URL(data.replace("apps://", "https://")).query
            val parameters = query.split("&")
            val mappedParameters = emptyMap<String, String>().toMutableMap()
            parameters.forEach {
                val entries = it.split("=")
                mappedParameters[entries[0]] = entries[1]
            }
            return mappedParameters
        }
        fun getPath(data: String): String {
            return URL(data.replace("apps://", "https://")).path
        }

        fun logIntentDetails(intent: Intent?) {
            if (intent == null) {
                Log.v("Intent", "Intent is null")
                return
            }

            // Log basic information
            Log.v("Intent", "Package: ${intent.`package`}")
            Log.v("Intent", "Action: ${intent.action}")
            Log.v("Intent", "Type: ${intent.type}")
            Log.v("Intent", "Component: ${intent.component}")
            Log.v("Intent", "Data String: ${intent.dataString}")

            // Log categories (if any)
            val categories = intent.categories
            if (categories != null && categories.isNotEmpty()) {
                Log.v("Intent", "Categories:")
                for (category in categories) {
                    Log.v("Intent", "\t- $category")
                }
            } else {
                Log.v("Intent", "Categories: null")
            }

            // Log extras (if any)
            val extras = intent.extras
            if (extras != null) {
                Log.v("Intent", "Extras:")
                for (key in extras.keySet()) {
                    val value = extras.get(key)
                    Log.v("Intent", "\t- $key: $value")
                }
            } else {
                Log.v("Intent", "Extras: null")
            }
        }

    }
}