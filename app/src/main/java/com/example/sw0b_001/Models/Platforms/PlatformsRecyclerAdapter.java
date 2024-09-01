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

import com.example.sw0b_001.Modals.AvailablePlatformsModalFragment;
import com.example.sw0b_001.R;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlatformsRecyclerAdapter extends
        RecyclerView.Adapter<PlatformsRecyclerAdapter.ViewHolder> {

    public Boolean isClickable = true;

    public final AsyncListDiffer<AvailablePlatforms> availableMDiffer =
            new AsyncListDiffer(this, AVAILABLE_DIFF_CALLBACK);

    public final AsyncListDiffer<StoredPlatformsEntity> storedMDiffer =
            new AsyncListDiffer(this, STORED_DIFF_CALLBACK);

    public AvailablePlatformsModalFragment.Type type;

    public List<AvailablePlatforms> availablePlatforms;

    public MutableLiveData<AvailablePlatforms> availablePlatformsMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<StoredPlatformsEntity> savedPlatformsMutableData = new MutableLiveData<>();

    public PlatformsRecyclerAdapter(AvailablePlatformsModalFragment.Type type) {
        this.type = type;
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
        if(type == AvailablePlatformsModalFragment.Type.AVAILABLE) {
            AvailablePlatforms platforms = availableMDiffer.getCurrentList().get(position);
            viewHolder.bind(platforms);

            viewHolder.cardView.setOnClickListener(it -> {
                if(isClickable) {
                    availablePlatformsMutableLiveData.setValue(platforms);
                }
            });
        }
        if(type == AvailablePlatformsModalFragment.Type.SAVED) {
            StoredPlatformsEntity platforms = storedMDiffer.getCurrentList().get(position);
            for(AvailablePlatforms platform : this.availablePlatforms) {
                if(platforms.getName().equals(platform.getName())) {
                    viewHolder.bind(platform);
                    break;
                }
            }
            viewHolder.cardView.setOnClickListener(it -> {
                if(isClickable) {
                    savedPlatformsMutableData.setValue(platforms);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(type == AvailablePlatformsModalFragment.Type.AVAILABLE) {
            return availableMDiffer.getCurrentList().size();
        } else {
            return storedMDiffer.getCurrentList().size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        MaterialCardView cardView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.platforms_thumbnails_card);
            this.image = itemView.findViewById(R.id.platforms_thumbnails);
        }

        public void bind(AvailablePlatforms platforms) {
            if(platforms.getLogo() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(platforms.getLogo(), 0,
                        platforms.getLogo().length);
                image.setImageBitmap(bitmap);
            }

        }
    }

    public static final DiffUtil.ItemCallback<AvailablePlatforms> AVAILABLE_DIFF_CALLBACK =
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

    public static final DiffUtil.ItemCallback<StoredPlatformsEntity> STORED_DIFF_CALLBACK =
            new DiffUtil.ItemCallback<StoredPlatformsEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull StoredPlatformsEntity oldItem,
                                               @NonNull StoredPlatformsEntity newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull StoredPlatformsEntity oldItem,
                                                  @NonNull StoredPlatformsEntity newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
