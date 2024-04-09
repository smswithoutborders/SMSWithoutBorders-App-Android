package com.example.sw0b_001.Models.Platforms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.ThreadExecutorPool
import java.util.concurrent.ThreadPoolExecutor

class PlatformsViewModel : ViewModel() {

    private val liveData: MutableLiveData<List<Platforms>> = MutableLiveData()
    fun get(context: Context): LiveData<List<Platforms>> {
        ThreadExecutorPool.executorService.execute(Runnable {
            liveData.postValue(Datastore.getDatastore(context).platformDao().all)
        })
        return liveData
    }
}