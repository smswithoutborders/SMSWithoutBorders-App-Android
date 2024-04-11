package com.example.sw0b_001.Models.Platforms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.EmailComposeActivity;
import com.example.sw0b_001.R;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlatformsRecyclerAdapter extends RecyclerView.Adapter<PlatformsRecyclerAdapter.ViewHolder> {

    public final AsyncListDiffer<Platforms> mDiffer =
            new AsyncListDiffer(this, Platforms.DIFF_CALLBACK);

    public PlatformsRecyclerAdapter(){ }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_cardlist_platforms, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {
        Platforms platforms = mDiffer.getCurrentList().get(position);
        viewHolder.bind(platforms);
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;

        MaterialCardView cardView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.platform_card_layout);
            this.name = itemView.findViewById(R.id.platform_name);
            this.image = itemView.findViewById(R.id.platform_logo);
        }

        public void bind(Platforms platforms) {
            name.setText(platforms.getName());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(PlatformsRecyclerAdapter.class.getName(), "Yes clicked");
                    if(platforms.getType().equals("email")) {
                        Intent intent = new Intent(v.getContext(), EmailComposeActivity.class);
                        intent.putExtra(EmailComposeActivity.INTENT_PLATFORM_ID, platforms.getId());
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
