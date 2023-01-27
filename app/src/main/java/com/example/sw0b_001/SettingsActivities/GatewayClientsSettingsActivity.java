package com.example.sw0b_001.SettingsActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.AppCompactActivityCustomized;
import com.example.sw0b_001.AddNewGatewayActivity;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsRecyclerAdapter;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.R;
import com.example.sw0b_001.databinding.ActivityGatewayClientsSettingsBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class GatewayClientsSettingsActivity extends AppCompactActivityCustomized {

    private ActivityGatewayClientsSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGatewayClientsSettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar gatewayClientToolbar = (Toolbar) findViewById(R.id.gateway_client_toolbar);
        setSupportActionBar(gatewayClientToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        LinearProgressIndicator linearProgressIndicator = findViewById(R.id.refresh_loader);
        linearProgressIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getLocalClassName(), "Resuming");
        try {
            populateSettings();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        populateOperatorId();
        try {
            populateSettings();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void populateOperatorId() {
        TextView operatorIdTextView = findViewById(R.id.operator_id_text);
        String operatorId = GatewayClientsHandler.getOperatorId(getApplicationContext());

        String operatorIdText = operatorIdTextView.getText().toString() + " " + operatorId;
        operatorIdTextView.setText(operatorIdText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gateway_client_settings_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void populateSettings() throws InterruptedException {
        List<GatewayClient> listOfGateways = GatewayClientsHandler.getAllGatewayClients(
                getApplicationContext());

        RecyclerView gatewayClientRecyclerView;
        GatewayClientsRecyclerAdapter gatewayClientsRecyclerAdapter;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        gatewayClientRecyclerView = findViewById(R.id.gateway_clients_recycler_view);
        gatewayClientRecyclerView.setLayoutManager(linearLayoutManager);

        gatewayClientsRecyclerAdapter = new GatewayClientsRecyclerAdapter(
                getApplicationContext(), listOfGateways,
                R.layout.layout_cardlist_gateway_clients, this);

        gatewayClientRecyclerView.setAdapter(gatewayClientsRecyclerAdapter);
    }

    public void refreshGatewayClientsSettings() throws InterruptedException {
        List<GatewayServer> gatewayServerList = GatewayServersHandler.getAllGatewayServers(
                getApplicationContext());

        Runnable callbackFunction = new Runnable() {
            @Override
            public void run() {
                try {
                    populateSettings();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LinearProgressIndicator linearProgressIndicator = findViewById(R.id.refresh_loader);
                linearProgressIndicator.setVisibility(View.INVISIBLE);
            }
        };

        for(GatewayServer gatewayServer : gatewayServerList) {
            String gatewayServerSeedsUrl = gatewayServer.getSeedsUrl();
            GatewayClientsHandler.remoteFetchAndStoreGatewayClients(getApplicationContext());
        }
    }

    public void onRefreshButton(View view) throws InterruptedException {
        LinearProgressIndicator linearProgressIndicator = findViewById(R.id.refresh_loader);
        linearProgressIndicator.setVisibility(View.VISIBLE);

        this.refreshGatewayClientsSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_gateway:
                Intent addGatewayIntent = new Intent(getApplicationContext(), AddNewGatewayActivity.class);
                startActivity(addGatewayIntent);
                break;
        }
        return false;
    }
}
