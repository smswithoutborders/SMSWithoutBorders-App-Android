package com.example.sw0b_001.Providers.Emails;

import androidx.appcompat.app.ActionBar;
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

import com.example.sw0b_001.EmailBodyActivity;
import com.example.sw0b_001.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmailThreadRecyclerAdapter extends RecyclerView.Adapter<EmailThreadRecyclerAdapter.ViewHolder> {

    Context context;
    int layout, highlightCount;
    public List<EmailMessage> threads;
    boolean isHighlighting;
    ActionBar ab;

    public EmailThreadRecyclerAdapter(Context context, List<EmailMessage> threads, int intendedLayout, ActionBar ab) {
        this.context = context;
        this.threads = threads;
        this.layout = intendedLayout;
        this.isHighlighting = false;
        this.ab = ab;
        this.highlightCount = 0;
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
        EmailMessage message = this.threads.get(position);
        holder.threadSubject.setText(this.threads.get(position).getTo());
        holder.threadSubjectSub.setText(this.threads.get(position).getBody().length() > 20 ? this.threads.get(position).getBody().substring(0, 20) : this.threads.get(position).getBody());
        holder.threadBottomRightText.setText(this.threads.get(position).getStatus());
        holder.threadTopRightText.setText(this.threads.get(position).getDatetime());
        holder.image.setImageResource(this.threads.get(position).getImage());

        if(holder.threadBottomRightText.getText().toString().equals("requested") || holder.threadBottomRightText.getText().toString().equals("delivered"))
            holder.threadBottomRightText.setTextColor(context.getResources().getColor(R.color.success_blue, context.getTheme()));
        else if(holder.threadBottomRightText.getText().toString().equals("pending") || holder.threadBottomRightText.getText().toString().equals("not delivered"))
            holder.threadBottomRightText.setTextColor(context.getResources().getColor(R.color.pending_gray, context.getTheme()));

        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selected(v);
                return true;
            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHighlighting) {
                    if(v.isSelected())
                        deselected(v);
                    else
                        selected(v);
                }
                else {
                    Intent intent = new Intent(context, EmailBodyActivity.class);
                    intent.putExtra("thread_id", message.getThreadId());
                    intent.putExtra("message_id", message.getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    public void selected(View v) {
        v.setBackgroundColor(context.getResources().getColor(R.color.highlight_blue, context.getTheme()));
        v.setSelected(true);
        isHighlighting = true;
        v.getRootView().findViewById(R.id.action_delete).setEnabled(true);
        ++highlightCount;
    }

    public void deselected(View v) {
        v.setBackgroundColor(context.getResources().getColor(R.color.default_dark, context.getTheme()));
        v.setSelected(false);
        --highlightCount;
        if(highlightCount < 1 ) {
            isHighlighting = false;
            v.getRootView().findViewById(R.id.action_delete).setEnabled(false);
        }
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
