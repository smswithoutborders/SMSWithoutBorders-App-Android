package com.example.sw0b_001.Models.GatewayClients

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.R
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.result.Result
import kotlinx.serialization.json.Json

class GatewayClientViewModel : ViewModel() {
    private var liveData: LiveData<List<GatewayClient>> = MutableLiveData()
    fun get(context: Context): LiveData<List<GatewayClient>> {
        if(liveData.value.isNullOrEmpty()) {
            loadRemote(context, null, null)
            liveData = Datastore.getDatastore(context).gatewayClientsDao().all
        }
        return liveData
    }

    fun loadRemote(context: Context,
                   successRunnable: Runnable?,
                   failureRunnable: Runnable?){
        val url = context.getString(R.string.gateway_client_seeding_url)
        ThreadExecutorPool.executorService.execute(Runnable {
            val (_, response, result) = GatewayClientsCommunications.fetchRemote(url)
            when(result) {
                is Result.Success -> {
                    val gatewayClientsList = Json
                            .decodeFromString<ArrayList<GatewayClientsCommunications.GatewayClient>>(result.get())

                    val gatewayClients = ArrayList<GatewayClient>()
                    gatewayClientsList.forEach {
                        val gatewayClient = GatewayClient()
                        gatewayClient.msisdn = it.msisdn
                        gatewayClient.country = it.country
                        gatewayClient.operatorName = ""
                        gatewayClients.add(gatewayClient)
                    }
                    Datastore.getDatastore(context).gatewayClientsDao().refresh(gatewayClients)
                    Log.d(javaClass.name, result.get())
                    successRunnable?.run()
                }
                is Result.Failure -> {
                    Log.e(javaClass.name, "Exception fetching Gateway clients", result.getException())
                    failureRunnable?.run()
                }
                else -> {

                }
            }

        })
    }
}