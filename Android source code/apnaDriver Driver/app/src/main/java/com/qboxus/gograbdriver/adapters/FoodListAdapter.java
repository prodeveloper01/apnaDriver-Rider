package com.qboxus.gograbdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.FoodListModel;
import com.qboxus.gograbdriver.databinding.ItemFoodListBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
    ItemFoodListBinding binding;
    Context context;
    ArrayList<FoodListModel> arrayList = new ArrayList<>();
    AdapterClickListener adapterClickListener;
    String currencySymbol;
    ArrayList<HashMap<String, String>> extraItem;
    Preferences preferences;

    public FoodListAdapter(Context context, ArrayList<FoodListModel> arrayList, AdapterClickListener adapterClickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.adapterClickListener = adapterClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        binding = ItemFoodListBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        preferences = new Preferences(context);
        final FoodListModel item = arrayList.get(position);
        holder.itemView.tvQuantity.setText(item.getTvQuantity());
        holder.itemView.itemName.setText(item.getItemName());
        holder.itemView.tvAmount.setText(item.getAmount());
        currencySymbol = preferences.getKeyCurrencySymbol();

        holder.itemView.tvQuantity.setText(item.getTvQuantity());
        holder.itemView.itemName.setText(item.getItemName());
        double totalPrice = Double.parseDouble(item.getTvQuantity()) *  Double.parseDouble(item.getAmount());
        holder.itemView.tvAmount.setText(currencySymbol +  Functions.roundoffDecimal(totalPrice));
        extraItem = item.getExtraItem();
        StringBuilder stringBuilder = new StringBuilder();;
        for (int b = 0; b < extraItem.size(); b++) {
            String menuExtraItemName = extraItem.get(b).get("menu_extra_item_name");
            String extraItemPrice = extraItem.get(b).get("menu_extra_item_price");
            stringBuilder.append(menuExtraItemName);
            stringBuilder.append(" ");
            stringBuilder.append("(");
            stringBuilder.append(currencySymbol + extraItemPrice);
            stringBuilder.append(")");
            if (b != extraItem.size() - 1) {
                stringBuilder.append(" ");
                stringBuilder.append("\u00b7");
                stringBuilder.append(" ");
            }
            holder.itemView.extraItem.setText(stringBuilder.toString());
        }
        calculateTotalPrice(item , holder.itemView.tvAmount);
    }

    private void calculateTotalPrice(FoodListModel item, TextView tvAmount) {
        Double totalExtraItemPrice = 0.0;
        double price1 = 0.0;
        double itemPrice = Double.parseDouble(item.getAmount());
        double quantity = Double.parseDouble(item.getTvQuantity());
        if (extraItem != null && extraItem.size() > 0) {
            for (int b = 0; b < extraItem.size(); b++) {
                String extraPrice = extraItem.get(b).get("menu_extra_item_price");
                double counter = Double.parseDouble(extraPrice);
                totalExtraItemPrice = counter + totalExtraItemPrice;
            }

            price1 = (totalExtraItemPrice + itemPrice) * quantity;
        } else {
            price1 = itemPrice * quantity;
        }

        tvAmount.setText(currencySymbol + Functions.roundoffDecimal(price1));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemFoodListBinding itemView;

        public ViewHolder(@NonNull ItemFoodListBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;

        }
    }
}
