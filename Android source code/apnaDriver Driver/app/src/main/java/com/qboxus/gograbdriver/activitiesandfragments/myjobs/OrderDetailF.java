package com.qboxus.gograbdriver.activitiesandfragments.myjobs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.ChatA;
import com.qboxus.gograbdriver.adapters.FoodListAdapter;
import com.qboxus.gograbdriver.appinterfaces.AdapterClickListener;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.FoodListModel;
import com.qboxus.gograbdriver.models.MyOrdersModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class OrderDetailF extends RootFragment implements View.OnClickListener {

    final String stOrderId = "order_id";
    TextView tvUsername, tvPickupName, tvPickupMapit, tvDropoffInfoName, tvDropoffInfoNumber;
    TextView tvDropoffInfoEmail, tvDropoffInfoMapit, tvDropoffInfoAddress, tvPaymentType, tvTotal, tvPickupAddress;
    TextView orderIdTxt, tv_deliveryInstruction, tvPickupNumber, tvPickupEmail, itemNameTxt;
    ReadMoreTextView itemDescTxt;
    String orderid, id, senderLocationLong, userId, userFullName, userImage, senderLocationLat, signature, status, selectedPos;
    String deliveryAddressLat, deliveryAddressLong, riderId, userPhone;
    Button btnRiderStatus, btnOpenSignature;
    SimpleDraweeView ivProfile;
    RelativeLayout reportBtn;
    LinearLayout upperDiv, buttonDiv;
    RelativeLayout phoneBtn;
    String emptyTime = "0000-00-00 00:00:00";
    String orderDeliveredSuccessfully = "Order Delivered Successfully";
    String symbol;
    View view;
    CallbackResponce callback;
    String onlineStatus;
    Preferences preferences;
    MyOrdersModel myOrdersModel;
    RecyclerView totalItemsRecyclerView;
    ArrayList<FoodListModel> modelArrayList = new ArrayList<>();
    FoodListAdapter foodListAdapter;
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String isSigned = data.getStringExtra("result");
                    if (isSigned.equals("ok")) {
                        btnOpenSignature.setVisibility(View.GONE);
                    }
                }
            });
    String receiverAddressDetail, senderAddress;
    private DatabaseReference databaseReference;

    public OrderDetailF() {
        //required empty constructor
    }

    public OrderDetailF(CallbackResponce callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_detailed, container, false);
        preferences = new Preferences(view.getContext());
        onlineStatus = preferences.getKeyUserActive();
        Bundle extras = getArguments();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        initViews();
        setAllCLickListner();
        methodSetYourItemsAdapter();
        symbol = preferences.getKeyCurrencySymbol();
        if (extras != null) {
            myOrdersModel = (MyOrdersModel) extras.getSerializable("dataModel");
            selectedPos = extras.getString("selected_pos");

            if (selectedPos != null && selectedPos.equals("0")) {
                btnRiderStatus.setVisibility(View.GONE);
                view.findViewById(R.id.phone_btn).setVisibility(View.GONE);
                view.findViewById(R.id.chat_btn).setVisibility(View.GONE);
            }


            if (selectedPos != null && selectedPos.equals("3")) {
                view.findViewById(R.id.phone_btn).setVisibility(View.GONE);
                view.findViewById(R.id.chat_btn).setVisibility(View.GONE);
            }


            if (myOrdersModel.orderType.equals("food")) {
                view.findViewById(R.id.layout_parcel).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.food_recycler_layout).setVisibility(View.GONE);
            }


            id = myOrdersModel.id;
            setUpScreenData();
            callApiShowDetail();
        }


        if (myOrdersModel != null) {
            id = myOrdersModel.id;
            if (!myOrdersModel.delivered.equals(emptyTime)) {
                btnRiderStatus.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ractengle_less_round_solid_black));
                btnRiderStatus.setText(getString(R.string.order_delivered));
                btnRiderStatus.setEnabled(false);
                btnRiderStatus.setClickable(false);
                upperDiv.setVisibility(View.GONE);
                if (signature.equals("")) {
                    btnOpenSignature.setVisibility(View.VISIBLE);
                }
            } else if (!(myOrdersModel.onTheWayToDropoff.equals(emptyTime))) {
                btnRiderStatus.setText(getString(R.string.delivered));
            } else if (!(myOrdersModel.pickupDatetime.equals(emptyTime))) {
                btnRiderStatus.setText(getString(R.string.on_the_way));
            } else if (!(myOrdersModel.onTheWayToPickup.equals(emptyTime))) {
                btnRiderStatus.setText(getString(R.string.order_picked));
            } else {
                btnRiderStatus.setText(getString(R.string.on_the_way_pick));
            }
        } else {
            btnRiderStatus.setText("Sorry Data receving error");
        }

        return view;
    }

    private void setUpScreenData() {

        //User Obj

        userPhone = myOrdersModel.orderPersonPhone;
        userImage = myOrdersModel.orderPersonImage;
        userId = myOrdersModel.orderPersonId;
        userFullName = myOrdersModel.senderName;
        tvUsername.setText(userFullName);
        if (userPhone == null && userPhone.equals("")) {
            phoneBtn.setVisibility(View.GONE);
        }
        ivProfile.setController(Functions.frescoImageLoad(userImage,R.drawable.ic_profile_gray,ivProfile,false));

        //Sender Detail
        tvPickupName.setText(myOrdersModel.senderName);
        String pickupEmail = myOrdersModel.senderEmail;
        if (pickupEmail != null && !pickupEmail.equals("")) {
            tvPickupEmail.setText(pickupEmail);
        } else {
            tvPickupEmail.setVisibility(View.GONE);
        }
        senderLocationLat = myOrdersModel.senderLat;
        senderLocationLong = myOrdersModel.senderLong;

        signature = myOrdersModel.signature;

        if (senderLocationLat != null && !senderLocationLat.equals("") && (senderLocationLong != null && !senderLocationLong.equals(""))) {
            tvPickupMapit.setVisibility(View.VISIBLE);
        } else {
            tvPickupMapit.setVisibility(View.GONE);
        }


        String pickupNumber = myOrdersModel.senderPhone;
        if (pickupNumber != null && !pickupNumber.equals("")) {
            tvPickupNumber.setText(pickupNumber);
        } else {
            tvPickupNumber.setText(R.string.no_number);
        }


        //Reciever Detail
        tvDropoffInfoName.setText(myOrdersModel.orderPersonName);

        String deliveryEmail = myOrdersModel.orderPersonEmail;
        if (deliveryEmail != null && !deliveryEmail.equals("")) {
            tvDropoffInfoEmail.setText(deliveryEmail);
        } else {
            tvDropoffInfoEmail.setText(R.string.no_email);
            tvDropoffInfoEmail.setVisibility(View.GONE);
        }

        deliveryAddressLat = myOrdersModel.receiverLat;

        deliveryAddressLong = myOrdersModel.receiverLong;

        if (deliveryAddressLat != null && !deliveryAddressLat.equals("") && (deliveryAddressLong != null && !deliveryAddressLong.equals(""))) {
            tvDropoffInfoMapit.setVisibility(View.VISIBLE);
        } else {
            tvDropoffInfoMapit.setVisibility(View.GONE);
        }

        tvDropoffInfoAddress.setText(myOrdersModel.recevierLocationString);

        receiverAddressDetail = myOrdersModel.recevierLocationString + " " +
                myOrdersModel.receiverAddressDetail;

        if (!myOrdersModel.addressInstructionDropOff.equals("")) {
            receiverAddressDetail = receiverAddressDetail + " \n" + myOrdersModel.addressInstructionDropOff;
        }

        senderAddress = myOrdersModel.senderLocationString + " " + myOrdersModel.senderAddressDetail;

        if (!myOrdersModel.addressInstructionPickUp.equals("")) {
            senderAddress = senderAddress + " \n" + myOrdersModel.addressInstructionPickUp;
        }

        tvDropoffInfoAddress.setText(receiverAddressDetail);

        tvPickupAddress.setText(senderAddress);
        if (!myOrdersModel.deliveryInstruction.equals("")) {
            tv_deliveryInstruction.setText(myOrdersModel.deliveryInstruction);
        }else{
            view.findViewById(R.id.instruction_div).setVisibility(View.GONE);
            view.findViewById(R.id.line_below_instruction).setVisibility(View.GONE);
        }
        String deliveryNumber = myOrdersModel.orderPersonPhone;

        if (deliveryNumber != null && !deliveryNumber.equals("")) {
            tvDropoffInfoNumber.setText(deliveryNumber);
        } else {
            tvDropoffInfoNumber.setText(R.string.no_number);
        }

        if (myOrdersModel.orderType.equals("food")) {
            view.findViewById(R.id.item_description_layout).setVisibility(View.GONE);
            view.findViewById(R.id.line_below_description).setVisibility(View.GONE);
        }

    }

    private void methodSetYourItemsAdapter() {

        foodListAdapter = new FoodListAdapter(getActivity(), modelArrayList, new AdapterClickListener() {
            @Override
            public void OnItemClick(int postion, Object Model, View view) {

            }
        });

        totalItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        totalItemsRecyclerView.setAdapter(foodListAdapter);
        foodListAdapter.notifyDataSetChanged();

    }


    // initalize the view that is used in this screen
    void initViews() {

        orderIdTxt = view.findViewById(R.id.order_id_txt);

        phoneBtn = view.findViewById(R.id.phone_btn);

        buttonDiv = view.findViewById(R.id.div_button);

        tvPickupNumber = view.findViewById(R.id.tv_pickup_number);

        tvUsername = view.findViewById(R.id.tv_username);

        tvPickupEmail = view.findViewById(R.id.tv_pickup_email);

        tvPickupName = view.findViewById(R.id.tv_pickup_name);

        tvPaymentType = view.findViewById(R.id.tv_payment_type);

        tvDropoffInfoName = view.findViewById(R.id.tv_dropoff_info_name);

        tvDropoffInfoNumber = view.findViewById(R.id.tv_dropoff_info_number);

        tvDropoffInfoEmail = view.findViewById(R.id.tv_dropoff_info_email);

        tvDropoffInfoMapit = view.findViewById(R.id.tv_dropoff_info_mapit);

        tvDropoffInfoAddress = view.findViewById(R.id.tv_dropoff_info_address);

        tvPickupMapit = view.findViewById(R.id.tv_pickup_mapit);

        tvTotal = view.findViewById(R.id.tv_total);

        tvPickupAddress = view.findViewById(R.id.tv_pickup_address);

        ivProfile = view.findViewById(R.id.iv_profile);

        upperDiv = view.findViewById(R.id.upper_div);

        btnRiderStatus = view.findViewById(R.id.btn_Rider_status);

        btnOpenSignature = view.findViewById(R.id.btn_Open_signature);

        reportBtn = view.findViewById(R.id.iv_menu_open);

        itemNameTxt = view.findViewById(R.id.item_name_txt);

        itemDescTxt = view.findViewById(R.id.item_desc_txt);

        totalItemsRecyclerView = view.findViewById(R.id.recylerview);

        tv_deliveryInstruction = view.findViewById(R.id.tv_deliveryInstruction);

    }

    void setAllCLickListner() {
        view.findViewById(R.id.chat_btn).setOnClickListener(this);
        view.findViewById(R.id.iv_menu).setOnClickListener(this);
        btnRiderStatus.setOnClickListener(this);
        btnOpenSignature.setOnClickListener(this);
        tvPickupMapit.setOnClickListener(this);
        tvDropoffInfoMapit.setOnClickListener(this);
        view.findViewById(R.id.phone_btn).setOnClickListener(this);
        view.findViewById(R.id.iv_menu_open).setOnClickListener(this);
    }


    //this method will fetch the detail of the order
    private void callApiShowDetail() {
        JSONObject sendobj = new JSONObject();

        try {
            if (myOrdersModel.orderType.equals("food")) {
                sendobj.put("food_order_id", id);
            } else {
                sendobj.put("parcel_order_id", id);
            }

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
                        if (myOrdersModel.orderType.equals("food")) {
                            parseData(respobj);
                        } else {
                            parseDeliveryData(respobj);
                        }

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
            JSONObject riderObj = respobj.getJSONObject("msg").getJSONObject("Rider");
            riderId = riderObj.optString("id");

            //OrderObj
            JSONObject orderObj = respobj.getJSONObject("msg").getJSONObject("ParcelOrder");
            JSONObject RiderOrder = respobj.getJSONObject("msg").getJSONObject("RiderOrder");

            JSONObject packageSizeObj = orderObj.getJSONObject("PackageSize");

            orderid = orderObj.optString("id");
            String paymentId = orderObj.optString("payment_card_id");
            orderIdTxt.setText(getResources().getString(R.string.order) + orderid);

            itemNameTxt.setText(orderObj.optString("item_title"));

            itemDescTxt.setText(packageSizeObj.optString("title"));

            if (!paymentId.equals("0")) {
                if (orderObj.has("Payment")) {
                    JSONObject paymentObj = orderObj.optJSONObject("Payment");
                    if (paymentObj != null && paymentObj.equals("")) {
                        if (paymentObj.optString("type") != null && !paymentObj.optString("type").equals("")) {
                            tvPaymentType.setText(paymentObj.optString("type"));
                        }
                    }
                }
            } else {
                tvPaymentType.setText(getActivity().getResources().getString(R.string.cash));
            }


            String delivered = RiderOrder.optString("delivered");
            if (!delivered.equals(emptyTime) && selectedPos.equals("3")) {
                if(!orderObj.optString("signature").equals("")){
                    btnOpenSignature.setVisibility(View.GONE);
                }
            }

            String total = orderObj.optString("total");

            if (total != null && !total.equalsIgnoreCase("")) {
                tvTotal.setText(symbol + total);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Functions.logDMsg( "exception at detail : " + e.toString());
        }

    }

    //this method will parse the data coming from api
    private void parseData(JSONObject respobj) {
        try {

            //Rider OBj
            JSONObject riderObj = respobj.getJSONObject("msg").getJSONObject("Rider");
            riderId = riderObj.optString("id");

            //OrderObj
            JSONObject orderObj = respobj.getJSONObject("msg").getJSONObject("FoodOrder");
            JSONObject riderOrderObj = respobj.getJSONObject("msg").getJSONObject("RiderOrder");
            orderid = orderObj.optString("id");
            String paymentId = orderObj.optString("payment_card_id");
            orderIdTxt.setText(getResources().getString(R.string.order) + orderid);

            JSONArray jsonArray = orderObj.optJSONArray("FoodOrderMenuItem");

            for (int x = 0; x < jsonArray.length(); x++) {
                JSONObject jsonObject = jsonArray.getJSONObject(x);
                FoodListModel foodListModel = new FoodListModel();
                foodListModel.setMenuId(jsonObject.optString("id"));
                foodListModel.setItemName(jsonObject.optString("name"));
                foodListModel.setTvQuantity(jsonObject.optString("quantity"));
                foodListModel.setAmount(jsonObject.optString("price"));
                foodListModel.setImage(jsonObject.optString("image"));
                modelArrayList.clear();
                JSONArray foodOrderMenuExtraItem = jsonObject.optJSONArray("FoodOrderMenuExtraItem");
                ArrayList<HashMap<String, String>> extraItem = new ArrayList<>();
                for (int a = 0; a < foodOrderMenuExtraItem.length(); a++) {
                    JSONObject object = foodOrderMenuExtraItem.getJSONObject(a);
                    HashMap<String, String> names = new HashMap<>();
                    names.put("menu_extra_item_id", object.optString("id"));
                    names.put("menu_extra_item_name", object.optString("name"));
                    names.put("menu_extra_item_price", object.optString("price"));
                    names.put("menu_extra_item_quantity", object.optString("quantity"));
                    extraItem.add(names);
                }
                foodListModel.setExtraItem(extraItem);
                modelArrayList.add(foodListModel);
            }

            foodListAdapter.notifyDataSetChanged();


            if (!paymentId.equals("0")) {
                if (orderObj.has("Payment")) {
                    JSONObject paymentObj = orderObj.optJSONObject("Payment");
                    if (paymentObj != null && paymentObj.equals("")) {
                        if (paymentObj.optString("type") != null && !paymentObj.optString("type").equals("")) {
                            view.findViewById(R.id.payment_div).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.line_below_payment).setVisibility(View.VISIBLE);
                            tvPaymentType.setText(paymentObj.optString("type"));
                        }
                    }
                }
            } else {
                tvPaymentType.setText(getActivity().getResources().getString(R.string.cash));
            }

            String total = orderObj.optString("price");


            String delivered = riderOrderObj.optString("delivered");
            if (!delivered.equals(emptyTime) && selectedPos.equals("3")) {
                if(!orderObj.optString("signature").equals("")){
                    btnOpenSignature.setVisibility(View.GONE);
                }

            }


            if (total != null && !total.equalsIgnoreCase("")) {
                tvTotal.setText(symbol + total);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //call api for update order status


    private void callApiUpdateRiderOrderStatus(String status) {
        JSONObject sendobj = new JSONObject();

        try {

            String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            String newString = time.replaceAll("\\/", "-");

            if (myOrdersModel.orderType.equals("food")) {
                sendobj.put("food_order_id", id);
            } else {
                sendobj.put("parcel_order_id", id);
            }

            sendobj.put(status, newString + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.showLoader(getActivity(), false, false);
        ApiRequest.callApi(getActivity(), ApisList.updateRiderOrderStatus, sendobj, resp -> {
            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {

                        JSONObject user = respobj.getJSONObject("msg");

                        JSONObject riderOrder = user.getJSONObject("RiderOrder");

                        MyOrdersModel myOrdersModel = new MyOrdersModel();

                        myOrdersModel.onTheWayToPickup = riderOrder.getString("on_the_way_to_pickup");
                        myOrdersModel.pickupDatetime = riderOrder.getString("pickup_datetime");
                        myOrdersModel.onTheWayToDropoff = riderOrder.getString("on_the_way_to_dropoff");
                        myOrdersModel.delivered = riderOrder.getString("delivered");

                        if (!myOrdersModel.delivered.equals(emptyTime)) {
                            btnRiderStatus.setText(getString(R.string.order_delivered));
                            btnRiderStatus.setEnabled(false);
                            btnRiderStatus.setClickable(false);
                            btnRiderStatus.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ractengle_less_round_solid_black));
                            if (signature.equals("")) {
                                btnOpenSignature.setVisibility(View.VISIBLE);
                            }
                            callFirebaseToDeleteChat();
                        } else if (!(myOrdersModel.onTheWayToDropoff.equals(emptyTime))) {
                            btnRiderStatus.setText(getString(R.string.delivered));
                        } else if (!(myOrdersModel.pickupDatetime.equals(emptyTime))) {
                            btnRiderStatus.setText(getString(R.string.on_the_way));
                        } else if (!(myOrdersModel.onTheWayToPickup.equals(emptyTime))) {
                            btnRiderStatus.setText(getString(R.string.order_picked));
                        } else {
                            btnRiderStatus.setText(getString(R.string.on_the_way_pick));
                        }
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
                    if (!(btnRiderStatus.getText().toString()).equals(orderDeliveredSuccessfully)) {
                        String btnStatus = btnRiderStatus.getText().toString();
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
                dataBundle4.putString(stOrderId, id);

                dataBundle4.putString("order_type", myOrdersModel.orderType);

                intent4.putExtras(dataBundle4);
                galleryLauncher.launch(intent4);

                break;

            case R.id.tv_pickup_mapit:

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("www.google.com")
                        .appendPath("maps")
                        .appendPath("dir")
                        .appendPath("")
                        .appendQueryParameter("api", "1")
                        .appendQueryParameter("destination", senderLocationLat + "," + senderLocationLong);
                String url = builder.build().toString();
                Log.d("Directions", url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

            case R.id.tv_dropoff_info_mapit:

                Uri.Builder builder1 = new Uri.Builder();
                builder1.scheme("https")
                        .authority("www.google.com")
                        .appendPath("maps")
                        .appendPath("dir")
                        .appendPath("")
                        .appendQueryParameter("api", "1")
                        .appendQueryParameter("destination", deliveryAddressLat + "," + deliveryAddressLong);
                String url1 = builder1.build().toString();
                Log.d("Directions", url1);
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(url1));
                startActivity(i1);

                break;


            case R.id.phone_btn:

                String number = tvDropoffInfoNumber.getText().toString();
                if (number != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", number, null));
                    startActivity(intent);
                }

                break;

            case R.id.iv_menu_open:
                openMenuTab(reportBtn);
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
