package com.qboxus.gograbdriver.activitiesandfragments.chatmodule;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.viewholders.AlertViewHolder;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.viewholders.ChatViewHolder;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int mychat = 1;
    private final int friendchat = 2;
    private final int alertMessage = 7;
    String myID = "";
    Context context;
    Integer todayDay = 0;
    private List<ChatModel> mDataSet;
    private OnItemClickListener listener;
    private ChatAdapter.OnLongClickListener long_listener;

    ChatAdapter(List<ChatModel> dataSet, String id, Context context, ChatAdapter.OnItemClickListener listener, ChatAdapter.OnLongClickListener long_listener) {
        mDataSet = dataSet;
        this.myID = id;
        this.context = context;
        this.listener = listener;
        this.long_listener = long_listener;
        Calendar cal = Calendar.getInstance();
        todayDay = cal.get(Calendar.DAY_OF_MONTH);
    }

    // this is the all types of view that is used in the chat
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype) {
            // we have 4 type of layout in chat activity text chat of my and other and also
            // image layout of my and other
            case mychat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                ChatViewHolder mychatHolder = new ChatViewHolder(v);
                return mychatHolder;
            case friendchat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                ChatViewHolder friendchatHolder = new ChatViewHolder(v);
                return friendchatHolder;

            case alertMessage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                AlertViewHolder alertviewholder = new AlertViewHolder(v);
                return alertviewholder;

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatModel chat = mDataSet.get(position);

        if (chat.getType().equals("text")) {
            ChatViewHolder chatviewholder = (ChatViewHolder) holder;
            // check if the message is from sender or receiver

            chatviewholder.username.setText(chat.sender_name);
            chatviewholder.message.setText(chat.getText());
            chatviewholder.msgDate.setText(showMessageTime(chat.getTimestamp()));
            String image = chat.getSender_image();
            if (image != null && !image.equals("")) {
                chatviewholder.chatImageView.setController(Functions.frescoImageLoad(image,
                        R.drawable.ic_profile_gray,chatviewholder.chatImageView,false));
            } else {
                chatviewholder.chatImageView.setController(Functions.frescoImageLoad(
                        ContextCompat.getDrawable(chatviewholder.itemView.getContext(),R.drawable.ic_profile_gray),
                        chatviewholder.chatImageView,false));
            }
            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(0, 2).equals(chat.getTimestamp().substring(0, 2))) {
                    chatviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    chatviewholder.datetxt.setVisibility(View.VISIBLE);
                    chatviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
                }

            } else {
                chatviewholder.datetxt.setVisibility(View.VISIBLE);
                chatviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
            }

            chatviewholder.bind(chat, long_listener);

        } else if (chat.getType().equals("delete")) {
            AlertViewHolder alertviewholder = (AlertViewHolder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.ColorBlack));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.d_border_gray_line));

            alertviewholder.message.setText("" + context.getString(R.string.this_message_was_deleted));

            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                    alertviewholder.datetxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
                }

            } else {
                alertviewholder.datetxt.setVisibility(View.VISIBLE);
                alertviewholder.datetxt.setText(changeDate(chat.getTimestamp()));
            }

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (mDataSet.get(position).getType().equals("text")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return mychat;
            }
            return friendchat;
        } else {
            return alertMessage;
        }
    }

    public String changeDate(String date) {
        long currenttime = System.currentTimeMillis();

        long databasedate = 0;
        Date d = null;
        try {
            d = Variables.df.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if (todayDay == chatday)
                return "Today";
            else if ((todayDay - chatday) == 1)
                return "Yesterday";
        } else if (difference < 172800000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if ((todayDay - chatday) == 1)
                return "Yesterday";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }

    public String showMessageTime(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        Date d = null;
        try {
            d = Variables.df.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d != null)
            return sdf.format(d);
        else
            return "null";
    }

    public String ChangeDate_to_time(String date) {

        Date d = null;
        try {
            d = Variables.df2.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }


    public interface OnItemClickListener {
        void onItemClick(int postion, ChatModel item, View view);
    }

    public interface OnLongClickListener {
        void onLongclick(ChatModel item, View view);
    }
}
