package com.qboxus.gograbdriver.activitiesandfragments.rideandrequest;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.gograbdriver.Constants;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.ChatA;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.appinterfaces.LocationServiceCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.mapclasses.MapAnimator;
import com.qboxus.gograbdriver.mapclasses.MapWorker;
import com.qboxus.gograbdriver.models.RideModel;
import com.qboxus.gograbdriver.models.RideRequestModel;
import com.qboxus.gograbdriver.models.UserModel;
import com.qboxus.gograbdriver.models.VehicleModel;
import com.qboxus.gograbdriver.R;
import com.qboxus.gograbdriver.services.BackgroundLocationService;
import com.qboxus.gograbdriver.services.BackgroundSoundService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RideActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationServiceCallback,
        View.OnClickListener {


    ImageView earthBtn, currentLocationBtn, btnRouteShow;
    TextView tvPickupLocDetail, tvDropoffLocDetail, tvCustomerEmail, tvCustomerName, tvEstimatedFare, tvEstimatedDistence;
    View riderStatusView;
    CoordinatorLayout bottomView;
    SimpleDraweeView imgCustomer;
    RelativeLayout tabLocationView;
    LinearLayout tabButtonView, tabRlSendMsgCall, btnDecline, btnAccept, btnStartNavigation,
            btnChat, btnCall, btnStatusChange, firstStepView, anchorView;
    LinearLayout tabStepOne, tabStepTwo;
    BottomSheetBehavior behavior;
    TextView stepTwoTvPickupTime, stepTwoTvPickupAddress, tvStatusChange, tvRideWithYou;
    final double avgSpeed = 0;
    RideModel rideModel;
    boolean isRoutShow = true;
    //map operation
    Preferences preferences;
    int trackingStep = 5;
    int trackingZoom = 0;
    public final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.hasExtra("type")) {

                    Functions.logDMsg("type"+intent.getStringExtra("type"));
                    if (intent.getStringExtra("type").equalsIgnoreCase("ride_cancel")) {
                        stopRideMusicService();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DriversTrips");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.removeLocation(preferences.getKeyRequestId() + "_" + preferences.getKeyUserId());
                        preferences.setKeyRequestId("0");
                        preferences.setKeyTripId("0");
                        preferences.setKeyRideTotalDistance(0.0f);
                        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nMgr.cancelAll();

                        Functions.clearFragment(getSupportFragmentManager());
                        Intent moveintent = new Intent(RideActivity.this, MainActivity.class);
                        moveintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(moveintent);

                    } else {
                        checkUserRequest();
                    }

                }
            } catch (Exception e) {
                Functions.logDMsg( "Error : Notification " + e);
            }
        }
    };
    float zoom = 0f;
    boolean isDefultZoom = false;
    BackgroundLocationService mService;
    boolean mBound = false;
    private MapView mapView;
    private GoogleMap googleMap;
    private MapWorker mapWorker;
    private Marker pickupMarker, dropoffMarker, mymarker;
    private LatLng pickupLatlng, dropoffLatlng, myLatlng;
    private final double EARTHRADIUS = 6366198;
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            BackgroundLocationService.LocalBinder binder = (BackgroundLocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setCallbacks(RideActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_ride);

        preferences = new Preferences(RideActivity.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale("" + preferences.getKeyLocale());
        mapView = findViewById(R.id.map_main_layout);
        mapView.onCreate(savedInstanceState);
        rideModel = new RideModel();
        zoom = Variables.mapZoomLevel;
        methodSetupmap();
        methodFindviewbyid();
        checkUserRequest();

    }

    private void methodFindviewbyid() {

        tvPickupLocDetail = findViewById(R.id.tv_pickup_loc_detail);
        tvDropoffLocDetail = findViewById(R.id.tv_dropoff_loc_detail);
        riderStatusView = findViewById(R.id.rider_status_view);
        firstStepView = findViewById(R.id.first_step_view);
        anchorView = findViewById(R.id.anchor_view);
        tabLocationView = findViewById(R.id.tab_rl_location);
        tabButtonView = findViewById(R.id.tab_rl_button);
        btnDecline = findViewById(R.id.btn_decline);
        btnDecline.setOnClickListener(this);
        btnAccept = findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(this);
        btnStartNavigation = findViewById(R.id.btn_start_navigation);
        btnStartNavigation.setOnClickListener(this);
        tabRlSendMsgCall = findViewById(R.id.tab_rl_send_msg_call);
        tabStepOne = findViewById(R.id.tab_step_one);
        tabStepTwo = findViewById(R.id.tab_step_two);
        btnStatusChange = findViewById(R.id.btn_status_change);
        btnStatusChange.setOnClickListener(this);
        tvStatusChange = findViewById(R.id.tv_status_change);
        tvRideWithYou = findViewById(R.id.tv_ride_with_you);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvEstimatedFare = findViewById(R.id.tv_estimated_fare);
        tvEstimatedDistence = findViewById(R.id.tv_estimated_distence);
        tvCustomerEmail = findViewById(R.id.tv_customer_email);
        bottomView = findViewById(R.id.bottom_view);
        imgCustomer = findViewById(R.id.img_customer);
        btnChat = findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(this);
        btnCall = findViewById(R.id.btn_call);
        btnCall.setOnClickListener(this);

        stepTwoTvPickupTime = findViewById(R.id.step_two_tv_pickup_time);
        stepTwoTvPickupAddress = findViewById(R.id.step_two_tv_pickup_address);


        earthBtn = findViewById(R.id.earth_btn);
        earthBtn.setOnClickListener(this);

        btnRouteShow = findViewById(R.id.route_btn);
        btnRouteShow.setOnClickListener(this);

        findViewById(R.id.directionBtn).setOnClickListener(this);

        currentLocationBtn = findViewById(R.id.current_location_btn);
        currentLocationBtn.setOnClickListener(this);
        behavior = BottomSheetBehavior.from(riderStatusView);

//        bottom sheet transitoion
        riderStatusView.setVisibility(View.VISIBLE);
        ((View) riderStatusView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        tabRlSendMsgCall.setVisibility(View.GONE);
        tabButtonView.setVisibility(View.VISIBLE);
        tabLocationView.setVisibility(View.VISIBLE);


        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    anchorView.animate().alpha(0).setDuration(200).start();
                } else {
                    anchorView.animate().alpha(1).setDuration(200).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void methodSetupmap() {
        MapsInitializer.initialize(RideActivity.this);
        mapView.onResume();

        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        mapWorker = new MapWorker(RideActivity.this, googleMap);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    public void checkUserRequest() {
        JSONObject params = new JSONObject();

        try {
            params.put("user_id", preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(this, ApisList.showActiveRequest, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {


                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        parseRideStatusResponce(resp);
                    } else {
                        stopRideMusicService();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DriversTrips");
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.removeLocation(preferences.getKeyRequestId() + "_" + preferences.getKeyUserId());
                        preferences.setKeyRequestId("0");
                        preferences.setKeyTripId("0");
                        preferences.setKeyRideTotalDistance(0.0f);
                        Functions.showToast(RideActivity.this, "" + jsonObject.optString("msg"));
                        Functions.clearFragment(getSupportFragmentManager());
                        Intent intent = new Intent(RideActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public void acceptRequest(String status) {
        JSONObject params = new JSONObject();
        try {
            params.put("driver_id", "" + preferences.getKeyUserId());
            params.put("request_id", "" + rideModel.getRideRequestModel().getId());
            params.put("request", status);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(this, false, false);
        ApiRequest.callApi(this, ApisList.driverResponseAgainstRequest, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {

                        JSONObject jsonObject = new JSONObject(resp);
                        String code = jsonObject.optString("code");

                        if (code.equals("200")) {
                            parseRideStatusResponce(resp);
                        } else {
                            stopRideMusicService();

                            Functions.showToast(RideActivity.this, "" + jsonObject.optString("msg"));
                            Functions.clearFragment(getSupportFragmentManager());
                            Intent intent = new Intent(RideActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }


                    } catch (Exception e) {
                        Functions.logDMsg( "Exception at Accept_Request " + e);
                    }
                }

            }
        });


    }

    private void parseRideStatusResponce(String resp) {

        try {

            JSONObject jsonObject = new JSONObject(resp);

            String code = jsonObject.optString("code");
            JSONObject msg = jsonObject.optJSONObject("msg");
            if (code.equals("200")) {
                JSONObject request = msg.getJSONObject("Request");
                JSONObject user = msg.getJSONObject("User");
                JSONObject driver = msg.getJSONObject("Driver");
                JSONObject vehicle = msg.getJSONObject("Vehicle");

                VehicleModel vehicleModel = new VehicleModel();
                {
                    vehicleModel.setId(vehicle.optString("id", ""));
                    vehicleModel.setDriverId(vehicle.optString("driver_id", ""));
                    vehicleModel.setUserId(vehicle.optString("user_id", ""));
                    vehicleModel.setMake(vehicle.optString("make", ""));
                    vehicleModel.setModel(vehicle.optString("model", ""));
                    vehicleModel.setYear(vehicle.optString("year", ""));
                    vehicleModel.setLicensePlate(vehicle.optString("license_plate", ""));
                    vehicleModel.setColor(vehicle.optString("color", ""));
                    vehicleModel.setRideTypeId(vehicle.optString("ride_type_id", ""));
                    vehicleModel.setImage(vehicle.optString("image", ""));
                    vehicleModel.setLat(vehicle.optString("lat", "0"));
                    vehicleModel.setLng(vehicle.optString("long", "0"));
                    vehicleModel.setOnline(vehicle.optString("online", "0"));
                    vehicleModel.setAccUpdated(vehicle.optString("updated", ""));
                    vehicleModel.setAccCreated(vehicle.optString("created", ""));
                }

                rideModel.setVehicleMode(vehicleModel);


                RideRequestModel requestModel = new RideRequestModel();
                {
                    preferences.setKeyRequestId("" + request.optString("id", "0"));

                    requestModel.setId(request.optString("id", ""));
                    requestModel.setUserId(request.optString("user_id", ""));
                    requestModel.setVehicleId(request.optString("vehicle_id", ""));
                    requestModel.setDriverId(request.optString("driver_id", ""));
                    requestModel.setPickupLat(request.optString("pickup_lat", "0"));
                    requestModel.setPickupLng(request.optString("pickup_long", "0"));
                    requestModel.setDestinationLat(request.optString("dropoff_lat", "0"));
                    requestModel.setDestinationLng(request.optString("dropoff_long", "0"));
                    requestModel.setRequest(request.optString("request", ""));
                    requestModel.setDriverResponceDatetime(request.optString("driver_response_datetime", ""));
                    requestModel.setDriverRideResponse(request.optString("driver_ride_response", ""));
                    requestModel.setUserRideResponse(request.optString("user_ride_response", ""));
                    requestModel.setReason(request.optString("reason", ""));
                    requestModel.setOnTheWay(request.optString("on_the_way", ""));
                    requestModel.setArriveOnLocation(request.optString("arrive_on_location", ""));
                    requestModel.setStartRide(request.optString("start_ride", ""));
                    requestModel.setEndRide(request.optString("end_ride", ""));
                    requestModel.setEstimatedFare(request.optString("estimated_fare", "0"));
                    requestModel.setWalletPay(request.optString("wallet_pay", "0"));
                    requestModel.setPaymentType(request.optString("payment_type", ""));
                    requestModel.setPaymentMethodId(request.optString("payment_method_id", ""));
                    requestModel.setCollectPayment(request.optString("collect_payment", "0"));
                    requestModel.setCreated(request.optString("created", ""));

                    if (msg.has("Trip")) {
                        JSONObject trip = msg.getJSONObject("Trip");
                        if (trip != null && trip.length() > 0 && !trip.equals("")) {
                            requestModel.setFinalFare(trip.optString("ride_fare", ""));
                            requestModel.setTrip_fare(trip.optString("trip_fare", ""));
                        }
                    }
                    requestModel.setPickupString(request.optString("pickup_location", ""));
                    requestModel.setDestinationString(request.optString("dropoff_location", ""));

                }

                rideModel.setRideRequestModel(requestModel);

                UserModel userModel = new UserModel();
                {
                    userModel.setId(user.optString("id", ""));
                    userModel.setEmail(user.optString("email", ""));
                    userModel.setFirstName(user.optString("first_name", ""));
                    userModel.setLastName(user.optString("last_name", ""));
                    userModel.setUsername(user.optString("username", ""));
                    userModel.setPhoneNo(user.optString("phone", ""));
                    userModel.setDob(user.optString("dob", ""));
                    userModel.setGender(user.optString("gender", ""));
                    userModel.setImage(user.optString("image", ""));
                    userModel.setDeviceToken("");
                    userModel.setRole(user.optString("role", ""));
                    userModel.setOnlineStatus(user.optString("online", ""));
                    userModel.setLat(user.optString("lat", "0"));
                    userModel.setLng(user.optString("long", "0"));
                    userModel.setWallet(user.optString("wallet", "0"));
                    userModel.setAuthToken(user.optString("token", ""));
                    userModel.setCreated(user.optString("created", ""));
                }

                rideModel.setUserModel(userModel);

                UserModel driverModel = new UserModel();
                {
                    driverModel.setId(driver.optString("id", ""));
                    driverModel.setEmail(driver.optString("email", ""));
                    driverModel.setFirstName(driver.optString("first_name", ""));
                    driverModel.setLastName(driver.optString("last_name", ""));
                    driverModel.setUsername(driver.optString("username", ""));
                    driverModel.setPhoneNo(driver.optString("phone", ""));
                    driverModel.setDob(driver.optString("dob", ""));
                    driverModel.setGender(driver.optString("gender", ""));
                    driverModel.setImage(driver.optString("image", ""));
                    driverModel.setRole(driver.optString("role", ""));
                    driverModel.setDeviceToken("");
                    driverModel.setOnlineStatus(driver.optString("online", ""));
                    driverModel.setLat(driver.optString("lat", "0"));
                    driverModel.setLng(driver.optString("long", "0"));
                    driverModel.setWallet(driver.optString("wallet", "0"));
                    driverModel.setAuthToken(driver.optString("token", ""));
                    driverModel.setCreated(driver.optString("created", ""));
                }

                rideModel.setDriverModel(driverModel);

                updateScreenAction();
            }

        } catch (Exception e) {
            Functions.logDMsg( "Exception at parsing : " + e);
        }
    }

    private void updateScreenAction() {

            if (rideModel != null) {

                imgCustomer.setController(Functions.frescoImageLoad(
                         rideModel.getUserModel().getImage(),
                        R.drawable.image_placeholder,
                        imgCustomer,
                        false
                ));

                double distance = distance(Double.valueOf(rideModel.getRideRequestModel().getPickupLat()), Double.valueOf(rideModel.getRideRequestModel().getPickupLng()),
                        Double.valueOf(rideModel.getRideRequestModel().getDestinationLat()), Double.valueOf(rideModel.getRideRequestModel().getDestinationLng()));

                tvCustomerName.setText("" + rideModel.getUserModel().getFirstName() + " " + rideModel.getUserModel().getLastName());
                tvCustomerEmail.setText("" + rideModel.getUserModel().getEmail());
                if(rideModel.getRideRequestModel().getFinalFare() !=null && !rideModel.getRideRequestModel().getFinalFare().equals("0")) {
                    tvEstimatedFare.setText(preferences.getKeyCurrencySymbol() + " " + rideModel.getRideRequestModel().getFinalFare());
                }else{
                    tvEstimatedFare.setText(preferences.getKeyCurrencySymbol() + " " + rideModel.getRideRequestModel().getEstimatedFare());
                }
                tvEstimatedDistence.setText("" + String.format("%.2f", distance) + " km");
                tvPickupLocDetail.setText("" + rideModel.getRideRequestModel().getPickupString());
                tvDropoffLocDetail.setText("" + rideModel.getRideRequestModel().getDestinationString());
                stepTwoTvPickupTime.setText("" + calculateTime(distance));
                stepTwoTvPickupAddress.setText("" + rideModel.getRideRequestModel().getPickupString());
                if (Integer.valueOf(rideModel.getRideRequestModel().getCollectPayment()) > 0) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DriversTrips");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(preferences.getKeyRequestId() + "_" + preferences.getKeyUserId() + "_" + preferences.getKeyVehicleId());
                    preferences.setKeyRequestId("0");
                    preferences.setKeyTripId("0");
                    preferences.setKeyRideTotalDistance(0.0f);
                    RideCompleteFeedbackF f = new RideCompleteFeedbackF(new FragmentCallback() {
                        @Override
                        public void Responce(Bundle bundle) {

                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user_data", rideModel);
                    f.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top);
                    ft.replace(R.id.ride_status_container, f, "RideCompleteFeedback_F").addToBackStack("RideCompleteFeedback_F").commit();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getEndRide()) > 0) {
                    riderStatusView.setVisibility(View.GONE);
                    RideCompletePaymentF f = new RideCompletePaymentF(new FragmentCallback() {
                        @Override
                        public void Responce(Bundle bundle) {

                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user_data", rideModel);
                    f.setArguments(bundle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    ft.replace(R.id.ride_status_container, f, "RideCompletePayment_F").addToBackStack("RideCompletePayment_F").commit();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getStartRide()) > 0) {

                    btnStatusChange.setVisibility(View.VISIBLE);
                    tabRlSendMsgCall.setVisibility(View.VISIBLE);
                    tabButtonView.setVisibility(View.GONE);
                    tabLocationView.setVisibility(View.VISIBLE);
                    tabStepOne.setVisibility(View.VISIBLE);
                    tabStepTwo.setVisibility(View.GONE);

                    tvStatusChange.setText(RideActivity.this.getString(R.string.end_trip));
                    tvRideWithYou.setText(RideActivity.this.getText(R.string.dropoff) + " " + rideModel.getUserModel().getUsername());
                    removeAllPin();
                    trackingStep = 4;
                    trackingZoom = 0;
                    updateRideSetp();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getArriveOnLocation()) > 0) {
                    preferences.setKeyTripId("0");
                    btnStatusChange.setVisibility(View.VISIBLE);
                    tabRlSendMsgCall.setVisibility(View.VISIBLE);
                    tabButtonView.setVisibility(View.GONE);
                    tabLocationView.setVisibility(View.VISIBLE);
                    tabStepOne.setVisibility(View.VISIBLE);
                    tabStepTwo.setVisibility(View.GONE);

                    tvStatusChange.setText(RideActivity.this.getString(R.string.start_trip));
                    tvRideWithYou.setText(RideActivity.this.getText(R.string.picked) + " " + rideModel.getUserModel().getUsername());
                    removeAllPin();
                    trackingStep = 3;
                    trackingZoom = 0;
                    updateRideSetp();
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getOnTheWay()) > 0) {
                    stopRideMusicService();
                    preferences.setKeyTripId("0");
                    riderStatusView.post(new Runnable() {
                        @Override
                        public void run() {
                            behavior.setPeekHeight((int) getResources().getDimension(R.dimen._140sdp));
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bottomView.getParent().requestLayout();
                        }
                    });


                    btnStatusChange.setVisibility(View.VISIBLE);
                    tabRlSendMsgCall.setVisibility(View.VISIBLE);
                    tabButtonView.setVisibility(View.GONE);
                    tabLocationView.setVisibility(View.VISIBLE);
                    tabStepOne.setVisibility(View.VISIBLE);
                    tabStepTwo.setVisibility(View.GONE);
                    tvRideWithYou.setText(RideActivity.this.getText(R.string.picking_up));
                    removeAllPin();
                    trackingStep = 2;
                    trackingZoom = 0;
                    updateRideSetp();

                } else if (Integer.valueOf(rideModel.getRideRequestModel().getRequest()) == 1) {
                    stopRideMusicService();
                    preferences.setKeyTripId("0");
                    riderStatusView.post(new Runnable() {
                        @Override
                        public void run() {
                            behavior.setPeekHeight((int) getResources().getDimension(R.dimen._80sdp));
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bottomView.getParent().requestLayout();
                        }
                    });

                    tabStepOne.setVisibility(View.GONE);
                    tabStepTwo.setVisibility(View.VISIBLE);
                    removeAllPin();
                    trackingStep = 1;
                    trackingZoom = 0;
                    updateRideSetp();
                } else if (rideModel.getDriverModel().getId().equalsIgnoreCase("136")) {
                    acceptRequest("2");
                } else if (Integer.valueOf(rideModel.getRideRequestModel().getRequest()) == 0) {
                    riderStatusView.post(new Runnable() {
                        @Override
                        public void run() {
                            behavior.setPeekHeight((int) getResources().getDimension(R.dimen._100sdp));
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bottomView.getParent().requestLayout();
                        }
                    });
                    startRideMusicService();
                    tvRideWithYou.setText(rideModel.getUserModel().getUsername() + " " + getString(R.string.want_to_ride_with_you));
                    trackingStep = 0;
                    updateRideSetp();
                }
            }
    }

    private String calculateTime(double distance) {
//        its an avg speed and output time is in second
        double time = distance / avgSpeed;
        time = time / 1000;

        Date date = new Date();
        long timeInMili = (long) (date.getTime() + time);

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm aa"); // modify format
        return formatter.format(new Date(timeInMili));
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    // draw poliline route between points
    private void drawRoute(Marker pickup, Marker dropoff) {

        if (pickup != null && dropoff != null && isRoutShow) {
            mapWorker.drawRoute(RideActivity.this, pickup.getPosition(), dropoff.getPosition(), googleMap);
        } else {
            MapAnimator.getInstance().clearMapRoute();
        }

    }

    private void showLatLngBoundZoom(Marker... marker) {
        if (marker[0] == null && marker[1] == null) {
            return;
        }
        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        for (Marker mrk : marker) {
            try {
                latlngBuilder.include(mrk.getPosition());
            } catch (Exception e) {
                Functions.logDMsg( "Exception at showLatLngBoundZoom: " + e);
            }
        }


        LatLngBounds bounds = latlngBuilder.build();

        LatLng center = bounds.getCenter();
        LatLng northEast = move(center, 709, 709);
        LatLng southWest = move(center, -709, -709);
        latlngBuilder.include(southWest);
        latlngBuilder.include(northEast);
        if (areBoundsTooSmall(bounds, 300)) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), Variables.mapZoomLevel));
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        }

    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    private LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }

    private double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.current_location_btn: {
                if (trackingZoom == 1) {
                    isDefultZoom = true;
                    trackingZoom = 1;
                    updateRideSetp();
                } else {
                    trackingZoom = 1;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, Variables.mapZoomLevel));
                }
            }
            break;

            case R.id.earth_btn:
                if (googleMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    earthBtn.setImageResource(R.drawable.ic_google_earth_two);
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    earthBtn.setImageResource(R.drawable.ic_google_earth_one);
                }
                break;
            case R.id.route_btn: {
                if (isRoutShow) {
                    btnRouteShow.setImageResource(R.drawable.ic_route_black);
                    isRoutShow = false;
                } else {
                    btnRouteShow.setImageResource(R.drawable.ic_route_blue);
                    isRoutShow = true;
                }
                updateRideSetp();
            }
            break;

            case R.id.directionBtn:
                try {

                    pickupLatlng = new LatLng(Double.parseDouble(rideModel.getRideRequestModel().getPickupLat()), Double.parseDouble(rideModel.getRideRequestModel().getPickupLng()));
                    dropoffLatlng = new LatLng(Double.parseDouble(rideModel.getRideRequestModel().getDestinationLat()), Double.parseDouble(rideModel.getRideRequestModel().getDestinationLng()));
                    myLatlng = new LatLng(Double.parseDouble(preferences.getKeyUserLat()), Double.parseDouble(preferences.getKeyUserLng()));
                } catch (Exception e) {
                    Functions.logDMsg( "Exception : UpdateRideSetp " + e);
                }
                if(rideModel.getRideRequestModel().getStartRide().equals("1")
                        || rideModel.getRideRequestModel().getEndRide().equals("1")
                        || rideModel.getRideRequestModel().getCollectPayment().equals("1")){

                    Functions.open_google_map(this,myLatlng,dropoffLatlng);

                }
                else if(rideModel.getRideRequestModel().getRequest().equals("1")
                        || rideModel.getRideRequestModel().getOnTheWay().equals("1") || rideModel.getRideRequestModel().getArriveOnLocation().equals("1")){
                    Functions.open_google_map(this,myLatlng,pickupLatlng);
                }
                break;


            case R.id.btn_decline: {
                acceptRequest("2");
            }
            break;
            case R.id.btn_accept: {
                acceptRequest("1");
            }
            break;
            case R.id.btn_status_change: {
                changeStatus();
            }
            break;
            case R.id.btn_start_navigation: {
                changeStatus();
            }
            break;
            case R.id.btn_chat: {
                openChat();
            }
            break;
            case R.id.btn_call: {
                phoneCall();
            }
            break;

        }
    }

    private void updateRideSetp() {

        try {

            pickupLatlng = new LatLng(Double.parseDouble(rideModel.getRideRequestModel().getPickupLat()), Double.parseDouble(rideModel.getRideRequestModel().getPickupLng()));
            dropoffLatlng = new LatLng(Double.parseDouble(rideModel.getRideRequestModel().getDestinationLat()), Double.parseDouble(rideModel.getRideRequestModel().getDestinationLng()));
            myLatlng = new LatLng(Double.parseDouble(preferences.getKeyUserLat()), Double.parseDouble(preferences.getKeyUserLng()));
        } catch (Exception e) {
            Functions.logDMsg( "Exception : UpdateRideSetp " + e);
        }
        switch (trackingStep) {
            case 0: {
                if (pickupMarker == null && dropoffMarker == null) {
                    if ((pickupMarker == null || pickupLatlng == null) && (dropoffMarker == null || dropoffLatlng == null)) {
                        pickupMarker = mapWorker.addMarker(pickupLatlng, mapWorker.pickupMarkerBitmap);
                        dropoffMarker = mapWorker.addMarker(dropoffLatlng, mapWorker.destinationMarker);
                        showLatLngBoundZoom(pickupMarker, dropoffMarker);
                        drawRoute(pickupMarker, dropoffMarker);
                    }
                } else {

                    switch (trackingZoom) {
                        case 0: {
                            showLatLngBoundZoom(pickupMarker, dropoffMarker);
                            drawRoute(pickupMarker, dropoffMarker);
                        }
                        break;
                        case 1: {
                            showLatLngBoundZoom(pickupMarker, dropoffMarker);
                            drawRoute(pickupMarker, dropoffMarker);
                        }
                        break;

                    }
                }
            }
            break;
            case 1: {
                if (mymarker == null && pickupMarker == null) {
                    if ((mymarker == null || myLatlng == null) && (pickupMarker == null || pickupLatlng == null)) {
                        mymarker = mapWorker.addMarker(myLatlng, mapWorker.carMarker);
                        pickupMarker = mapWorker.addMarker(pickupLatlng, mapWorker.pickupMarkerBitmap);

                        showLatLngBoundZoom(mymarker, pickupMarker);
                        mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                        drawRoute(mymarker, pickupMarker);
                    }

                } else {

                    switch (trackingZoom) {
                        case 0: {

                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            showLatLngBoundZoom(mymarker, pickupMarker);
                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, pickupMarker);
                        }
                        break;
                        case 1: {
                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            if (isDefultZoom) {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom = false;
                            } else {
                                zoom = googleMap.getCameraPosition().zoom;
                            }

                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(myLatlng)
                                            .zoom(zoom).build()));
                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, pickupMarker);
                        }
                        break;

                    }

                }
            }
            break;
            case 2: {

                if (mymarker == null && pickupMarker == null) {
                    if ((mymarker == null || myLatlng == null) && (pickupMarker == null || pickupLatlng == null)) {
                        mymarker = mapWorker.addMarker(myLatlng, mapWorker.carMarker);
                        pickupMarker = mapWorker.addMarker(pickupLatlng, mapWorker.pickupMarkerBitmap);

                        showLatLngBoundZoom(mymarker, pickupMarker);
                        mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                        drawRoute(mymarker, pickupMarker);
                    }

                } else {

                    switch (trackingZoom) {
                        case 0: {
                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            showLatLngBoundZoom(mymarker, pickupMarker);
                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, pickupMarker);
                        }
                        break;
                        case 1: {
                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            if (isDefultZoom) {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom = false;
                            } else {
                                zoom = googleMap.getCameraPosition().zoom;
                            }
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(myLatlng)
                                            .zoom(zoom).build()));

                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, pickupMarker);
                        }
                        break;

                    }

                }
            }
            break;
            case 3: {
                if (mymarker == null && dropoffMarker == null) {
                    if ((mymarker == null || myLatlng == null) && (dropoffMarker == null || dropoffLatlng == null)) {
                        mymarker = mapWorker.addMarker(myLatlng, mapWorker.carMarker);
                        dropoffMarker = mapWorker.addMarker(dropoffLatlng, mapWorker.destinationMarker);

                        showLatLngBoundZoom(mymarker, dropoffMarker);
                        mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                        drawRoute(mymarker, dropoffMarker);
                    }

                } else {

                    switch (trackingZoom) {
                        case 0: {
                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            showLatLngBoundZoom(mymarker, dropoffMarker);
                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, dropoffMarker);
                        }
                        break;
                        case 1: {
                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            if (isDefultZoom) {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom = false;
                            } else {
                                zoom = googleMap.getCameraPosition().zoom;
                            }
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(myLatlng)
                                            .zoom(zoom).build()));

                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, dropoffMarker);
                        }
                        break;

                    }

                }
            }
            break;
            case 4: {
                if (mymarker == null && dropoffMarker == null) {
                    if ((mymarker == null || myLatlng == null) && (dropoffMarker == null || dropoffLatlng == null)) {
                        mymarker = mapWorker.addMarker(myLatlng, mapWorker.carMarker);
                        dropoffMarker = mapWorker.addMarker(dropoffLatlng, mapWorker.destinationMarker);

                        showLatLngBoundZoom(mymarker, dropoffMarker);
                        mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                        drawRoute(mymarker, dropoffMarker);

                    }

                } else {
                    switch (trackingZoom) {
                        case 0: {
                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            showLatLngBoundZoom(mymarker, dropoffMarker);
                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, dropoffMarker);
                        }
                        break;
                        case 1: {
                            try {
                                mapWorker.rotateMarker(mymarker, (float) Functions.getBearingBetweenTwoPoints1(mymarker.getPosition(), myLatlng));
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception : " + e);
                            }
                            if (isDefultZoom) {
                                zoom = Variables.mapZoomLevel;
                                isDefultZoom = false;
                            } else {
                                zoom = googleMap.getCameraPosition().zoom;
                            }
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                    (new CameraPosition.Builder().target(myLatlng)
                                            .zoom(zoom).build()));

                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                            drawRoute(mymarker, dropoffMarker);
                        }
                        break;

                    }
                }
            }
            break;
        }

    }

    private void removeAllPin() {
        if (pickupMarker != null) {
            pickupMarker.remove();
            pickupMarker = null;
        }

        if (mymarker != null) {
            mymarker.remove();
            mymarker = null;
        }
        if (dropoffMarker != null) {
            dropoffMarker.remove();
            dropoffMarker = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    public void startRideMusicService() {
        BackgroundSoundService backgroundSoundService = new BackgroundSoundService();
        Intent mServiceIntent = new Intent(getApplicationContext(), backgroundSoundService.getClass());
        if (!Functions.isMyServiceRunning(getApplicationContext(), backgroundSoundService.getClass())) {
            getApplicationContext().startService(mServiceIntent);
        }
    }

    public void stopRideMusicService() {
        BackgroundSoundService backgroundSoundService = new BackgroundSoundService();
        Intent mServiceIntent = new Intent(getApplicationContext(), backgroundSoundService.getClass());
        if (Functions.isMyServiceRunning(getApplicationContext(), backgroundSoundService.getClass())) {
            getApplicationContext().stopService(mServiceIntent);
        }
    }

    public void startLocationService() {

        BackgroundLocationService backgroundLocationService = new BackgroundLocationService();
        Intent mServiceIntent = new Intent(this, backgroundLocationService.getClass());
        if (!Functions.isMyServiceRunning(this, backgroundLocationService.getClass())) {
            startService(mServiceIntent);
            Functions.logDMsg( "Service start");
        }
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

    }

    public void unbindService() {
        BackgroundLocationService backgroundLocationService = new BackgroundLocationService();
        if (Functions.isMyServiceRunning(this, backgroundLocationService.getClass())) {
            unbindService(mConnection);
            Functions.logDMsg( "Service start");
        }
    }

    @Override
    public void updatelocation(Double lat, Double lon) {
        preferences.setKeyUserLat("" + lat);
        preferences.setKeyUserLng("" + lon);

        updateRideSetp();

        Functions.getLocationString(this, new LatLng(lat, lon), new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (resp != null || !resp.equals(""))
                    preferences.setKeyCurrentAdress(resp);

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (Functions.getActiveFragment(getSupportFragmentManager()).equals("Chat_F")) {
            super.onBackPressed();
        }
    }

    public void changeStatus() {
        JSONObject params = new JSONObject();

        try {

            params.put("request_id", rideModel.getRideRequestModel().getId());

            if (rideModel.getRideRequestModel().getEndRide().equals("1")) {
                params.put("status", "collect_payment");
            } else if (rideModel.getRideRequestModel().getStartRide().equals("1")) {
                final String[] location_address = {""};
                Functions.getLocationString(RideActivity.this, new LatLng(dropoffLatlng.latitude, dropoffLatlng.longitude), new CallbackResponce() {
                    @Override
                    public void responce(String resp) {
                        if (resp != null || !resp.equals("")) {
                            location_address[0] = resp;
                        }
                    }
                });
                params.put("status", "end_ride");
                params.put("dropoff_lat", "" + dropoffLatlng.latitude);
                params.put("dropoff_long", "" + dropoffLatlng.longitude);
                params.put("dropoff_location", "" + location_address[0]);
                params.put("total_distance", String.format("%.0f", preferences.getKeyRideTotalDistance()));
                if (rideModel.getRideRequestModel().getPaymentMethodId().equals("0")) {
                    params.put("payment_method_id", "" + rideModel.getRideRequestModel().getPaymentMethodId());
                    params.put("payment_type", "cash");
                } else {
                    params.put("payment_method_id", "" + rideModel.getRideRequestModel().getPaymentMethodId());
                    params.put("payment_type", "card");
                }

            } else if (rideModel.getRideRequestModel().getArriveOnLocation().equals("1")) {

                final String[] location_address = {""};
                Functions.getLocationString(RideActivity.this, new LatLng(pickupLatlng.latitude, pickupLatlng.longitude), new CallbackResponce() {
                    @Override
                    public void responce(String resp) {
                        if (resp != null || !resp.equals("")) {
                            location_address[0] = resp;
                        }
                    }
                });

                params.put("status", "start_ride");
                params.put("pickup_lat", "" + pickupLatlng.latitude);
                params.put("pickup_long", "" + pickupLatlng.longitude);
                params.put("pickup_location", "" + location_address[0]);

            } else if (rideModel.getRideRequestModel().getOnTheWay().equals("1")) {
                params.put("status", "arrive_on_location");
            } else if (rideModel.getRideRequestModel().getRequest().equals("1")) {
                params.put("status", "on_the_way");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        changeStatus(params);
    }

    public void changeStatus(JSONObject params) {

        Functions.showLoader(this, false, false);
        ApiRequest.callApi(this, ApisList.startTrip, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {

                Functions.cancelLoader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);

                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {

                        if (params.getString("status").equals("start_ride")) {
                            preferences.setKeyTripId("" + jsonObject.getJSONObject("msg").getJSONObject("Trip").optString("id"));
                        }
                    }
                    checkUserRequest();

                } catch (Exception e) {
                    Functions.logDMsg( " Exception " + e);
                }

            }
        });
    }

    public void openChat() {

        Intent intent=new Intent(this,ChatA.class);
        intent.putExtra("user_id", "" + rideModel.getUserModel().getId());
        intent.putExtra("user_name", "" + rideModel.getUserModel().getFirstName() +" "+rideModel.getUserModel().getLastName());
        intent.putExtra("user_img", "" + Constants.BASE_URL + rideModel.getUserModel().getImage());
        intent.putExtra("order_id", "" + rideModel.getRideRequestModel().getId());
        intent.putExtra("senderid", preferences.getKeyUserId());
        startActivity(intent);
    }

    public void phoneCall() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.call_to) + " " + rideModel.getUserModel().getUsername() + " " + getString(R.string.from_your_phone));
        builder1.setTitle(getString(R.string.make_a_phone_call));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.call),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        onCall();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void onCall() {
        startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + rideModel.getUserModel().getPhoneNo())));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        Functions.unRegisterConnectivity(RideActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("request_responce");
        registerReceiver(broadcastReceiver, intentFilter);
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
    }


}
