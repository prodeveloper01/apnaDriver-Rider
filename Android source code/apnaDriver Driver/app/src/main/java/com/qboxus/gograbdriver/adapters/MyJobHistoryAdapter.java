package com.qboxus.gograbdriver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.MyJobHistoryModel;
import com.qboxus.gograbdriver.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyJobHistoryAdapter extends RecyclerView.Adapter<MyJobHistoryAdapter.ViewHolder> {



    ArrayList<MyJobHistoryModel> dataList;
    Context context;
    AdapterClickListener adapterClicklistener;

    public MyJobHistoryAdapter(Context context, ArrayList<MyJobHistoryModel> captainPortal_models, AdapterClickListener onClickListner){

      this.context=context;
      this.adapterClicklistener =onClickListner;
      this.dataList = captainPortal_models;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_list_view,null);

        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        return  new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyJobHistoryModel myJobHistoryModel = dataList.get(position);
        holder.tvPickupTime.setText(Functions.changeDateFormat("yyyy-MM-dd HH:mm:ss","dd MMM hh:mm a",myJobHistoryModel.getTripModel().getPickupDatetime()));
        holder.tvDropoffTime.setText(Functions.changeDateFormat("yyyy-MM-dd HH:mm:ss","dd MMM hh:mm a",myJobHistoryModel.getTripModel().getDestinationDatetime()));

        holder.tvPickupLoc.setText(myJobHistoryModel.getTripModel().getPickupLocation());
        holder.tvDropoffLoc.setText(myJobHistoryModel.getTripModel().getDestinationLocation());
        holder.tvEstimatedTime.setText(GetFifrenceBetweenTime(myJobHistoryModel.getTripModel().getPickupDatetime(),myJobHistoryModel.getTripModel().getDestinationDatetime()));

        holder.imgMyJob.setController(Functions.frescoImageLoad(
                myJobHistoryModel.getTripModel().getMap(),
                R.drawable.image_placeholder,
                holder.imgMyJob,
                false
        ));

        holder.bind(position, myJobHistoryModel, adapterClicklistener);
    }

    private String GetFifrenceBetweenTime(String pickupTime,String dropoffTime) {
      try {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          Date firstDate = sdf.parse(pickupTime);
          Date secondDate = sdf.parse(dropoffTime);

          long diffInMillies = secondDate.getTime() - firstDate.getTime();

          long secs = (diffInMillies) / 1000;
          int hours = (int) (secs / 3600);
          secs = secs % 3600;
          int mins = (int) (secs / 60);
          secs = secs % 60;

          return (hours+"H "+mins+"M "+secs+"S");
      }
      catch (Exception e)
      {
          return "";
      }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Preferences preferences;
        TextView tvPickupTime, tvPickupLoc, tvDropoffTime, tvDropoffLoc, tvEstimatedTime;
        SimpleDraweeView imgMyJob;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            preferences=new Preferences(itemView.getContext());
            tvEstimatedTime =itemView.findViewById(R.id.tv_estimated_time);
            tvPickupTime =itemView.findViewById(R.id.tv_pickup_time);
            tvPickupLoc =itemView.findViewById(R.id.tv_pickup_loc);
            tvDropoffTime =itemView.findViewById(R.id.tv_dropoff_time);
            tvDropoffLoc =itemView.findViewById(R.id.tv_dropoff_loc);
            imgMyJob =itemView.findViewById(R.id.img_my_job);
        }

        public void bind(final int pos, final MyJobHistoryModel item , final AdapterClickListener listener ){

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(pos,item,view);
                }
            });

        }

    }

}
