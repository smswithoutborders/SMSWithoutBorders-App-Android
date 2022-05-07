package com.example.sw0b_001.Models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.GatewayClientsSettingsActivity;
import com.example.sw0b_001.R;
import com.example.sw0b_001.SettingsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.ViewHolder> {

    Context context;
    List<String> listOfSettings;
    int settingsRenderLayout;

    public SettingsRecyclerAdapter(Context context, List<String> listOfSettings, int settingsRenderLayout){
        this.context = context;
        this.listOfSettings = listOfSettings;
        this.settingsRenderLayout = settingsRenderLayout;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.settingsRenderLayout, parent, false);
        return new SettingsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String settings = this.listOfSettings.get(position);
        holder.name.setText(settings);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settings.equals(SettingsActivity.GATEWAY_CLIENT_SETTINGS)) {
                    Intent gatewayClientsIntent = new Intent(context, GatewayClientsSettingsActivity.class);
                    context.startActivity(gatewayClientsIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.listOfSettings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ConstraintLayout layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.settings_card_layout);
            this.name = itemView.findViewById(R.id.settings_item_text);
        }
    }
}
