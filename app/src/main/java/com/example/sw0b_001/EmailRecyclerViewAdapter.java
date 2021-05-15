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

import com.example.sw0b_001.Providers.Emails.EmailCustomThreads;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EmailRecyclerViewAdapter extends RecyclerView.Adapter<EmailRecyclerViewAdapter.ViewHolder> {

    String text1s[], text2s[], text3s[], text4s[];
    int images[];

    Context context;
    Intent onclickIntent;
    int layout;
    ArrayList<EmailCustomThreads> threads;

    public EmailRecyclerViewAdapter(Context context, ArrayList<EmailCustomThreads> threads, Intent intent, int intendedLayout) {
        this.context = context;
        this.threads = threads;
        this.onclickIntent = intent;
        this.layout = intendedLayout;
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
//        holder.threadSubject.setText(this.threads.get(position).getSubject());
//        holder.threadSubjectSub.setText(this.threads.get(position).getSubjectSub());
//        holder.threadBottomRightText.setText(this.threads.get(position).getBottomRightText());
//        holder.threadTopRightText.setText(this.threads.get(position).getTopRightText());
//        holder.image.setImageResource(this.threads.get(position).getImage());
//
//        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, onclickIntent);
//                onclickIntent.putExtra("threadId", threads.get(position).getId());
//                onclickIntent.putExtra("subject", threads.get(position).getSubject());
//                context.startActivity(onclickIntent);
//            }
//        });
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
            threadSubject = itemView.findViewById(R.id.name);
            threadSubjectSub = itemView.findViewById(R.id.description);
            threadTopRightText = itemView.findViewById(R.id.type);
            threadBottomRightText = itemView.findViewById(R.id.provider);
            image = itemView.findViewById(R.id.image);
            mainLayout = itemView.findViewById(R.id.email_thread_card_layout);
        }
    }
}
