package com.example.sw0b_001.SettingsActivities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.AddNewGatewayActivity;
import com.example.sw0b_001.Models.AppCompactActivityRtlEnabled;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsRecyclerAdapter;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.R;
import com.example.sw0b_001.databinding.ActivityGatewayClientsSettingsBinding;
import com.example.sw0b_001.databinding.ActivityTweetComposeBinding;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class GatewayClientsSettingsActivity extends AppCompactActivityRtlEnabled {
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

    public void cancelNewGatewayClient(View view) {
        ConstraintLayout newGatewayClientConstrain = findViewById(R.id.new_gateway_client_constraint);
        newGatewayClientConstrain.setVisibility(View.GONE);
    }

//    public void onAddNewGatewayClient(View view ) throws InterruptedException {
//        TextInputEditText gatewayClientNumberText = findViewById(R.id.new_gateway_client_text_input);
//        String newGatewayClientNumber = gatewayClientNumberText.getText().toString();
//
//        if(newGatewayClientNumber.isEmpty()) {
//            gatewayClientNumberText.setError(getString(R.string.gateway_client_settings_add_custom_empty_error));
//            return;
//        }
//
//        GatewayClient gatewayClient = new GatewayClient();
//        gatewayClient.setType("custom");
//        gatewayClient.setMSISDN(newGatewayClientNumber);
//
//        GatewayClientsHandler.add(getApplicationContext(), gatewayClient);
//
//        cancelNewGatewayClient(view);
//        onRefreshButton(view);
//    }

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

    public void onContactsClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (1) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor contactCursor = getApplicationContext().getContentResolver().query(contactData, null, null, null, null);
                    if(contactCursor != null) {
                        if (contactCursor.moveToFirst()) {
                            int contactIndexInformation = contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            String number = contactCursor.getString(contactIndexInformation);

                            EditText numberEditText = findViewById(R.id.new_gateway_client_text_input);
                            numberEditText.setText(number);
                        }
                    }
                }
                break;
        }
    }
}
