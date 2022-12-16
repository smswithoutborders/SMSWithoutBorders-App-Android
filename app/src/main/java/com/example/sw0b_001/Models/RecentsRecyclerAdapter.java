package com.example.sw0b_001.Models;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.Platforms.Platform;
import com.example.sw0b_001.Models.Platforms.PlatformsHandler;
import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class RecentsRecyclerAdapter extends RecyclerView.Adapter<RecentsRecyclerAdapter.ViewHolder> {

    Context context;
    List<EncryptedContent> listOfRecents;
    int recentsRenderLayout;

    public RecentsRecyclerAdapter(Context context, List<EncryptedContent> listOfRecents, int recentsRenderLayout){
        this.context = context;
        this.listOfRecents = listOfRecents;
        this.recentsRenderLayout = recentsRenderLayout;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.recentsRenderLayout, parent, false);
        return new RecentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        EncryptedContent encryptedContent = listOfRecents.get(position);

        int trimLength = 90;
        String displayString = encryptedContent.getEncryptedContent().length() > trimLength ?
                encryptedContent.getEncryptedContent().substring(0, trimLength) + "..." :
                encryptedContent.getEncryptedContent();

        holder.encryptedTextSnippet.setText(displayString);

        Platform platform = PlatformsHandler.getPlatform(this.context,
                encryptedContent.getPlatformName());

        holder.platformLogo.setImageResource(
                (int) PlatformsHandler.hardGetLogoByName(context, platform.getName()));

        Date date = new Date(encryptedContent.getDate());
        if(DateUtils.isToday(encryptedContent.getDate())) {
            DateFormat format = new SimpleDateFormat("HH:mm a");
            holder.date.setText(format.format(date));
        }
        else {
            DateFormat format = new SimpleDateFormat("MMMM dd");

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);

            holder.date.setText(format.format(calendar.getTime()));
        }

        if (DateUtils.isToday(encryptedContent.getDate())) {
            holder.date.setText("Today");
        }
        else {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd");
            holder.date.setText(dateFormat.format(encryptedContent.getDate()));
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent platformIntent = PlatformsHandler.getIntent(context, platform.getName(), platform.getType());
                platformIntent.putExtra("encrypted_content_id", encryptedContent.getId());
                platformIntent.putExtra("platform_id", platform.getId());
                context.startActivity(platformIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.listOfRecents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView encryptedTextSnippet;
        TextView date;
        ImageView platformLogo;
        ConstraintLayout layout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.recents_card_layout);
            this.date = itemView.findViewById(R.id.recent_date);
            this.encryptedTextSnippet = itemView.findViewById(R.id.encryptedTextSnippet);
            this.platformLogo = itemView.findViewById(R.id.recents_platform_logo);
        }
    }
}
