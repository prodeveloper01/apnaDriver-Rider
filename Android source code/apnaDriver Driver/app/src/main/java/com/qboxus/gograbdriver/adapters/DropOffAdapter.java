package com.qboxus.gograbdriver.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.R;
import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.databinding.ItemDropoffListBinding;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.models.RecipientModel;
import com.qboxus.gograbdriver.models.RiderOrderMultiStop;

import java.util.ArrayList;

public class DropOffAdapter extends RecyclerView.Adapter<DropOffAdapter.ViewHolder> {
    Context context;
    ArrayList<RecipientModel> dataList = new ArrayList<>();
    ArrayList<RiderOrderMultiStop> multiStops = new ArrayList<>();
    AdapterClickListener adapterClickListener;
    ItemDropoffListBinding binding;
    Preferences preferences;
    public DropOffAdapter(Context context, ArrayList<RecipientModel> dataList,ArrayList<RiderOrderMultiStop> multiStops, AdapterClickListener adapterClickListener ) {
        preferences = new Preferences(context);
        this.context = context;
        this.dataList = dataList;
        this.multiStops = multiStops;
        this.adapterClickListener = adapterClickListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        binding = ItemDropoffListBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final RecipientModel item = dataList.get(position);

        holder.itemBinding.tvRecipentName.setText("Name: "+item.getRecipientName());
        holder.itemBinding.tvRecipentPhone.setText("Phone: "+item.getRecipientNumber());
        holder.itemBinding.tvRecipentAddress.setText("Address: "+item.getRecipientAddress());

        if(TextUtils.isEmpty(item.getRecipientNote())){
            holder.itemBinding.tvRecipentInstruction.setVisibility(View.GONE);
        }else {
            holder.itemBinding.tvRecipentInstruction.setVisibility(View.VISIBLE);
            holder.itemBinding.tvRecipentInstruction.setText("Instructions: "+item.getDeliveryInstruction());
        }


        holder.itemBinding.tvItemType.setText("Item Type: "+item.getTypeOfItem());
        holder.itemBinding.tvItemSize.setText("Size: "+item.getPackageSize());
        holder.itemBinding.tvTotal.setText("Price: "+ preferences.getKeyCurrencySymbol()+" "+item.getPrice());



        if(multiStops!=null) {
            if (position <= (multiStops.size() - 1)) {

                RiderOrderMultiStop multiStop=multiStops.get(position);
                if(!multiStop.delivered.equals(Variables.emptyTime)){
                    holder.itemBinding.statustxt.setText("Delivered");
                    holder.itemBinding.activeImage.setVisibility(View.GONE);
                    holder.itemBinding.statustxt.setBackgroundColor(context.getResources().getColor(R.color.gray));
                    holder.itemBinding.anonymousLayout.setBackground(context.getResources().getDrawable(R.drawable.d_border_gray_line));

                }
                else if(!multiStop.on_the_way_to_dropoff.equals(Variables.emptyTime)){
                    holder.itemBinding.statustxt.setText("On the Way");
                    holder.itemBinding.activeImage.setVisibility(View.VISIBLE);
                    holder.itemBinding.statustxt.setBackgroundColor(context.getResources().getColor(R.color.app_color));
                    holder.itemBinding.anonymousLayout.setBackground(context.getResources().getDrawable(R.drawable.ractengle_less_round_stroke_primary));
                }

                holder.itemBinding.statustxt.setVisibility(View.VISIBLE);
            } else {
                holder.itemBinding.activeImage.setVisibility(View.GONE);
                holder.itemBinding.statustxt.setVisibility(View.GONE);
                holder.itemBinding.anonymousLayout.setBackground(context.getResources().getDrawable(R.drawable.d_border_gray_line));

            }
        }


        holder.bind(position, item, adapterClickListener);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemDropoffListBinding itemBinding;
        public ViewHolder(@NonNull ItemDropoffListBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }

        public void bind(final int item, final RecipientModel model, final AdapterClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // This is OnClick of any list Item
                    listener.OnItemClick(item, model, v);
                }

            });

            itemBinding.mapbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(item, model, v);
                }
            });

            itemBinding.phoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(item, model, v);
                }
            });


        }
    }
}
