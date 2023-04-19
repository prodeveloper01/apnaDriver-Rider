package com.qboxus.gograbdriver.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.RideModel;
import com.qboxus.gograbdriver.models.RideRequestModel;
import com.qboxus.gograbdriver.services.BackgroundSoundService;

import org.json.JSONObject;

public class FirebaseNotificationReceiver extends BroadcastReceiver {

    public Context context;
    Preferences preferences;
    RideModel rideModel;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        preferences = new Preferences(context);
        Bundle extras = intent.getExtras();
        try {


            if (extras != null) {
                String type = extras.getString("type");
                if(type!=null){
                    switch (type){
                        case "taxi_order":
                        case "request_vehicle":
                            checkUserRequest(context);
                            break;
                    }

                    Intent intent1 = new Intent();
                    intent1.setAction("request_responce");
                    intent1.putExtras(intent.getExtras());
                    context.sendBroadcast(intent1);
                }

            }

        } catch (Exception e) {
            Functions.logDMsg( "broadcast notification " + e);
        }
    }


    public void checkUserRequest(Context context) {

        JSONObject params = new JSONObject();

        try {
            params.put("user_id", preferences.getKeyUserId());
        } catch (Exception e) {
            Functions.logDMsg( "Exception " + e);
        }


        ApiRequest.callApi(context, ApisList.showActiveRequest, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {


                try {

                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    JSONObject msg = jsonObject.optJSONObject("msg");
                    if (code.equals("200")) {
                        ParseRideStatusResponce(resp);
                    } else {
                        stopRideMusicService();
                    }
                } catch (Exception e) {
                    Functions.logDMsg( "Exception " + e);
                }


            }
        });

    }


    private void ParseRideStatusResponce(String resp) {

        try {

            rideModel = new RideModel();
            JSONObject jsonObject = new JSONObject(resp);

            String code = jsonObject.optString("code");
            JSONObject msg = jsonObject.optJSONObject("msg");
            if (code.equals("200")) {
                JSONObject request = msg.getJSONObject("Request");

                RideRequestModel rideRequestModel = new RideRequestModel();
                {
                    rideRequestModel.setId(request.optString("id", ""));
                    rideRequestModel.setUserId(request.optString("user_id", ""));
                    rideRequestModel.setVehicleId(request.optString("vehicle_id", ""));
                    rideRequestModel.setDriverId(request.optString("driver_id", ""));
                    rideRequestModel.setPickupLat(request.optString("pickup_lat", "0"));
                    rideRequestModel.setPickupLng(request.optString("pickup_long", "0"));
                    rideRequestModel.setDestinationLat(request.optString("destination_lat", "0"));
                    rideRequestModel.setDestinationLng(request.optString("destination_long", "0"));
                    rideRequestModel.setRequest(request.optString("request", ""));
                    rideRequestModel.setDriverResponceDatetime(request.optString("driver_response_datetime", ""));
                    rideRequestModel.setDriverRideResponse(request.optString("driver_ride_response", ""));
                    rideRequestModel.setUserRideResponse(request.optString("user_ride_response", ""));
                    rideRequestModel.setReason(request.optString("reason", ""));
                    rideRequestModel.setOnTheWay(request.optString("on_the_way", ""));
                    rideRequestModel.setArriveOnLocation(request.optString("arrive_on_location", ""));
                    rideRequestModel.setStartRide(request.optString("start_ride", ""));
                    rideRequestModel.setEndRide(request.optString("end_ride", ""));
                    rideRequestModel.setEstimatedFare(request.optString("estimated_fare", "0"));
                    rideRequestModel.setWalletPay(request.optString("wallet_pay", "0"));
                    rideRequestModel.setPaymentType(request.optString("payment_type", ""));
                    rideRequestModel.setPaymentMethodId(request.optString("payment_method_id", ""));
                    rideRequestModel.setCollectPayment(request.optString("collect_payment", "0"));
                    rideRequestModel.setCreated(request.optString("created", ""));
                    //rideRequestModel.setFinalFare(request.optString("final_fare","0"));
                    rideRequestModel.setPickupString(request.optString("pickup_location", ""));
                    rideRequestModel.setDestinationString(request.optString("destination_location", ""));

                }

                rideModel.setRideRequestModel(rideRequestModel);

                updateScreenAction();
            }

        } catch (Exception e) {
            Functions.logDMsg( "Exception " + e);
        }
    }


    private void updateScreenAction() {
        try {

            if (rideModel != null) {

                if (Integer.valueOf(rideModel.getRideRequestModel().getCollectPayment()) > 0) {
                    stopRideMusicService();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getEndRide()) > 0) {
                    stopRideMusicService();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getStartRide()) > 0) {
                    stopRideMusicService();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getArriveOnLocation()) > 0) {
                    stopRideMusicService();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getOnTheWay()) > 0) {
                    stopRideMusicService();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getRequest()) == 1) {
                    stopRideMusicService();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getRequest()) == 0) {

                    startRideMusicService();
                }

            }
        } catch (Exception e) {
            Functions.logDMsg( "Exception " + e);
        }
    }


    public void startRideMusicService() {
        BackgroundSoundService backgroundSoundService = new BackgroundSoundService();
        Intent mServiceIntent = new Intent(context, backgroundSoundService.getClass());
        if (!Functions.isMyServiceRunning(context, backgroundSoundService.getClass())) {
            context.startService(mServiceIntent);
        }
    }


    public void stopRideMusicService() {
        BackgroundSoundService backgroundSoundService = new BackgroundSoundService();
        Intent mServiceIntent = new Intent(context, backgroundSoundService.getClass());
        if (Functions.isMyServiceRunning(context, backgroundSoundService.getClass())) {
            context.stopService(mServiceIntent);
        }
    }

}





