package com.example.sw0b_001.Data.Platforms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Database.Datastore

class PlatformsViewModel : ViewModel() {

    private var liveData: LiveData<List<Platforms>> = MutableLiveData()
    fun get(context: Context): LiveData<List<Platforms>> {
        if(liveData.value.isNullOrEmpty()) {
            liveData = Datastore.getDatastore(context).platformDao().all
        }
        return liveData
    }
}