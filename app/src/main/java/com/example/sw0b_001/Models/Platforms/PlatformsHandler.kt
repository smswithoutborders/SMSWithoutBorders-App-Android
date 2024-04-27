package com.example.sw0b_001.Models.Platforms

import android.content.Context
import android.util.Log
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.v2.Vault_V2
import com.github.kittinunf.fuel.core.Headers

class PlatformsHandler {
    companion object {
        fun storePlatforms(context: Context, uid: String, url: String, headers: Headers) {
            val platforms = Vault_V2.getPlatforms(url, headers, uid)

            Datastore.getDatastore(context).platformDao().deleteAll()
            val listPlatforms = ArrayList<Platforms>()
            platforms.saved_platforms.forEach {
                val platform = Platforms()
                platform.name = it.name
                platform.description = ""
                platform.type = it.type
                platform.letter = it.letter
                platform.isSaved = true
//            platform.logo = PlatformsHandler
//                    .hardGetLogoByName(applicationContext, it.name)
//            platformsViewModel.store(requireContext(), platform)
                listPlatforms.add(platform)
            }

            platforms.unsaved_platforms.forEach {
                val platform = Platforms()
                platform.name = it.name
                platform.description = ""
                platform.type = it.type
                platform.letter = it.letter
//            platform.logo = PlatformsHandler
//                    .hardGetLogoByName(applicationContext, it.name)
//            platformsViewModel.store(requireContext(), platform)
                listPlatforms.add(platform)
            }
            Datastore.getDatastore(context).platformDao().insertAll(listPlatforms)
            Log.d(javaClass.name, "Platforms stored")
        }
    }
}