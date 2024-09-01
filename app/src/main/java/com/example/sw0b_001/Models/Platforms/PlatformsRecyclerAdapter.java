package com.example.sw0b_001.Models.Platforms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.R;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class PlatformsRecyclerAdapter extends
        RecyclerView.Adapter<PlatformsRecyclerAdapter.ViewHolder> {

    public Boolean isClickable = true;
    public final AsyncListDiffer<AvailablePlatforms> mDiffer =
            new AsyncListDiffer(this, DIFF_CALLBACK);

    FragmentTransaction fragmentTransaction;
    Runnable onClickListenerCallback;

    public MutableLiveData<AvailablePlatforms> availablePlatformsMutableLiveData = new MutableLiveData<>();

    public PlatformsRecyclerAdapter(FragmentTransaction fragmentTransaction){
        this.fragmentTransaction = fragmentTransaction;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_platforms_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder viewHolder, int position) {
        AvailablePlatforms platforms = mDiffer.getCurrentList().get(position);
        viewHolder.bind(platforms, this.onClickListenerCallback);

        viewHolder.cardView.setOnClickListener(it -> {
            if(isClickable) {
                availablePlatformsMutableLiveData.setValue(platforms);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        MaterialCardView cardView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.platforms_thumbnails_card);
            this.image = itemView.findViewById(R.id.platforms_thumbnails);
        }

        public void bind(AvailablePlatforms platforms, Runnable onClickListenerCallback) {
            if(platforms.getLogo() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(platforms.getLogo(), 0,
                        platforms.getLogo().length);
                image.setImageBitmap(bitmap);
            }

        }
    }

    public static final DiffUtil.ItemCallback<AvailablePlatforms> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AvailablePlatforms>() {
                @Override
                public boolean areItemsTheSame(@NonNull AvailablePlatforms oldItem,
                                               @NonNull AvailablePlatforms newItem) {
                    return oldItem.getName().equals(newItem.getName());
                }

                @Override
                public boolean areContentsTheSame(@NonNull AvailablePlatforms oldItem,
                                                  @NonNull AvailablePlatforms newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
