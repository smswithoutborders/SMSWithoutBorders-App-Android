package com.example.sw0b_001.Models.Platforms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.UserArtifactsHandler
import com.example.sw0b_001.Models.v2.Vault_V2
import com.example.sw0b_001.Modules.Network

class PlatformsViewModel : ViewModel() {

    private var liveData: LiveData<List<Platforms>> = MutableLiveData()
    private var mutableLiveData: MutableLiveData<Pair<List<Platforms>, List<Platforms>>> = MutableLiveData()

    fun get(context: Context): LiveData<List<Platforms>> {
        if(liveData.value.isNullOrEmpty()) {
            liveData = Datastore.getDatastore(context).platformDao().all
        }
        return liveData
    }

    fun getSaved(context: Context): LiveData<List<Platforms>> {
        if(liveData.value.isNullOrEmpty()) {
            liveData = Datastore.getDatastore(context).platformDao().saved
        }
        return liveData
    }

    public var networkResponseResults: Network.NetworkResponseResults? = null
    fun getUnsaved(context: Context, uid: String, password: String, onCompleteRunnable: Runnable): LiveData<List<Platforms>> {
        if(liveData.value.isNullOrEmpty()) {
            ThreadExecutorPool.executorService.execute {
                try {
                    networkResponseResults = Vault_V2.loginViaUID(context, uid, password)
                    val platforms = Vault_V2.getPlatforms(context,
                            networkResponseResults?.response?.headers!!, uid)
                    this.networkResponseResults = platforms.first

                    val listPlatforms = ArrayList<Platforms>()
                    platforms.second.unsaved_platforms.forEach {
                        val platform = Platforms()
                        platform.name = it.name
                        platform.description = ""
                        platform.type = it.type
                        platform.letter = it.letter
                        listPlatforms.add(platform)
                    }
                    platforms.second.saved_platforms.forEach {
                        val platform = Platforms()
                        platform.name = it.name
                        platform.description = ""
                        platform.type = it.type
                        platform.letter = it.letter
                        platform.isSaved = true
                        listPlatforms.add(platform)
                    }
                    Datastore.getDatastore(context).platformDao().insertAll(listPlatforms)
                } catch(e: Exception) {
                    e.printStackTrace()
                } finally {
                    onCompleteRunnable.run()
                }
            }
            liveData = Datastore.getDatastore(context).platformDao().unSaved
        }
        return liveData
    }

    fun getSavedCount(context: Context) : Int {
        return Datastore.getDatastore(context).platformDao().countSaved()
    }

    fun getSeparated(context: Context): MutableLiveData<Pair<List<Platforms>, List<Platforms>>> {
        val savedList = ArrayList<Platforms>()
        val unsavedList = ArrayList<Platforms>()
        if(!mutableLiveData.isInitialized) {
            ThreadExecutorPool.executorService.execute {
                val platforms: List<Platforms> = Datastore.getDatastore(context).platformDao().allList
                platforms.forEach {
                    if(it.isSaved) savedList.add(it)
                    else unsavedList.add(it)
                }
                mutableLiveData
                        .postValue(Pair<List<Platforms>, List<Platforms>>(savedList, unsavedList))
            }
        }
        return mutableLiveData
    }
}