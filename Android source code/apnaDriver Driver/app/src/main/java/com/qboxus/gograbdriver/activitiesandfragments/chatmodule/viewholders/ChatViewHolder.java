package com.qboxus.gograbdriver.activitiesandfragments.chatmodule.viewholders;


import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.ChatAdapter;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.ChatModel;
import com.qboxus.gograbdriver.R;


public class ChatViewHolder extends RecyclerView.ViewHolder {

    public TextView message, datetxt, msgDate, username;
    public View view;
    public SimpleDraweeView chatImageView;

    public ChatViewHolder(View itemView) {
        super(itemView);
        view = itemView;

        this.message = view.findViewById(R.id.messageText);
        this.username = view.findViewById(R.id.username);
        this.datetxt = view.findViewById(R.id.datetxt);
        this.msgDate = view.findViewById(R.id.msg_date);
        this.chatImageView = view.findViewById(R.id.chatImageView);

    }

    public void bind(final ChatModel item,
                     final ChatAdapter.OnLongClickListener long_listener) {
        message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                long_listener.onLongclick(item,v);
                return false;
            }
        });
    }
}

