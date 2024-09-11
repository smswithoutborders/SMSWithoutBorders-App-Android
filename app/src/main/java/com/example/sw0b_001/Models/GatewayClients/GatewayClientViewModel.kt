package com.example.sw0b_001.Models.GatewayClients

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sw0b_001.Database.Datastore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GatewayClientViewModel() : ViewModel() {
    private var liveData: LiveData<List<GatewayClient>> = MutableLiveData()
    private val _selectedGatewayClient = MutableStateFlow<GatewayClient?>(null)
    val selectedGatewayClient: StateFlow<GatewayClient?> = _selectedGatewayClient.asStateFlow()

    fun get(context: Context, successRunnable: Runnable?): LiveData<List<GatewayClient>> {
        if(liveData.value.isNullOrEmpty()) {
            loadRemote(context, successRunnable, successRunnable)
            liveData = Datastore.getDatastore(context).gatewayClientsDao().all
        }
        return liveData
    }

    fun updateSelectedGatewayClient(newGatewayClientMsisdn: String?) {
        val newGatewayClient = liveData.value?.find { it.mSISDN == newGatewayClientMsisdn }
        _selectedGatewayClient.value = newGatewayClient
    }

    fun loadRemote(context: Context,
                   successRunnable: Runnable?,
                   failureRunnable: Runnable?){
        CoroutineScope(Dispatchers.Default).launch{
            try {
                GatewayClientsCommunications.fetchAndPopulateWithDefault(context)

                val gatewayClient = GatewayClientsCommunications(context)
                val defaultMsisdn = gatewayClient.getDefaultGatewayClient()
                val defaultGatewayClient = defaultMsisdn?.let { getGatewayClientByMsisdn(context, it) }

                _selectedGatewayClient.value = defaultGatewayClient
                successRunnable?.run()
            } catch (e: Exception) {
                Log.e(javaClass.name, "Exception fetching Gateway clients", e)
                failureRunnable?.run()
            }
        }
    }

    private fun getGatewayClientByMsisdn(context: Context, msisdn: String): GatewayClient? {
        return Datastore.getDatastore(context).gatewayClientsDao().getByMsisdn(msisdn)
    }
}