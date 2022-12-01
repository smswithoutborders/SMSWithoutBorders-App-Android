package com.example.sw0b_001;



import static com.example.sw0b_001.R.id.new_gateway_toolbar;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.sw0b_001.Models.AppCompactActivityRtlEnabled;
import com.example.sw0b_001.Models.GatewayClients.GatewayClient;
import com.example.sw0b_001.Models.GatewayClients.GatewayClientsHandler;
import com.example.sw0b_001.SettingsActivities.GatewayClientsSettingsActivity;
import com.google.android.material.textfield.TextInputEditText;

public class AddNewGatewayActivity extends AppCompactActivityRtlEnabled {

    GatewayClientsSettingsActivity gatewayClientsSettingsActivity = new GatewayClientsSettingsActivity();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gateway);

        Toolbar addNewGateway = findViewById(new_gateway_toolbar);
        setSupportActionBar(addNewGateway);

//         Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }


    public void onAddNewGatewayClient(View view ) throws InterruptedException {
        TextInputEditText gatewayClientNumberText = findViewById(R.id.new_gateway_client_text_input);
        String newGatewayClientNumber = gatewayClientNumberText.getText().toString();

        if(newGatewayClientNumber.isEmpty()) {
            gatewayClientNumberText.setError(getString(R.string.gateway_client_settings_add_custom_empty_error));
            return;
        }

        GatewayClient gatewayClient = new GatewayClient();
        gatewayClient.setType("custom");
        gatewayClient.setMSISDN(newGatewayClientNumber);

        GatewayClientsHandler.add(getApplicationContext(), gatewayClient);

    }
}




