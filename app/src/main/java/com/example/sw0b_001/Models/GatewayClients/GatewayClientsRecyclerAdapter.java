package com.example.sw0b_001.Models.GatewayClients;

import android.content.Context;
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

import com.example.sw0b_001.GatewayClientsSettingsActivity;
import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

public class GatewayClientsRecyclerAdapter extends RecyclerView.Adapter<GatewayClientsRecyclerAdapter.ViewHolder> {

    Context context;
    GatewayClientsSettingsActivity gatewayClientsSettingsActivity;

    public GatewayClientsRecyclerAdapter(Context context, GatewayClientsSettingsActivity gatewayClientsSettingsActivity){
        this.context = context;
        this.gatewayClientsSettingsActivity = gatewayClientsSettingsActivity;
    }

    public GatewayClientsRecyclerAdapter() {
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(gatewayClientsSettingsActivity.gatewayClientsLayout, parent, false);
        return new GatewayClientsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        GatewayClient gatewayClient = this.gatewayClientsSettingsActivity.listOfGateways.get(position);
        holder.MSISDN.setText(gatewayClient.getMSISDN());

        holder.switchBtn.setChecked(gatewayClient.isDefault());

        holder.switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    gatewayClient.setDefault(true);
                    try {
                        GatewayClientsHandler.toggleDefault(context, gatewayClient);

                        gatewayClientsSettingsActivity.populateSettings();

                        notifyDataSetChanged();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                   buttonView.setChecked(true);
                }
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gatewayClient.setDefault(true);
                try {
                    GatewayClientsHandler.toggleDefault(context, gatewayClient );

                    gatewayClientsSettingsActivity.populateSettings();

                    notifyDataSetChanged();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(getClass().getName(), "# of gateway clients: " + this.gatewayClientsSettingsActivity.listOfGateways.size());
        return this.gatewayClientsSettingsActivity.listOfGateways.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView MSISDN;
        TextView country;
        TextView lastPing;
        Switch switchBtn;
        ConstraintLayout layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.gateway_client_card_layout);
            this.MSISDN = itemView.findViewById(R.id.gateway_client_MSISDN);
            this.country = itemView.findViewById(R.id.gateway_client_country);
            this.lastPing = itemView.findViewById(R.id.gateway_client_last_ping_time);
            this.switchBtn = itemView.findViewById(R.id.gateway_client_radio_btn);
        }
    }
}
