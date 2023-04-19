package com.qboxus.gograbdriver.activitiesandfragments.earningfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.databinding.FragmentEarningBinding;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;

import org.json.JSONException;
import org.json.JSONObject;


public class EarningF extends RootFragment {


    Preferences preferences;
    View.OnClickListener navClickListener;

    FragmentEarningBinding binding;

    public EarningF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    public EarningF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEarningBinding.inflate(getLayoutInflater());

        initControl();
        actionControl();
        return binding.getRoot();
    }

    private void actionControl() {
        callApiRideDetails();
        binding.ivNavMenu.setOnClickListener(navClickListener);
    }

    private void initControl() {
        preferences = new Preferences(getContext());

    }

    public void callApiRideDetails() {
        JSONObject params = new JSONObject();

        try {
            params.put("driver_id", preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(getContext(), ApisList.showEarningsAndRecentTrips, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                parseData(resp);

            }
        });

    }

    public void parseData(String resp) {

        try {
            JSONObject jsonObject = new JSONObject(resp);

            String code = jsonObject.optString("code");

            if (code.equals("200")) {

                JSONObject msg = jsonObject.optJSONObject("msg");

                JSONObject RideHailing=msg.optJSONObject("RideHailing");
                JSONObject FoodOrder=msg.optJSONObject("FoodOrder");
                JSONObject ParcelOrder=msg.optJSONObject("ParcelOrder");

                binding.rideTotalEaning.setText(preferences.getKeyCurrencySymbol() + " " + RideHailing.optString("earnings"));
                binding.totalRide.setText(RideHailing.optString("completed_orders"));

                binding.foodTotalEaning.setText(preferences.getKeyCurrencySymbol() + " " + FoodOrder.optString("earnings"));
                binding.foodTotalRide.setText(FoodOrder.optString("completed_orders"));

                binding.parcelTotalEaning.setText(preferences.getKeyCurrencySymbol() + " " + ParcelOrder.optString("earnings"));
                binding.parcelTotalRide.setText(ParcelOrder.optString("completed_orders"));

            }
        } catch (Exception e) {
            Functions.logDMsg("Error " + e);
        }

    }

}