package com.qboxus.gograbdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.models.MyOrdersModel;
import com.qboxus.gograbdriver.R;
import com.qboxus.gograbdriver.models.RecipientModel;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.FilterViewHolder> {

    List<MyOrdersModel> dataList;
    Context context;
    AdapterClickListener adapterClickListener;

    public OrdersAdapter(Context context, List<MyOrdersModel> dataList, AdapterClickListener adapterClicklistener) {

        this.context = context;
        this.dataList = dataList;
        this.adapterClickListener = adapterClicklistener;
    }


    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_orders_layout, null);
        return new FilterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {

        MyOrdersModel item = dataList.get(position);

        if (item.senderName != null && !item.equals("")) {
            String personName = Functions.toTitleCase(item.senderName);
            holder.tvpersonName.setText(personName);
        }
        String orderCreateDate = item.orderCreateDate;
        if (orderCreateDate != null && !orderCreateDate.equals("0000-00-00 00:00:00")) {
            orderCreateDate = Functions.convertDatetime(orderCreateDate, "convert_dateonly");
            holder.orderCreateDate.setText(orderCreateDate);
        }

        holder.tvOrderNo.setText(item.id);
        holder.tvActualPickupLocation.setText(item.senderLocationString);

        String pickup_date = item.orderPickupTime;
        if (pickup_date != null && !pickup_date.equals("0000-00-00 00:00:00")) {
            pickup_date = Functions.convertDatetime(pickup_date, "pickup_date");
            holder.tvActualDropOff.setText(pickup_date);
            if(item.orderType.equals("food")) {
                holder.tvDropTitle.setText(context.getString(R.string.delivery_time));
            }else{
                holder.tvDropTitle.setText(context.getString(R.string.pickup_time));
            }
        }else{

            if(item.orderType.equals("food")){
                holder.tvActualDropOff.setText(item.recevierLocationString);

            }
            else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i=0;i<item.recipientList.size();i++){
                    if(i>0)
                    stringBuilder.append("\n");

                    stringBuilder.append(i+1+": ");
                    stringBuilder.append(item.recipientList.get(i).getRecipientAddress());
                }

                holder.tvActualDropOff.setText(stringBuilder.toString());

            }
            holder.tvDropTitle.setText(context.getString(R.string.dropoff_location));
        }

        if (item.currentView.equals("Pending")) {
            holder.rltDetailDiv.setVisibility(View.GONE);
            holder.divAccept.setVisibility(View.VISIBLE);
        } else {
            holder.tvOrderStatus.setText(item.currentView);
            holder.rltDetailDiv.setVisibility(View.VISIBLE);
            holder.divAccept.setVisibility(View.GONE);
        }

        holder.bind(position, item, adapterClickListener);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderNo, tvpersonName, tvActualDropOff, tvActualPickupLocation, tvOrderStatus, tvDropTitle, orderCreateDate;
        RelativeLayout btnYes, btnNo, btnDetailsP;
        RelativeLayout rltDetailDiv, btnDetails;
        LinearLayout divAccept;
        LinearLayout histroyRlt;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            orderCreateDate = itemView.findViewById(R.id.order_create_date);
            tvpersonName = itemView.findViewById(R.id.tv_person_name);
            tvActualDropOff = itemView.findViewById(R.id.tv_actual_drop_off);
            tvActualPickupLocation = itemView.findViewById(R.id.tv_actual_Pickup_location);
            btnYes = itemView.findViewById(R.id.btn_yes);
            histroyRlt = itemView.findViewById(R.id.histroy_rlt);
            btnNo = itemView.findViewById(R.id.btn_no);
            btnDetails = itemView.findViewById(R.id.btn_details);
            rltDetailDiv = itemView.findViewById(R.id.rlt_detail_div);
            divAccept = itemView.findViewById(R.id.div_accept);
            btnDetailsP = itemView.findViewById(R.id.btn_details_p);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvDropTitle = itemView.findViewById(R.id.tv_drop_title);
        }


        public void bind(final int item, final MyOrdersModel my_orders_model_, final AdapterClickListener listener) {

            itemView.setOnClickListener(v -> {

                listener.OnItemClick(item, my_orders_model_, v);


            });

            btnDetails.setOnClickListener(v -> {

                listener.OnItemClick(item, my_orders_model_, v);


            });

            btnYes.setOnClickListener(v -> {

                listener.OnItemClick(item, my_orders_model_, v);


            });

            btnNo.setOnClickListener(v -> {

                listener.OnItemClick(item, my_orders_model_, v);


            });

            btnDetailsP.setOnClickListener(v -> {
                listener.OnItemClick(item, my_orders_model_, v);


            });

        }

    }


}
