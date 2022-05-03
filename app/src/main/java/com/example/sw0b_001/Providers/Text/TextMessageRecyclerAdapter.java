package com.example.sw0b_001.Providers.Text;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sw0b_001.R;
import com.example.sw0b_001.TextBodyActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TextMessageRecyclerAdapter extends RecyclerView.Adapter<TextMessageRecyclerAdapter.ViewHolder> {

    Context context;
    int layout, highlightCount;
    public List<TextMessage> textMessages;
    boolean isHighlighting;
    ActionBar ab;

    public TextMessageRecyclerAdapter(Context context, List<TextMessage> textMessages, int intendedLayout, ActionBar ab) {
        this.context = context;
        this.layout = intendedLayout;
        this.textMessages = textMessages;
        this.isHighlighting = false;
        this.ab = ab;
        this.highlightCount = 0;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) { LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(this.layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        TextMessage textMessage = this.textMessages.get(position);
//        holder.threadSubject.setText(this.textMessages.get(position).getBody());
        holder.threadSubject.setText(this.textMessages.get(position).getBody().length() > 20 ?
                this.textMessages.get(position).getBody().substring(0, 20) + "..." :
                this.textMessages.get(position).getBody());
        holder.threadSubSubject.setText("");
        holder.threadBottomRightText.setText(this.textMessages.get(position).getStatus());
        holder.threadTopRightText.setText(this.textMessages.get(position).getDatetime());
        holder.image.setImageResource(this.textMessages.get(position).getImage());

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
                    Intent intent = new Intent(context, TextBodyActivity.class);
                    intent.putExtra("text_message_id", textMessage.getId());
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
        return this.textMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView threadSubject, threadSubSubject, threadTopRightText, threadBottomRightText;
        ImageView image;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            threadSubject = itemView.findViewById(R.id.subject);
            threadSubSubject = itemView.findViewById(R.id.subjectSub);
            threadTopRightText = itemView.findViewById(R.id.topRight);
            threadBottomRightText = itemView.findViewById(R.id.bottomRight);
            image = itemView.findViewById(R.id.image);
//            mainLayout = itemView.findViewById(R.id.email_thread_card_layout);
        }
    }
}
