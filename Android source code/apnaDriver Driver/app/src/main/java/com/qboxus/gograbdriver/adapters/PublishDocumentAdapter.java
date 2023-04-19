package com.qboxus.gograbdriver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.models.PublishDocumentModel;
import com.qboxus.gograbdriver.R;

import java.util.List;

public class PublishDocumentAdapter extends RecyclerView.Adapter<PublishDocumentAdapter.ChildViewHolder> {


    List<PublishDocumentModel> documentModelList;
    AdapterClickListener clickListener;


    public PublishDocumentAdapter(List<PublishDocumentModel> documentModelList, AdapterClickListener clickListener) {
        this.documentModelList = documentModelList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload_document_layout, null);
        return new ChildViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {

        PublishDocumentModel model = documentModelList.get(position);


        holder.txtDocName.setText(model.getName());


        holder.bind(position, model, clickListener);
    }

    @Override
    public int getItemCount() {
        return documentModelList.size();
    }

    class ChildViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDelete;
        TextView txtDocName;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);

            ivDelete = itemView.findViewById(R.id.iv_delete);
            txtDocName = itemView.findViewById(R.id.txt_doc_name);


        }

        public void bind(final int item, final PublishDocumentModel documentModel, final AdapterClickListener listener) {

            itemView.setOnClickListener(v -> {

                listener.OnItemClick(item, documentModel, v);

            });

            ivDelete.setOnClickListener(v -> {

                listener.OnItemClick(item, documentModel, v);


            });



        }
    }


}
