package com.example.sw0b_001.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.R;
import com.example.sw0b_001.SettingsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.ViewHolder> {

    Context context;
    List<String> settings;
    int settingRenderLayout;
    SettingsActivity settingsActivity;

    public SettingsRecyclerAdapter(Context context, List<String> settings, int settingRenderLayout, SettingsActivity settingsActivity){
        this.context = context;
        this.settings = settings;
        this.settingRenderLayout = settingRenderLayout;
        this.settingsActivity = settingsActivity;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.settingRenderLayout, parent, false);
        return new SettingsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String setting = this.settings.get(position);
        holder.name.setText(setting);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.settings.size();
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
