package com.example.sw0b_001.Models.Notifications;

import static com.example.sw0b_001.Models.RecentsRecyclerAdapter.DIFF_CALLBACK;

import android.content.Context;
import android.text.format.DateUtils;
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

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

        holder.type.setText(notifications.message);
        holder.message.setText(notifications.message);
//        holder.date.setText(notifications.date);

        Date date = new Date(notifications.date);
        if(DateUtils.isToday(notifications.date)) {
            DateFormat format = new SimpleDateFormat("HH:mm a");
            holder.date.setText(format.format(date));
        }
        else {
            DateFormat format = new SimpleDateFormat("MMMM dd");

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);

            holder.date.setText(format.format(calendar.getTime()));
        }

        if (DateUtils.isToday(notifications.date)) {
            holder.date.setText("Today");
        }
        else {
            DateFormat dateFormat = new SimpleDateFormat("MMM dd");
            holder.date.setText(dateFormat.format(notifications.date));
        }
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
        TextView type;
        TextView date;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.type = itemView.findViewById(R.id.notification_text);
            this.message = itemView.findViewById(R.id.notification_summary);
            this.date = itemView.findViewById(R.id.notification_date);
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
