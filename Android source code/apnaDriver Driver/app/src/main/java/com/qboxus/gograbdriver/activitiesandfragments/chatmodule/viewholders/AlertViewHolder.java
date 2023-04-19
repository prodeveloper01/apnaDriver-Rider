package com.qboxus.gograbdriver.activitiesandfragments.chatmodule.viewholders;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.R;


public class AlertViewHolder extends RecyclerView.ViewHolder {


    public TextView message,datetxt;
    public View view;

    public AlertViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        this.message = view.findViewById(R.id.message);
        this.datetxt = view.findViewById(R.id.datetxt);
    }

}