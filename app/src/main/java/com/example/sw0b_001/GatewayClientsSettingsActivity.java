package com.example.sw0b_001;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GatewayClientsSettingsActivity extends AppCompatActivity {

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

        populateSettings();
    }

    public void populateSettings() {
        List<GatewayClient> listOfGateways = new ArrayList<>();

        RecyclerView gatewayRecyclerView = findViewById(R.id.gateway_clients_recycler_view);
        // settingsRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        GatewayClientsRecyclerAdapter gatewayClientRecyclerAdapter = new GatewayClientsRecyclerAdapter(this, listOfGateways, R.layout.layout_cardlist_gateway_clients);
        gatewayRecyclerView.setAdapter(gatewayClientRecyclerAdapter);
        gatewayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
