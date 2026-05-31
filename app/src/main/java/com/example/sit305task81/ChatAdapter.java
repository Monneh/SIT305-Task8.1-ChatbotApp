package com.example.sit305task81;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sit305task81.data.MessageEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private final List<MessageEntity> messages = new ArrayList<>();

    public void submitMessages(List<MessageEntity> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout row = new LinearLayout(parent.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(parent, 5), 0, dp(parent, 5));
        row.setLayoutParams(new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        ));
        return new MessageViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout row;

        MessageViewHolder(@NonNull LinearLayout row) {
            super(row);
            this.row = row;
        }

        void bind(MessageEntity message) {
            row.removeAllViews();
            row.setGravity(message.fromUser ? Gravity.RIGHT : Gravity.LEFT);

            if (!message.fromUser) {
                row.addView(avatar("AI"));
            }

            LinearLayout bubble = new LinearLayout(row.getContext());
            bubble.setOrientation(LinearLayout.VERTICAL);
            bubble.setPadding(dp(12), dp(8), dp(12), dp(7));
            bubble.setBackground(rounded(Color.rgb(226, 226, 226), dp(5)));

            TextView body = new TextView(row.getContext());
            body.setText(message.content);
            body.setTextColor(Color.rgb(45, 45, 45));
            body.setTextSize(14);
            bubble.addView(body);

            TextView time = new TextView(row.getContext());
            time.setText(formatTime(message.timestamp));
            time.setTextColor(Color.rgb(100, 100, 100));
            time.setTextSize(10);
            time.setGravity(Gravity.RIGHT);
            bubble.addView(time);

            int maxWidth = row.getResources().getDisplayMetrics().widthPixels - dp(110);
            LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                    Math.min(maxWidth, dp(260)),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            bubbleParams.setMargins(dp(6), 0, dp(6), 0);
            row.addView(bubble, bubbleParams);

            if (message.fromUser) {
                row.addView(avatar("U"));
            }
        }

        private TextView avatar(String label) {
            TextView avatar = new TextView(row.getContext());
            avatar.setText(label);
            avatar.setTextSize(label.length() > 1 ? 10 : 12);
            avatar.setTypeface(Typeface.DEFAULT_BOLD);
            avatar.setTextColor(Color.rgb(70, 70, 70));
            avatar.setGravity(Gravity.CENTER);
            avatar.setBackground(rounded(Color.rgb(220, 220, 220), dp(18)));
            avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(34), dp(34)));
            return avatar;
        }

        private String formatTime(long timestamp) {
            return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(timestamp));
        }

        private GradientDrawable rounded(int color, int radius) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(color);
            drawable.setCornerRadius(radius);
            return drawable;
        }

        private int dp(int value) {
            return Math.round(value * row.getResources().getDisplayMetrics().density);
        }
    }

    private static int dp(ViewGroup parent, int value) {
        return Math.round(value * parent.getResources().getDisplayMetrics().density);
    }
}
