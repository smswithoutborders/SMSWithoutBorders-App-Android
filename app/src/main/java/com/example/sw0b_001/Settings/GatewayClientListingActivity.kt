package com.example.sw0b_001.Settings

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sw0b_001.AppCompactActivityCustomized
import com.example.sw0b_001.Data.GatewayClients.GatewayClient
import com.example.sw0b_001.Data.GatewayClients.GatewayClientViewModel
import com.example.sw0b_001.Data.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class GatewayClientListingActivity : AppCompactActivityCustomized() {

    lateinit var listViewAdapter: GatewayClientListingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gateway_clients_available)

        val myToolbar = findViewById<MaterialToolbar>(R.id.gateway_client_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView = findViewById<ListView>(R.id.gateway_clients_recycler_view)
        val gatewayClientsViewModel =
                ViewModelProvider(this)[GatewayClientViewModel::class.java]

        val linearProgressIndicator = findViewById<LinearProgressIndicator>(R.id.refresh_loader)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)

        val gatewayClient = GatewayClientsCommunications(applicationContext)
        gatewayClientsViewModel.get(applicationContext).observe(this, Observer {
            listViewAdapter = GatewayClientListingAdapter(gatewayClient, it)
            listView.adapter = listViewAdapter

            if(it.isNullOrEmpty())
                linearProgressIndicator.visibility = View.VISIBLE
            else
                linearProgressIndicator.visibility = View.GONE

            refreshLayout.isRefreshing = false
        })

        findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)
                .setOnRefreshListener {
                    refreshLayout.isRefreshing = true
                    gatewayClientsViewModel.loadRemote(applicationContext,
                            {
                                refreshLayout.isRefreshing = false
                            }
                    ) {
                        runOnUiThread {
                            refreshLayout.isRefreshing = false
                            Toast.makeText(applicationContext, "Failed to refresh...",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }

        val sharedPreferencesChangeListener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            listViewAdapter.notifyDataSetChanged()
//            Toast.makeText(applicationContext, "Default shared preferences changed",
//                    Toast.LENGTH_SHORT).show()
        }

        gatewayClient.sharedPreferences
                .registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)
    }

    class GatewayClientListingAdapter(val gatewayClientsCommunications: GatewayClientsCommunications,
                                      var gatewayClientsList: List<GatewayClient>) : BaseAdapter() {

        override fun getCount(): Int {
            return gatewayClientsList.size
        }

        override fun getItem(position: Int): GatewayClient {
            return gatewayClientsList[position]
        }

        override fun getItemId(position: Int): Long {
            return gatewayClientsList[position].id
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val defaultGatewayClientMsisdn = gatewayClientsCommunications.getDefaultGatewayClient()
            var view = convertView
            if(view == null) {
                val inflater: LayoutInflater = LayoutInflater.from(parent?.context);
                view = inflater.inflate(R.layout.layout_cardlist_gateway_clients, parent,
                        false)
            }

            val gatewayClient = getItem(position)
            view?.findViewById<MaterialTextView>(R.id.gateway_client_MSISDN)?.text =
                    gatewayClient.msisdn

            view?.setOnClickListener(gatewayClientOnClickListener(gatewayClient))

            val radioButton = view?.findViewById<SwitchMaterial>(R.id.gateway_client_radio_btn)
            radioButton?.isChecked = defaultGatewayClientMsisdn == gatewayClient.msisdn

            view?.findViewById<MaterialCardView>(R.id.gateway_client_listing_card)
                    ?.setOnClickListener(gatewayClientOnClickListener(gatewayClient))

            return view!!
        }

        private fun gatewayClientOnClickListener(gatewayClient: GatewayClient):
                OnClickListener {
            return OnClickListener {
                gatewayClientsCommunications
                        .updateDefaultGatewayClient(gatewayClient.msisdn)
            }
        }
    }
}