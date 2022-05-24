package com.example.sw0b_001;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsRecyclerAdapter;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class GatewayClientsSettingsActivity extends AppCompatActivity {
    public RecyclerView gatewayClientRecyclerView;
    public GatewayClientsRecyclerAdapter gatewayClientsRecyclerAdapter;
    public List<GatewayClient> listOfGateways = new ArrayList<>();
    public int gatewayClientsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gateway_clients_settings);

        Toolbar gatewayClientToolbar = (Toolbar) findViewById(R.id.gateway_client_toolbar);
        setSupportActionBar(gatewayClientToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        LinearProgressIndicator linearProgressIndicator = findViewById(R.id.refresh_loader);
        linearProgressIndicator.setVisibility(View.INVISIBLE);

        this.gatewayClientsLayout = R.layout.layout_cardlist_gateway_clients;
        this.gatewayClientRecyclerView = findViewById(R.id.gateway_clients_recycler_view);
        this.gatewayClientRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // gatewayRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.gatewayClientsRecyclerAdapter = new GatewayClientsRecyclerAdapter( getApplicationContext(), this);
        populateSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gateway_client_settings_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void populateSettings() {
        this.listOfGateways = GatewayClientsHandler.getAllGatewayClients(getApplicationContext());
        Log.d(getLocalClassName(), "# of listed gateway clients: " + this.listOfGateways.size());
        this.gatewayClientRecyclerView.setAdapter(this.gatewayClientsRecyclerAdapter);
    }

    public void refreshGatewayClientsSettings() throws InterruptedException {
        List<GatewayServer> gatewayServerList = GatewayServersHandler.getAllGatewayServers(getApplicationContext());
        GatewayClientsHandler.clearStoredGatewayClients(getApplicationContext());

        Runnable callbackFunction = new Runnable() {
            @Override
            public void run() {
                populateSettings();
                LinearProgressIndicator linearProgressIndicator = findViewById(R.id.refresh_loader);
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            }
        };

        for(GatewayServer gatewayServer : gatewayServerList) {
            String gatewayServerSeedsUrl = gatewayServer.getSeedsUrl();
            GatewayClientsHandler.remoteFetchAndStoreGatewayClients(getApplicationContext(), gatewayServerSeedsUrl, callbackFunction);
        }
    }

    public void onRefreshButton(View view) throws InterruptedException {
        // TODO put a loader here
        LinearProgressIndicator linearProgressIndicator = findViewById(R.id.refresh_loader);
        linearProgressIndicator.setVisibility(View.VISIBLE);

        this.refreshGatewayClientsSettings();
        this.gatewayClientsRecyclerAdapter.notifyDataSetChanged();

    }
}
