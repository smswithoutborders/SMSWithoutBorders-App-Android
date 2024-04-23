package com.example.sw0b_001.Modules

import android.content.Intent
import android.util.Log

class Helpers {
    companion object {

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