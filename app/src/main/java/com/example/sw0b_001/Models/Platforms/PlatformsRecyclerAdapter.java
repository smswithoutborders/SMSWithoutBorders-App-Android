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
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.EmailComposeActivity;
import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlatformsRecyclerAdapter extends RecyclerView.Adapter<PlatformsRecyclerAdapter.ViewHolder> {

    public Context context;
    public List<Platforms> platforms;
    public int platformRenderLayout;

    public PlatformsRecyclerAdapter(Context context, List<Platforms> platforms, int layout){
        this.context = context;
        this.platforms = platforms;
        this.platformRenderLayout = layout;
    }

    public void update(List<Platforms> platforms) {
        this.platforms = platforms;
        this.notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.platformRenderLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {
        Platforms platform = this.platforms.get(position);
        viewHolder.name.setText(platform.getName());
        // viewHolder.image.setImageResource(platform.getLogo());

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // Intent intent = Platforms.getIntent(context.getApplicationContext(), platform.getProvider(), platform.getName());
                Intent intent = Platforms.getIntent(context.getApplicationContext(), platform.getName(), platform.getType());
//                Log.i("onclick" + platform.getName(), platform.getType());
                intent.putExtra("platform_id", platform.getId());
                context.startActivity(intent);

                 */
                Log.d(getClass().getName(), "Got clicked");

                // FIX change to dynamic intent navigation
                Intent emailComposeIntent = new Intent(context.getApplicationContext(), EmailComposeActivity.class);
                context.startActivity(emailComposeIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.platforms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        ConstraintLayout layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.platform_card_layout);
            this.name = itemView.findViewById(R.id.platform_name);
            this.image = itemView.findViewById(R.id.platform_logo);
        }
    }
}
