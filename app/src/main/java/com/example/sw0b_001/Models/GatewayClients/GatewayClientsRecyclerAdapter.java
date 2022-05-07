package com.example.sw0b_001.Models.GatewayClients;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GatewayClientsRecyclerAdapter extends RecyclerView.Adapter<GatewayClientsRecyclerAdapter.ViewHolder> {

    Context context;
    List<GatewayClient> gatewayClients;
    int gatewayClientRenderLayout;

    public GatewayClientsRecyclerAdapter(Context context, List<GatewayClient> gatewayClients, int gatewayClientRenderLayout){
        this.context = context;
        this.gatewayClients = gatewayClients;
        this.gatewayClientRenderLayout = gatewayClientRenderLayout;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.gatewayClientRenderLayout, parent, false);
        return new GatewayClientsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        GatewayClient gatewayClient = this.gatewayClients.get(position);
        holder.MSISDN.setText(gatewayClient.getMSISDN());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.gatewayClients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView MSISDN;
        TextView country;
        TextView lastPing;
        ConstraintLayout layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.gateway_client_card_layout);
            this.MSISDN = itemView.findViewById(R.id.gateway_client_MSISDN);
            this.country = itemView.findViewById(R.id.gateway_client_country);
            this.lastPing = itemView.findViewById(R.id.gateway_client_last_ping_time);
        }
    }
}
