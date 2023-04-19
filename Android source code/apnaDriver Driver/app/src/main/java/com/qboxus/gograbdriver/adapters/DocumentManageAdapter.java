package com.qboxus.gograbdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.models.DocumentManageModel;
import com.qboxus.gograbdriver.R;

import java.util.ArrayList;

public class DocumentManageAdapter extends RecyclerView.Adapter<DocumentManageAdapter.ViewHolder> {


    ArrayList<DocumentManageModel> datalist;
    Context context;
    AdapterClickListener clickListener;


    public DocumentManageAdapter(Context context, ArrayList<DocumentManageModel> datalist, AdapterClickListener clickListener) {
        this.datalist = datalist;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.documents_item_view, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DocumentManageModel documentHomeModel = datalist.get(position);
        String document = decodeString(documentHomeModel.getType());
        String capitalize_txt = Functions.toTitleCase(document);

        if(capitalize_txt.equals("Identification")){
            holder.tvNameDocument.setText("National ID/Password");
        }else{
            holder.tvNameDocument.setText(capitalize_txt);
        }


        if (documentHomeModel.getStatus().equals("1")) {
            holder.tvStatus.setText(R.string.approved);
        } else if (documentHomeModel.getStatus().equals("0")) {
            holder.tvStatus.setText(R.string.pending);
        } else {
            holder.tvStatus.setText(R.string.rejected);
        }

        if (datalist.size()>0)
        {
            if (position==datalist.size()-1)
            {
                holder.viewBottomLine.setVisibility(View.GONE);
            }
            else
            {
                holder.viewBottomLine.setVisibility(View.VISIBLE);
            }
        }


        holder.bind(position, documentHomeModel, clickListener);



    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameDocument, tvStatus;
        View viewBottomLine, tabView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNameDocument = itemView.findViewById(R.id.tv_title);
            tvStatus = itemView.findViewById(R.id.et_status);
            viewBottomLine =itemView.findViewById(R.id.view_bottom_line);
            tabView =itemView.findViewById(R.id.tab_view);
        }

        public void bind(final int item, final DocumentManageModel documentHomeModel, final AdapterClickListener listener) {

            tabView.setOnClickListener(v -> {

                listener.OnItemClick(item, documentHomeModel, v);


            });

        }
    }

    public String decodeString(String strData) {
        if (strData == null) {
            return "";
        }
        return strData.replaceAll("_", " ");
    }


}
