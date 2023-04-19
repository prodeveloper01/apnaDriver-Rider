package com.qboxus.gograbdriver.activitiesandfragments.myjobhistory;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.models.MyJobHistoryModel;
import com.qboxus.gograbdriver.R;
import com.willy.ratingbar.ScaleRatingBar;


public class JobDetailF extends RootFragment implements View.OnClickListener {


    View view;
    ImageView ivBack;
    SimpleDraweeView img_customer;
    Preferences preferences;
    MyJobHistoryModel myJobHistoryModel;
    SimpleDraweeView imgDetailMap;
    TextView tvPickupLoc, tvDropoffLoc, tvDropoffTime, tvRideFare, tvCustomerName;
    ScaleRatingBar simpleRatingBar;

    public JobDetailF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_job_detail, container, false);
        initControl();
        actionControl();


        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
    }

    private void initControl() {
        preferences=new Preferences(view.getContext());
        myJobHistoryModel= (MyJobHistoryModel) getArguments().getSerializable("data");
        ivBack =view.findViewById(R.id.iv_back);
        img_customer=view.findViewById(R.id.img_customer);
        tvCustomerName =view.findViewById(R.id.tv_customer_name);
        imgDetailMap =view.findViewById(R.id.img_my_job);
        tvPickupLoc =view.findViewById(R.id.tv_pickup_loc);
        tvDropoffLoc =view.findViewById(R.id.tv_dropoff_loc);
        tvDropoffTime =view.findViewById(R.id.tv_dropoff_time);
        tvRideFare =view.findViewById(R.id.tv_ride_fare);
        simpleRatingBar=view.findViewById(R.id.simpleRatingBar);

        setUpScreenData();
    }

    private void setUpScreenData() {


        imgDetailMap.setController(Functions.frescoImageLoad(myJobHistoryModel.getTripModel().getMap(),
                R.drawable.image_placeholder,imgDetailMap,false));
        tvPickupLoc.setText(myJobHistoryModel.getTripModel().getPickupLocation());
        tvDropoffLoc.setText(myJobHistoryModel.getTripModel().getDestinationLocation());
        tvDropoffTime.setText(Functions.changeDateFormat("yyyy-MM-dd HH:mm:ss","dd MMM hh:mm a",myJobHistoryModel.getTripModel().getDestinationDatetime()));
        tvRideFare.setText(preferences.getKeyCurrencySymbol()+" "+myJobHistoryModel.getTripModel().getTripPaymentModel().getEstimatedFare());


        img_customer.setController(Functions.frescoImageLoad(myJobHistoryModel.getUserModel().getImage(),
                R.drawable.ic_profile_gray,img_customer,false));
        tvCustomerName.setText(""+myJobHistoryModel.getUserModel().getFirstName() + " "+ myJobHistoryModel.getUserModel().getLastName() ) ;

        if (myJobHistoryModel.getDriverRatingList() != null && myJobHistoryModel.getDriverRatingList().size()>0)
        {
            simpleRatingBar.setRating(Float.valueOf(myJobHistoryModel.getDriverRatingList().get(0).getStar()));
        }else{
            simpleRatingBar.setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
        }
    }
}
