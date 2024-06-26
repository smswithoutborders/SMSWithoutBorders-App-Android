package com.example.sw0b_001.Models.GatewayClients

import android.content.Context
import android.content.SharedPreferences
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Modules.Network
import com.example.sw0b_001.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GatewayClientsCommunications(context: Context) {

    @Serializable
    data class GatewayClientJsonPayload(val msisdn: String,
                                        val country: String,
                                        val operator: String,
                                        val operator_code: String,
                                        val protocols: ArrayList<String>,
                                        val last_published_date: String)

    private val filename = "gateway_client_prefs"
    private val defaultKey = "DEFAULT_KEY"

    val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(filename, Context.MODE_PRIVATE)

    fun updateDefaultGatewayClient(msisdn: String) {
        sharedPreferences.edit()
                .putString(defaultKey, msisdn)
                .apply()
    }

    fun getDefaultGatewayClient(): String? {
        return sharedPreferences.getString(defaultKey, "")
    }

    companion object {

        fun populateDefaultGatewayClientsSetDefaults(context: Context) {
            val gatewayClientList: MutableList<GatewayClient> = java.util.ArrayList()

            val gatewayClient = GatewayClient()
            gatewayClient.setCountry("Cameroon")
            gatewayClient.msisdn = context.getString(R.string.default_gateway_MSISDN_0)
            gatewayClient.setOperatorName("MTN Cameroon")
            gatewayClient.setOperatorId("62401")
            gatewayClient.setType("custom")

            val gatewayClient2 = GatewayClient()
            gatewayClient2.setCountry("Cameroon")
            gatewayClient2.msisdn = context.getString(R.string.default_gateway_MSISDN_2)
            gatewayClient2.setOperatorName("Orange Cameroon")
            gatewayClient2.setOperatorId("62402")
            gatewayClient2.setType("custom")
            if(GatewayClientsCommunications(context)
                    .getDefaultGatewayClient().isNullOrEmpty())
                GatewayClientsCommunications(context)
                        .updateDefaultGatewayClient(gatewayClient2.msisdn)

            val gatewayClient3 = GatewayClient()
            gatewayClient3.setCountry("Nigeria")
            gatewayClient3.msisdn = context.getString(R.string.default_gateway_MSISDN_3)
            gatewayClient3.setOperatorName("MTN Nigeria")
            gatewayClient3.setOperatorId("62130")
            gatewayClient3.setType("custom")

            gatewayClientList.add(gatewayClient)
            gatewayClientList.add(gatewayClient2)
            gatewayClientList.add(gatewayClient3)

            Datastore.getDatastore(context).gatewayClientsDao().refresh(gatewayClientList)
        }
        private fun fetchRemote(context: Context): ArrayList<GatewayClientJsonPayload> {
            val url = context.getString(R.string.smswithoutboarders_official_gateway_client_seeding_url)
            val networkResponseResults = Network.requestGet(url)
            when(networkResponseResults.response.statusCode) {
                in 400..500 -> throw Exception("Failed to fetch Gateway clients")
                in 500..600 -> throw Exception("Error fetching Gateway clients")
                else -> {
                    val json = Json {ignoreUnknownKeys = true}
                    return json.decodeFromString<
                            ArrayList<
                                    GatewayClientJsonPayload>>(networkResponseResults.result.get())
                }
            }
        }

        fun fetchAndPopulateWithDefault(context: Context) {
            populateDefaultGatewayClientsSetDefaults(context)
            val gatewayClientList = ArrayList<GatewayClient>()
            try {
                val gatewayClients : ArrayList<GatewayClientJsonPayload> = fetchRemote(context)
                gatewayClients.forEach {
                    val gatewayClient = GatewayClient()
                    gatewayClient.setCountry(it.country)
                    gatewayClient.msisdn = it.msisdn
                    gatewayClient.setOperatorName(it.operator)
                    gatewayClient.setOperatorId(it.operator_code)
                    gatewayClientList.add(gatewayClient)
                }
                Datastore.getDatastore(context).gatewayClientsDao().insertAll(gatewayClientList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}