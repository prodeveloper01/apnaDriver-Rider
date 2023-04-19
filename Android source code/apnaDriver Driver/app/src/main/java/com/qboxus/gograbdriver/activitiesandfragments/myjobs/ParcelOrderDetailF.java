package com.qboxus.gograbdriver.activitiesandfragments.myjobs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.gograbdriver.R;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.ChatA;
import com.qboxus.gograbdriver.adapters.DropOffAdapter;
import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.databinding.FragmentParcelOrderDetailedBinding;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.models.MyOrdersModel;
import com.qboxus.gograbdriver.models.RecipientModel;
import com.qboxus.gograbdriver.models.RiderOrderMultiStop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ParcelOrderDetailF extends RootFragment implements View.OnClickListener {

    final String stOrderId = "order_id";
    String orderid,  senderLocationLong, userId, userFullName, userImage, senderLocationLat,  status, selectedPos;
    String deliveryAddressLat, deliveryAddressLong, riderId;
    String emptyTime = "0000-00-00 00:00:00";
    String orderDeliveredSuccessfully = "Order Delivered Successfully";
    String symbol;
    CallbackResponce callback;
    String onlineStatus;
    Preferences preferences;
    MyOrdersModel myOrdersModel;

    FragmentParcelOrderDetailedBinding binding;
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String isSigned = data.getStringExtra("result");
                    if (isSigned.equals("ok")) {
                        callApiShowDetail();
                    }
                }
            });
    String receiverAddressDetail, senderAddress;
    private DatabaseReference databaseReference;

    public ParcelOrderDetailF() {
        //required empty constructor
    }

    public ParcelOrderDetailF(CallbackResponce callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentParcelOrderDetailedBinding.inflate(getLayoutInflater());

        preferences = new Preferences(requireContext());
        onlineStatus = preferences.getKeyUserActive();
        Bundle extras = getArguments();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        setAllCLickListner();
        symbol = preferences.getKeyCurrencySymbol();
        if (extras != null) {
            myOrdersModel = (MyOrdersModel) extras.getSerializable("dataModel");

            setUpScreenData();
            callApiShowDetail();
        }


        return binding.getRoot();
    }

    private void setUpScreenData() {
        //User Obj

        userImage = myOrdersModel.orderPersonImage;
        userId = myOrdersModel.orderPersonId;
        userFullName = myOrdersModel.senderName;
        binding.tvUsername.setText(userFullName);

        if (TextUtils.isEmpty(myOrdersModel.senderPhone)) {
            binding.phoneBtn.setVisibility(View.GONE);
        }else {
            binding.phoneBtn.setVisibility(View.VISIBLE);
        }

        binding.ivProfile.setController(Functions.frescoImageLoad(userImage,R.drawable.ic_profile_gray,binding.ivProfile,false));

        //Sender Detail
        binding.tvPickupName.setText(myOrdersModel.senderName);
        String pickupEmail = myOrdersModel.senderEmail;
        if (pickupEmail != null && !pickupEmail.equals("")) {
            binding.tvPickupEmail.setText(pickupEmail);
        }
        else {
            binding.tvPickupEmail.setVisibility(View.GONE);
        }
        senderLocationLat = myOrdersModel.senderLat;
        senderLocationLong = myOrdersModel.senderLong;

        if (senderLocationLat != null && !senderLocationLat.equals("") && (senderLocationLong != null && !senderLocationLong.equals(""))) {
            binding.tvPickupMapit.setVisibility(View.VISIBLE);
        } else {
            binding.tvPickupMapit.setVisibility(View.GONE);
        }


        String pickupNumber = myOrdersModel.senderPhone;
        if (pickupNumber != null && !pickupNumber.equals("")) {
            binding.tvPickupNumber.setText(pickupNumber);
        } else {
            binding.tvPickupNumber.setText(R.string.no_number);
        }



        deliveryAddressLat = myOrdersModel.receiverLat;
        deliveryAddressLong = myOrdersModel.receiverLong;

        receiverAddressDetail = myOrdersModel.recevierLocationString + " " +
                myOrdersModel.receiverAddressDetail;

        if (!myOrdersModel.addressInstructionDropOff.equals("")) {
            receiverAddressDetail = receiverAddressDetail + " \n" + myOrdersModel.addressInstructionDropOff;
        }

        senderAddress = myOrdersModel.senderLocationString + " " + myOrdersModel.senderAddressDetail;

        if (!myOrdersModel.addressInstructionPickUp.equals("")) {
            senderAddress = senderAddress + " \n" + myOrdersModel.addressInstructionPickUp;
        }

        binding.tvPickupAddress.setText(senderAddress);



        setReceipentAdapter();
    }



    DropOffAdapter adapter;
    private void setReceipentAdapter() {
        adapter = new DropOffAdapter(getActivity(), myOrdersModel.recipientList,myOrdersModel.orderMultiStops, new AdapterClickListener() {
            @Override
            public void OnItemClick(int postion, Object Model, View view) {
                RecipientModel item= (RecipientModel) Model;

                switch (view.getId()){
                    case R.id.mapbtn:
                        Functions.openMap(getActivity(),""+item.getRecipientLat(),""+item.getRecipientLat());
                        break;

                    case R.id.phone_btn:
                        Functions.makePhone(getActivity(),item.getRecipientNumber());
                        break;
                }

            }
        });
        binding.recyclerView.setAdapter(adapter);
    }


    void setAllCLickListner() {
        binding.chatBtn.setOnClickListener(this);
        binding.ivMenu.setOnClickListener(this);
        binding.btnRiderStatus.setOnClickListener(this);
        binding.btnOpenSignature.setOnClickListener(this);
        binding.tvPickupMapit.setOnClickListener(this);
        binding.phoneBtn.setOnClickListener(this);
        binding.ivMenuOpen.setOnClickListener(this);
    }


    //this method will fetch the detail of the order
    private void callApiShowDetail() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("parcel_order_id", myOrdersModel.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(), false, false);
        ApiRequest.callApi(getActivity(), ApisList.showRiderOrderDetails, sendobj, resp -> {

            Functions.cancelLoader();
            if (resp != null) {
                try {
                    JSONObject respobj = new JSONObject(resp);
                    if (respobj.getString("code").equals("200")) {

                        parseDeliveryData(respobj);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void parseDeliveryData(JSONObject respobj) {

        try {
            //Rider OBj

            JSONObject msg=respobj.getJSONObject("msg");

            JSONObject riderObj = msg.getJSONObject("Rider");
            riderId = riderObj.optString("id");

            //OrderObj
            JSONObject parcelOrder = msg.getJSONObject("ParcelOrder");
            JSONObject RiderOrder = msg.getJSONObject("RiderOrder");
            JSONArray riderOrderMultiStop = msg.getJSONArray("RiderOrderMultiStop");


            orderid = parcelOrder.optString("id");
            String paymentId = parcelOrder.optString("payment_card_id");
            binding.orderIdTxt.setText(getResources().getString(R.string.order) + orderid);

            if (!TextUtils.isEmpty(paymentId) && !paymentId.equals("0")) {
                if (parcelOrder.has("Payment")) {
                    JSONObject paymentObj = parcelOrder.optJSONObject("Payment");
                    if (paymentObj != null && paymentObj.equals("")) {
                        if (paymentObj.optString("type") != null && !paymentObj.optString("type").equals("")) {
                            binding.tvPaymentType.setText(paymentObj.optString("type"));
                        }
                    }
                }
            } else {
                binding.tvPaymentType.setText(getActivity().getResources().getString(R.string.cash));
            }


            String total = parcelOrder.optString("total");

            if (total != null && !total.equalsIgnoreCase("")) {
                binding.tvTotal.setText(symbol + total);
            }

            myOrdersModel.status=parcelOrder.optString("status");
            if(myOrdersModel.status.equals("0")){
                binding.btnRiderStatus.setVisibility(View.GONE);
                binding.btnOpenSignature.setVisibility(View.GONE);
            }
            else if(myOrdersModel.status.equals("2")){
                binding.phoneBtn.setVisibility(View.GONE);
                binding.chatBtn.setVisibility(View.GONE);
            }

            myOrdersModel.onTheWayToPickup = RiderOrder.getString("on_the_way_to_pickup");
            myOrdersModel.pickupDatetime = RiderOrder.getString("pickup_datetime");
            myOrdersModel.onTheWayToDropoff = RiderOrder.getString("on_the_way_to_dropoff");
            myOrdersModel.delivered = RiderOrder.getString("delivered");
            myOrdersModel.orderMultiStops.clear();
            for (int i=0;i<riderOrderMultiStop.length();i++){
                JSONObject object=riderOrderMultiStop.getJSONObject(i);
                RiderOrderMultiStop orderStatus= new RiderOrderMultiStop();
                orderStatus.id = object.optString("id");
                orderStatus.rider_order_id = object.optString("rider_order_id");
                orderStatus.parcel_order_id = object.optString("parcel_order_id");
                orderStatus.on_the_way_to_pickup = object.optString("on_the_way_to_pickup");
                orderStatus.pickup_datetime = object.optString("pickup_datetime");
                orderStatus.on_the_way_to_dropoff = object.optString("on_the_way_to_dropoff");
                orderStatus.delivered = object.optString("delivered");
                orderStatus.signature = object.optString("signature");
                orderStatus.created = object.optString("created");
                myOrdersModel.orderMultiStops.add(orderStatus);
            }

            showButtonStatus();

        } catch (JSONException e) {
            e.printStackTrace();
            Functions.logDMsg( "exception at detail : " + e.toString());
        }

    }


    public void showButtonStatus(){

       if(myOrdersModel.onTheWayToPickup.equals(emptyTime)){
            binding. btnRiderStatus.setText(getString(R.string.on_the_way_pick));
        }
       else if(myOrdersModel.pickupDatetime.equals(emptyTime)) {
            binding.btnRiderStatus.setText(getString(R.string.order_picked));
        }
       else if (myOrdersModel.orderMultiStops.isEmpty()) {
            binding.btnRiderStatus.setText(getString(R.string.on_the_way));
        }
       else {
            RiderOrderMultiStop item=myOrdersModel.orderMultiStops.get(myOrdersModel.orderMultiStops.size()-1);

            if(myOrdersModel.recipientList.size()==myOrdersModel.orderMultiStops.size() && !TextUtils.isEmpty(item.signature)){
                binding.btnRiderStatus.setText(getString(R.string.order_delivered));
                binding.btnRiderStatus.setEnabled(false);
                binding.btnRiderStatus.setClickable(false);
                binding.upperDiv.setVisibility(View.GONE);
                binding.btnOpenSignature.setVisibility(View.GONE);
            }

            else if (!TextUtils.isEmpty(item.signature)) {

                binding.btnRiderStatus.setEnabled(true);
                binding.btnRiderStatus.setClickable(true);
                binding.upperDiv.setVisibility(View.VISIBLE);
                binding.btnOpenSignature.setVisibility(View.GONE);

                binding.btnRiderStatus.setText(getString(R.string.on_the_way));
            }

            else if(item.on_the_way_to_dropoff.equals(emptyTime)) {
                binding.btnRiderStatus.setText(getString(R.string.on_the_way));

            }

            else if(item.delivered.equals(emptyTime)){
                binding.btnRiderStatus.setText(getString(R.string.delivered));
            }

            else if(TextUtils.isEmpty(item.signature)){
                binding.btnRiderStatus.setText(getString(R.string.order_delivered));
                binding.btnRiderStatus.setEnabled(false);
                binding.btnRiderStatus.setClickable(false);
                binding.upperDiv.setVisibility(View.GONE);
                binding.btnOpenSignature.setVisibility(View.VISIBLE);
                callFirebaseToDeleteChat();
                binding.btnRiderStatus.setText(getString(R.string.delivered));
            }

        }

       adapter.notifyDataSetChanged();
    }


    private void callApiUpdateRiderOrderStatus(String status) {
        JSONObject sendobj = new JSONObject();

        try {

            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            String newString = time.replaceAll("\\/", "-");
            sendobj.put("parcel_order_id", myOrdersModel.id);
            sendobj.put(status, newString + "");

            if(!myOrdersModel.orderMultiStops.isEmpty()) {
                RiderOrderMultiStop item = myOrdersModel.orderMultiStops.get(myOrdersModel.orderMultiStops.size() - 1);
                if (TextUtils.isEmpty(item.signature))
                    sendobj.put("rider_order_multi_stop_id", item.id);

                if(status.equals("delivered") && myOrdersModel.recipientList.size() == myOrdersModel.orderMultiStops.size()){
                    sendobj.put("completed", "1");
                }
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.showLoader(getActivity(), false, false);
        ApiRequest.callApi(getActivity(), ApisList.updateRiderOrderParcelOrderStatus, sendobj, resp -> {
            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {

                        JSONObject msg = respobj.getJSONObject("msg");
                        JSONObject RiderOrder = msg.getJSONObject("RiderOrder");
                        JSONArray riderOrderMultiStop = msg.getJSONArray("RiderOrderMultiStop");

                        myOrdersModel.onTheWayToPickup = RiderOrder.getString("on_the_way_to_pickup");
                        myOrdersModel.pickupDatetime = RiderOrder.getString("pickup_datetime");
                        myOrdersModel.onTheWayToDropoff = RiderOrder.getString("on_the_way_to_dropoff");
                        myOrdersModel.delivered = RiderOrder.getString("delivered");

                        myOrdersModel.orderMultiStops.clear();
                        for (int i=0;i<riderOrderMultiStop.length();i++){
                            JSONObject object=riderOrderMultiStop.getJSONObject(i);
                            RiderOrderMultiStop orderStatus= new RiderOrderMultiStop();
                            orderStatus.id = object.optString("id");
                            orderStatus.rider_order_id = object.optString("rider_order_id");
                            orderStatus.parcel_order_id = object.optString("parcel_order_id");
                            orderStatus.on_the_way_to_pickup = object.optString("on_the_way_to_pickup");
                            orderStatus.pickup_datetime = object.optString("pickup_datetime");
                            orderStatus.on_the_way_to_dropoff = object.optString("on_the_way_to_dropoff");
                            orderStatus.delivered = object.optString("delivered");
                            orderStatus.signature = object.optString("signature");
                            orderStatus.created = object.optString("created");
                            myOrdersModel.orderMultiStops.add(orderStatus);
                        }

                        showButtonStatus();



                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void callFirebaseToDeleteChat() {

        databaseReference.child("chat").child(userId + "-" + riderId + "-" + orderid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //auto generated method stub
            }
        });

        databaseReference.child("chat").child(riderId + "-" + userId + "-" + orderid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.chat_btn:

                if (myOrdersModel.orderType.equals("food")) {
                    chatFragment(myOrdersModel.orderPersonId, myOrdersModel.orderPersonName, myOrdersModel.orderPersonImage, orderid, "user_rider_chat");
                } else {
                    chatFragment(userId, userFullName, userImage, orderid, "user_rider_chat");
                }

                break;

            case R.id.iv_menu:
                getActivity().onBackPressed();
                break;

            case R.id.btn_Rider_status:
                if (onlineStatus.equals("1")) {
                    if (!(binding.btnRiderStatus.getText().toString()).equals(orderDeliveredSuccessfully)) {
                        String btnStatus = binding.btnRiderStatus.getText().toString();
                        if (btnStatus.equals(getActivity().getResources().getString(R.string.on_the_way_pick))) {
                            status = "on_the_way_to_pickup";
                        } else if (btnStatus.equals(getActivity().getResources().getString(R.string.order_picked))) {
                            status = "pickup_datetime";
                        } else if (btnStatus.equals(getActivity().getResources().getString(R.string.on_the_way))) {
                            status = "on_the_way_to_dropoff";
                        } else if (btnStatus.equals(getActivity().getResources().getString(R.string.delivered))) {
                            status = "delivered";
                        }

                        callApiUpdateRiderOrderStatus(status);
                    }
                } else {
                    Functions.showAlert(getActivity(), getActivity().getResources().getString(R.string.alert), getString(R.string.online_first));
                }


                break;

            case R.id.btn_Open_signature:

                Intent intent4 = new Intent(getActivity(), GetSignatureA.class);
                Bundle dataBundle4 = new Bundle();
                dataBundle4.putString(stOrderId, myOrdersModel.id);
                dataBundle4.putString("multiDropId", myOrdersModel.orderMultiStops.get(myOrdersModel.orderMultiStops.size()-1).id);
                dataBundle4.putString("order_type", myOrdersModel.orderType);

                intent4.putExtras(dataBundle4);
                galleryLauncher.launch(intent4);

                break;

            case R.id.tv_pickup_mapit:

                Functions.openMap(getActivity(),senderLocationLat,senderLocationLong);

                break;


            case R.id.phone_btn:
                Functions.makePhone(getActivity(),myOrdersModel.senderPhone);
                break;

            case R.id.iv_menu_open:
                openMenuTab(binding.ivMenuOpen);
                break;

            default:
                break;

        }
    }



    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String receiverid, String name, String picture, String orderId, String type) {

        Intent intent=new Intent(getActivity(),ChatA.class);
        intent.putExtra("senderid", preferences.getKeyUserId());
        intent.putExtra("user_id", receiverid);
        intent.putExtra("user_name", name);
        intent.putExtra("user_img", picture);
        intent.putExtra("fragment", "rider_user_chat");
        intent.putExtra("order_id", orderId);
        intent.putExtra("type", type);
        startActivity(intent);
    }


    public void openMenuTab(View anchorView) {
        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, anchorView);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.setGravity(Gravity.TOP | Gravity.END);
        }
        popup.show();
        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.report_problem:
                    chatFragment("0", "Admin", "user_image", "", "admin");
                    break;


                default:
                    break;

            }
            return true;
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (callback != null) {
            callback.responce(status);
            status = null;
        }
    }
}
