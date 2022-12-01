package com.example.sw0b_001;



import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.sw0b_001.Models.AppCompactActivityRtlEnabled;

public class AddNewGatewayActivity extends AppCompactActivityRtlEnabled {

    private int new_gateway_toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_gateway);

//        Toolbar addNewGateway = findViewById(new_add_gateway_toolbar);
        Toolbar addNewGateway = findViewById(new_gateway_toolbar);
        setSupportActionBar(addNewGateway);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        
    }
}

