package com.example.sw0b_001.Settings

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
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
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.GatewayClients.GatewayClient
import com.example.sw0b_001.Models.GatewayClients.GatewayClientAddModalFragment
import com.example.sw0b_001.Models.GatewayClients.GatewayClientViewModel
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class GatewayClientListingActivity : AppCompactActivityCustomized() {

    private lateinit var listViewAdapter: GatewayClientListingAdapter

    private lateinit var gatewayClientsViewModel: GatewayClientViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gateway_clients_available)

        val myToolbar = findViewById<MaterialToolbar>(R.id.gateway_client_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView = findViewById<ListView>(R.id.gateway_clients_recycler_view)

        val linearProgressIndicator = findViewById<LinearProgressIndicator>(R.id.refresh_loader)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)

        refreshLayout.isRefreshing = true
        linearProgressIndicator.visibility = View.VISIBLE

        val gatewayClient = GatewayClientsCommunications(applicationContext)
        gatewayClientsViewModel = ViewModelProvider(this)[GatewayClientViewModel::class.java]

        gatewayClientsViewModel.get(applicationContext) {
            runOnUiThread {
                linearProgressIndicator.visibility = View.GONE
                refreshLayout.isRefreshing = false
            }
        }.observe(this, Observer {
            listViewAdapter = GatewayClientListingAdapter(gatewayClient, it)
            listView.adapter = listViewAdapter
        })

        findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)
                .setOnRefreshListener { refresh() }


        gatewayClient.sharedPreferences
                .registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)
    }

    private val sharedPreferencesChangeListener = OnSharedPreferenceChangeListener { _, _ ->
        println("Yes things have changed")
        if(::listViewAdapter.isInitialized)
            listViewAdapter.notifyDataSetChanged()
    }

    private fun refresh() {
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)
        refreshLayout.isRefreshing = true

        val linearProgressIndicator = findViewById<LinearProgressIndicator>(R.id.refresh_loader)
        linearProgressIndicator.visibility = View.VISIBLE

        gatewayClientsViewModel.loadRemote(applicationContext,
                { refreshLayout.isRefreshing = false } ) {
            runOnUiThread {
                refreshLayout.isRefreshing = false
                linearProgressIndicator.visibility = View.GONE
                Toast.makeText(applicationContext, "Failed to refresh...",
                        Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.gateway_client_settings_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.gateway_client_menu_refresh) {
            refresh()
            return true
        }
        else if(item.itemId == R.id.gateway_client_menu_add_contact) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()

            val gatewayClientAddFragment = GatewayClientAddModalFragment()
            fragmentTransaction.add(gatewayClientAddFragment, "gateway_client_add_tag")
            fragmentTransaction.show(gatewayClientAddFragment)
            fragmentTransaction.commitNow()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class GatewayClientListingAdapter(private val gatewayClientsCommunications: GatewayClientsCommunications,
                                      private var gatewayClientsList: List<GatewayClient>) : BaseAdapter() {

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
            radioButton?.setOnClickListener(gatewayClientOnClickListener(gatewayClient))


            view?.findViewById<MaterialCardView>(R.id.gateway_client_listing_card)
                    ?.setOnClickListener(gatewayClientOnClickListener(gatewayClient))

            return view!!
        }

        private fun gatewayClientOnClickListener(gatewayClient: GatewayClient):
                OnClickListener {
            return OnClickListener {
                gatewayClientsCommunications.updateDefaultGatewayClient(gatewayClient.msisdn)
                ThreadExecutorPool.executorService.execute {
                    gatewayClient.type = "custom"
                    Datastore.getDatastore(it.context).gatewayClientsDao().update(gatewayClient)
                }
            }
        }
    }
}