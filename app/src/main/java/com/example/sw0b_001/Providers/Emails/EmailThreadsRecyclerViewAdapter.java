package com.example.sw0b_001.Providers.Emails;

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

import com.example.sw0b_001.EmailThreadActivity;
import com.example.sw0b_001.EmailThreadsActivity;
import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmailThreadsRecyclerViewAdapter extends RecyclerView.Adapter<EmailThreadsRecyclerViewAdapter.ViewHolder> {

    Context context;
    int layout;
    long platformId;
    List<EmailThreads> threads;
    EmailThreadsActivity emailThreadsActivity;

    public EmailThreadsRecyclerViewAdapter(Context context, List<EmailThreads> threads, int intendedLayout, long platformId, EmailThreadsActivity emailThreadsActivity) {
        this.context = context;
        this.threads = threads;
        this.layout = intendedLayout;
        this.platformId = platformId;
        this.emailThreadsActivity = emailThreadsActivity;
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
        EmailThreads thread = this.threads.get(position);
        holder.threadSubject.setText(this.threads.get(position).getSubject());
        holder.threadSubjectSub.setText(this.threads.get(position).getRecipient());
        holder.threadBottomRightText.setText("");
        holder.threadTopRightText.setText(this.threads.get(position).getMdate());
        holder.image.setImageResource(this.threads.get(position).getImage());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EmailThreadActivity.class);
                intent.putExtra("thread_id", thread.getId());
                intent.putExtra("platform_id", platformId);
                context.startActivity(intent);
                emailThreadsActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.threads.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView threadSubject, threadSubjectSub, threadTopRightText, threadBottomRightText;
        ImageView image;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            threadSubject = itemView.findViewById(R.id.subject);
            threadSubjectSub = itemView.findViewById(R.id.subjectSub);
            threadTopRightText = itemView.findViewById(R.id.topRight);
            threadBottomRightText = itemView.findViewById(R.id.bottomRight);
            image = itemView.findViewById(R.id.image);
            mainLayout = itemView.findViewById(R.id.email_thread_card_layout);
        }
    }
}
