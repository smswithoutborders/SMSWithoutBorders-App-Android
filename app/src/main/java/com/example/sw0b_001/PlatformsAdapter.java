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

import org.jetbrains.annotations.NotNull;

public class PlatformsAdapter extends RecyclerView.Adapter<PlatformsAdapter.ViewHolder> {

    String text1s[], text2s[], text3s[];
    int images[];
    Context context;
    Intent onclickIntent;
    int layout;

    public PlatformsAdapter(Context context, String text1s[], String text2s[], int images[], Intent onclickIntent){
        this.context = context;
        this.text1s = text1s;
        this.text2s = text2s;
        this.images = images;
        this.layout = R.layout.activity_cardlist;
        this.onclickIntent = onclickIntent;
    }

    public PlatformsAdapter(Context context, String text1s[], String text2s[], String text3s[], int images[], Intent onclickIntent, int layout){
        this.context = context;
        this.text1s = text1s;
        this.text2s = text2s;
        this.text3s = text3s;
        this.images = images;
        this.layout = layout;
        this.onclickIntent = onclickIntent;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.text1.setText(this.text1s[position]);
        holder.text2.setText(this.text2s[position]);
        if( this.text3s != null)
            holder.text3.setText(this.text3s[position]);
        holder.image.setImageResource(this.images[position]);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, onclickIntent);
                onclickIntent.putExtra("text1", text1s[position]);
                onclickIntent.putExtra("text2", text2s[position]);
                if( text3s != null)
                    onclickIntent.putExtra("text3", text3s[position]);
                onclickIntent.putExtra("image", images[position]);

                context.startActivity(onclickIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text1, text2, text3;
        ImageView image;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            if(itemView.findViewById(R.id.text3) != null ) {
                text3 = itemView.findViewById(R.id.text3);
            }
            image = itemView.findViewById(R.id.platform_image);

            mainLayout = itemView.findViewById(R.id.platforms_layout);
        }
    }
}
