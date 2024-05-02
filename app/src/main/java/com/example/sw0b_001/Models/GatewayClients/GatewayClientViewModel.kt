package com.example.sw0b_001.Models.GatewayClients

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.R
import com.github.kittinunf.result.Result
import kotlinx.serialization.json.Json

class GatewayClientViewModel : ViewModel() {
    private var liveData: LiveData<List<GatewayClient>> = MutableLiveData()
    fun get(context: Context, successRunnable: Runnable?): LiveData<List<GatewayClient>> {
        if(liveData.value.isNullOrEmpty()) {
            loadRemote(context, successRunnable, null)
            liveData = Datastore.getDatastore(context).gatewayClientsDao().all
        }
        return liveData
    }

    fun loadRemote(context: Context,
                   successRunnable: Runnable?,
                   failureRunnable: Runnable?){
        ThreadExecutorPool.executorService.execute(Runnable {
            try {
                GatewayClientsCommunications.fetchAndPopulateWithDefault(context)
                successRunnable?.run()
            } catch (e: Exception) {
                Log.e(javaClass.name, "Exception fetching Gateway clients", e)
                failureRunnable?.run()
            }
        })
    }
}