package com.example.sw0b_001.Models.EncryptedContent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.Platforms.Platforms;
import com.example.sw0b_001.Models.Platforms._PlatformsHandler;
import com.example.sw0b_001.Modules.Helpers;
import com.example.sw0b_001.R;
import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder> {
    public final AsyncListDiffer<EncryptedContent> mDiffer = new AsyncListDiffer(this,
            EncryptedContent.DIFF_CALLBACK);

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cardlist_recents, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        EncryptedContent encryptedContent = mDiffer.getCurrentList().get(position);
        holder.bind(encryptedContent);
    }

    @Override
    public int getItemCount() {
        return this.mDiffer.getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView encryptedTextSnippet;
        TextView date, subject;
        ImageView platformLogo;
        MaterialCardView card;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.recents_card_layout);
            date = itemView.findViewById(R.id.recent_date);
            encryptedTextSnippet = itemView.findViewById(R.id.encryptedTextSnippet);
            subject = itemView.findViewById(R.id.homepage_subject);
            platformLogo = itemView.findViewById(R.id.recents_platform_logo);
        }

        public void bind(EncryptedContent encryptedContent) {
            encryptedTextSnippet.setText(encryptedContent.getEncryptedContent());

            Platforms platforms = _PlatformsHandler.getPlatform(itemView.getContext(),
                    encryptedContent.getPlatformName());

            try {
                platformLogo.setImageResource(_PlatformsHandler.hardGetLogoByName(platforms.getName()));

                String dateStr = Helpers.INSTANCE.formatDate(itemView.getContext(),
                        encryptedContent.getDate());

                date.setText(dateStr);
                subject.setText("Sample Subject");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
