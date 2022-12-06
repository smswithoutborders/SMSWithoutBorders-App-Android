package com.example.sw0b_001.SettingsActivities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.AppCompactActivityCustomized;
import com.example.sw0b_001.AddNewGatewayActivity
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsRecyclerAdapter;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.R;
import com.example.sw0b_001.databinding.ActivityGatewayClientsSettingsBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GatewayClientsSettingsActivity extends AppCompactActivityCustomized {
    public RecyclerView gatewayClientRecyclerView;
    public GatewayClientsRecyclerAdapter gatewayClientsRecyclerAdapter;
    public List<GatewayClient> listOfGateways = new ArrayList<>();
    public int gatewayClientsLayout;

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

        try {
            refreshGatewayClientsSettings();
            this.gatewayClientsRecyclerAdapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.gatewayClientsLayout = R.layout.layout_cardlist_gateway_clients;
        this.gatewayClientRecyclerView = findViewById(R.id.gateway_clients_recycler_view);
        this.gatewayClientRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // gatewayRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        this.gatewayClientsRecyclerAdapter = new GatewayClientsRecyclerAdapter( getApplicationContext(), this);
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
        this.listOfGateways = GatewayClientsHandler.getAllGatewayClients(getApplicationContext());
        this.gatewayClientRecyclerView.setAdapter(this.gatewayClientsRecyclerAdapter);
    }

    public void refreshGatewayClientsSettings() throws InterruptedException {
        List<GatewayServer> gatewayServerList = GatewayServersHandler.getAllGatewayServers(getApplicationContext());
        GatewayClientsHandler.clearStoredGatewayClients(getApplicationContext());

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
            GatewayClientsHandler.remoteFetchAndStoreGatewayClients(getApplicationContext(), gatewayServerSeedsUrl, callbackFunction);
        }
    }

    public void onRefreshButton(View view) throws InterruptedException {
        LinearProgressIndicator linearProgressIndicator = findViewById(R.id.refresh_loader);
        linearProgressIndicator.setVisibility(View.VISIBLE);

        this.refreshGatewayClientsSettings();
        this.gatewayClientsRecyclerAdapter.notifyDataSetChanged();

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("", "Plus button clicked");
        ConstraintLayout newGatewayClientConstrain = findViewById(R.id.new_gateway_client_constraint);
        TextInputEditText gatewayClientInput = findViewById(R.id.new_gateway_client_text_input);

        switch(item.getItemId()) {
            case R.id.add_gateway:

                Intent addGatewayIntent = new Intent(getApplicationContext(), AddNewGatewayActivity.class);
                startActivity(addGatewayIntent);

                break;
        }
        return false;
    }
}
