package com.example.sw0b_001.Models.GatewayClients;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Helpers.CustomHelpers;
import com.example.sw0b_001.SettingsActivities.GatewayClientsSettingsActivity;
import com.example.sw0b_001.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GatewayClientsRecyclerAdapter extends RecyclerView.Adapter<GatewayClientsRecyclerAdapter.ViewHolder> {

    Context context;
    List<GatewayClient> gatewayClientList;
    int renderLayout;
    GatewayClientsSettingsActivity gatewayClientsSettingsActivity;

    public GatewayClientsRecyclerAdapter(Context context, List<GatewayClient> gatewayClientList,
                                         int renderLayout, GatewayClientsSettingsActivity gatewayClientsSettingsActivity){
        this.context = context;
        this.gatewayClientList = gatewayClientList;
        this.renderLayout = renderLayout;
        this.gatewayClientsSettingsActivity = gatewayClientsSettingsActivity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.renderLayout, parent, false);
        return new GatewayClientsRecyclerAdapter.ViewHolder(view);
    }

    public void handleCheckedSwitch(GatewayClient gatewayClient) {
        gatewayClient.setDefault(true);
        try {
            GatewayClientsHandler.toggleDefault(context, gatewayClient);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        GatewayClient gatewayClient = this.gatewayClientList.get(position);

        holder.MSISDN.setText(gatewayClient.getMSISDN());
        if(gatewayClient.getType() == null)
            holder.country.setText(gatewayClient.getCountry());
        else {
            holder.country.setText(gatewayClient.getType());
        }

        holder.operatorName.setText(gatewayClient.getOperatorName());

        holder.switchBtn.setChecked(gatewayClient.isDefault());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckedSwitch(gatewayClient);
                try {
                    gatewayClientsSettingsActivity.populateSettings();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    handleCheckedSwitch(gatewayClient);
                    try {
                        gatewayClientsSettingsActivity.populateSettings();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.gatewayClientList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView MSISDN;
        TextView country;
        TextView operatorName;
        SwitchMaterial switchBtn;
        ConstraintLayout layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.gateway_client_card_layout);
            this.MSISDN = itemView.findViewById(R.id.gateway_client_MSISDN);
            this.country = itemView.findViewById(R.id.gateway_client_country);
            this.operatorName = itemView.findViewById(R.id.gateway_client_operator_name);
            this.switchBtn = itemView.findViewById(R.id.gateway_client_radio_btn);
        }
    }
}
