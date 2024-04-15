package com.example.sw0b_001.Security

import android.content.Context
import android.util.Log
import androidx.startup.Initializer

class LockScreenInitializer : Initializer<LockScreenInitializer.LockScreenRuntime> {

    class LockScreenRuntime(context: Context) {

        init {
            Log.d(javaClass.name, "Lock screen called now")
        }
    }

    override fun create(context: Context): LockScreenRuntime {
        return LockScreenRuntime(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}