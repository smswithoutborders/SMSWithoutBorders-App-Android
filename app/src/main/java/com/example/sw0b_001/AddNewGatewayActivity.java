package com.example.sw0b_001;



import static com.example.sw0b_001.R.id.new_gateway_toolbar;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.example.sw0b_001.Models.AppCompactActivityRtlEnabled;

public class AddNewGatewayActivity extends AppCompactActivityRtlEnabled {
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
}




