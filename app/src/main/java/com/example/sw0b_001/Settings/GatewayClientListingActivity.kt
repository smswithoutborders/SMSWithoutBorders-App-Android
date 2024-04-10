package com.example.sw0b_001.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sw0b_001.AppCompactActivityCustomized
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.GatewayClients.GatewayClient
import com.example.sw0b_001.Models.GatewayClients.GatewayClientViewModel
import com.example.sw0b_001.Models.RecentsViewModel
import com.example.sw0b_001.R
import com.example.sw0b_001.Security.SecurityHandler
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class GatewayClientListingActivity : AppCompactActivityCustomized() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gateway_clients_available)

        val myToolbar = findViewById<MaterialToolbar>(R.id.gateway_client_toolbar)
        setSupportActionBar(myToolbar)

        val listView = findViewById<ListView>(R.id.gateway_clients_recycler_view)

        val gatewayClientsViewModel =
                ViewModelProvider(this)[GatewayClientViewModel::class.java]

        gatewayClientsViewModel.get(applicationContext).observe(this, Observer {
            val listViewAdapter = GatewayClientListingAdapter(it)
            listView.adapter = listViewAdapter
        })
    }

    class GatewayClientListingAdapter(private var gatewayClientsList: List<GatewayClient>)
        : BaseAdapter() {
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
            var view = convertView
            if(view == null) {
                val inflater: LayoutInflater = LayoutInflater.from(parent?.context);
                view = inflater.inflate(R.layout.layout_cardlist_gateway_clients, parent,
                        false)
            }

            view?.findViewById<MaterialTextView>(R.id.gateway_client_MSISDN)?.text =
                    getItem(position).msisdn

            return view!!
        }

    }
}