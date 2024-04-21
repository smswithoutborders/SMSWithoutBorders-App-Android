package com.example.sw0b_001.Data.Platforms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Data.ThreadExecutorPool
import com.example.sw0b_001.Database.Datastore

class PlatformsViewModel : ViewModel() {

    private var liveData: LiveData<List<Platforms>> = MutableLiveData()
    private var mutableLiveData: MutableLiveData<Pair<List<Platforms>, List<Platforms>>> = MutableLiveData()

    fun get(context: Context): LiveData<List<Platforms>> {
        if(liveData.value.isNullOrEmpty()) {
            liveData = Datastore.getDatastore(context).platformDao().all
        }
        return liveData
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

    fun storeAll(context: Context, platforms: List<Platforms>) {
        Datastore.getDatastore(context).platformDao().insertAll(platforms)
    }

    public fun store(context: Context, platforms: Platforms) {
        Datastore.getDatastore(context).platformDao().insert(platforms)
    }

    public fun deleteAll(context: Context) {
        Datastore.getDatastore(context).platformDao().deleteAll()
    }
}