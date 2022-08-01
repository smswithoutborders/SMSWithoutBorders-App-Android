package com.example.sw0b_001.Models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.R;
import com.example.sw0b_001.SettingsActivities.GatewayClientsSettingsActivity;
import com.example.sw0b_001.SettingsActivities.LanguageSettingsActivity;
import com.example.sw0b_001.SettingsActivities.SettingsActivity;
import com.example.sw0b_001.SettingsActivities.StoreAccessSettingsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.ViewHolder> {

    Context context;
    List<String> listOfSettings;
    int settingsRenderLayout;
    Activity activity;

    public SettingsRecyclerAdapter(Context context, List<String> listOfSettings, int settingsRenderLayout, Activity activity){
        this.context = context;
        this.listOfSettings = listOfSettings;
        this.settingsRenderLayout = settingsRenderLayout;
        this.activity = activity;
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
        holder.settingsItemImage.setImageResource(SettingsActivity.SETTINGS_ICON_MAPPER.get(settings));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settings.equals(SettingsActivity.GATEWAY_CLIENT_SETTINGS)) {
                    Intent gatewayClientsIntent = new Intent(context, GatewayClientsSettingsActivity.class);
                    context.startActivity(gatewayClientsIntent);
                }
                else if(settings.equals(SettingsActivity.STORED_ACCESS_SETTINGS)) {
                    Intent storeAccessIntent = new Intent(context, StoreAccessSettingsActivity.class);
                    context.startActivity(storeAccessIntent);
                }
                else if(settings.equals(SettingsActivity.LANGUAGE_SETTINGS)) {
                    Intent storeAccessIntent = new Intent(context, LanguageSettingsActivity.class);
                    context.startActivity(storeAccessIntent);
                    activity.finish();
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
        ImageView settingsItemImage;
        ConstraintLayout layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.settings_card_layout);
            this.name = itemView.findViewById(R.id.settings_item_text);
            this.settingsItemImage = itemView.findViewById(R.id.settings_item_img);
        }
    }
}
