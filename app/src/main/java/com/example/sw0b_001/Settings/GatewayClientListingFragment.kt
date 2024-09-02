package com.example.sw0b_001.Settings

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.Models.GatewayClients.GatewayClient
import com.example.sw0b_001.Models.GatewayClients.GatewayClientAddModalFragment
import com.example.sw0b_001.Models.GatewayClients.GatewayClientViewModel
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsCommunications
import com.example.sw0b_001.Models.Platforms.PlatformsViewModel
import com.example.sw0b_001.Models.ThreadExecutorPool
import com.example.sw0b_001.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView


class GatewayClientListingFragment : Fragment(R.layout.activity_gateway_clients_available) {

    private lateinit var listViewAdapter: GatewayClientListingAdapter

    private val viewModel: GatewayClientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.gateway_clients_recycler_view)

        val linearProgressIndicator = view.findViewById<LinearProgressIndicator>(R.id.refresh_loader)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)

        refreshLayout.isRefreshing = true
        linearProgressIndicator.visibility = View.VISIBLE

        val gatewayClient = GatewayClientsCommunications(requireContext())

        viewModel.get(requireContext()) {
            activity?.runOnUiThread {
                linearProgressIndicator.visibility = View.GONE
                refreshLayout.isRefreshing = false
            }
        }.observe(this, Observer {
            listViewAdapter = GatewayClientListingAdapter(gatewayClient, it)
            listView.adapter = listViewAdapter
        })

        view.findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)
            .setOnRefreshListener { refresh(view) }

        gatewayClient.sharedPreferences
            .registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.gateway_client_settings_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if(menuItem.itemId == R.id.gateway_client_menu_refresh) {
                    refresh(view)
                    return true
                }
                else if(menuItem.itemId == R.id.gateway_client_menu_add_contact) {
                    activity?.let {
                        val fragmentTransaction = it.supportFragmentManager.beginTransaction()
                        val gatewayClientAddFragment = GatewayClientAddModalFragment()
                        fragmentTransaction.add(gatewayClientAddFragment, "gateway_client_add_tag")
                        fragmentTransaction.show(gatewayClientAddFragment)
                        fragmentTransaction.commitNow()
                        return true
                    }
                    return false
                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private val sharedPreferencesChangeListener = OnSharedPreferenceChangeListener { _, _ ->
        if(::listViewAdapter.isInitialized)
            listViewAdapter.notifyDataSetChanged()
    }

    private fun refresh(view: View) {
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.gateway_client_swipe_refresh)
        refreshLayout.isRefreshing = true

        val linearProgressIndicator = view.findViewById<LinearProgressIndicator>(R.id.refresh_loader)
        linearProgressIndicator.visibility = View.VISIBLE

        viewModel.loadRemote(requireContext(), {
            refreshLayout.isRefreshing = false
        } ) {
            activity?.runOnUiThread {
                refreshLayout.isRefreshing = false
                linearProgressIndicator.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to refresh...",
                        Toast.LENGTH_SHORT).show()
            }
        }

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