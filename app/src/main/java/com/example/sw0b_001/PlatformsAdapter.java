package com.example.sw0b_001;

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

import com.example.sw0b_001.ListPlatforms.EmailActivities_Recent;

import org.jetbrains.annotations.NotNull;

public class PlatformsAdapter extends RecyclerView.Adapter<PlatformsAdapter.ViewHolder> {

    String platforms[], descriptions[];
    int images[];
    Context context;

    public PlatformsAdapter(Context context, String platforms[], String descriptions[], int images[]){
        this.context = context;
        this.platforms = platforms;
        this.descriptions = descriptions;
        this.images = images;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.platforms_listed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.platform.setText(this.platforms[position]);
        holder.description.setText(this.descriptions[position]);
        holder.image.setImageResource(this.images[position]);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EmailActivities_Recent.class);
                intent.putExtra("platform", platforms[position]);
                intent.putExtra("description", descriptions[position]);
                intent.putExtra("image", images[position]);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView platform, description;
        ImageView image;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            platform = itemView.findViewById(R.id.platform_name);
            description = itemView.findViewById(R.id.platform_description);
            image = itemView.findViewById(R.id.platform_image);

            mainLayout = itemView.findViewById(R.id.platforms_layout);
        }
    }
}
