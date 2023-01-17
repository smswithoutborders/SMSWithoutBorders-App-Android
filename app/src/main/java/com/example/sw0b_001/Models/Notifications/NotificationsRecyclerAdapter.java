package com.example.sw0b_001.Models.Notifications;

import static com.example.sw0b_001.Models.RecentsRecyclerAdapter.DIFF_CALLBACK;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.Models.EncryptedContent.EncryptedContent;
import com.example.sw0b_001.Models.RecentsRecyclerAdapter;
import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.ViewHolder>{
    private final AsyncListDiffer<Notifications> mDiffer = new AsyncListDiffer(this, DIFF_CALLBACK);

    Context context;
    int renderLayout;

    public NotificationsRecyclerAdapter(Context context, int renderLayout) {
        this.context = context;
        this.renderLayout = renderLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.renderLayout, parent, false);
        return new NotificationsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifications notifications = mDiffer.getCurrentList().get(position);

        holder.message.setText(notifications.message);
    }


    @Override
    public int getItemCount() {
        return this.mDiffer.getCurrentList().size();
    }

    public void submitList(List<Notifications> notificationsList) {
        mDiffer.submitList(notificationsList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.message = itemView.findViewById(R.id.notification_text);
        }
    }

    public static final DiffUtil.ItemCallback<Notifications> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Notifications>() {
                @Override
                public boolean areItemsTheSame(@NonNull Notifications oldItem, @NonNull Notifications newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Notifications oldItem, @NonNull Notifications newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
