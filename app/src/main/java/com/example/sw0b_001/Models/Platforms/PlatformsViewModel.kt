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

    private var availableLiveData: LiveData<List<AvailablePlatforms>> = MutableLiveData()

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

    fun getAvailablePlatforms(context: Context): LiveData<List<AvailablePlatforms>> {
        if(liveData.value.isNullOrEmpty()) {
            availableLiveData = Datastore.getDatastore(context).availablePlatformsDao().fetchAll()
        }
        return availableLiveData
    }

    fun getSavedCount(context: Context) : Int {
        return Datastore.getDatastore(context).platformDao().countSaved()
    }
}