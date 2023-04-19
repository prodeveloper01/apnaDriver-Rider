package com.qboxus.gograbdriver.activitiesandfragments.mainnavigation;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.qboxus.gograbdriver.activitiesandfragments.accounts.SignInA;
import com.qboxus.gograbdriver.activitiesandfragments.documentfragment.DocumentManageF;
import com.qboxus.gograbdriver.activitiesandfragments.earningfragments.EarningF;
import com.qboxus.gograbdriver.activitiesandfragments.myjobhistory.HistroyMainF;
import com.qboxus.gograbdriver.activitiesandfragments.myjobs.MyJobF;
import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.PrivacyAndTermsF;
import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.SettingF;
import com.qboxus.gograbdriver.activitiesandfragments.vehiclemanagefragment.ManageVehicleF;
import com.qboxus.gograbdriver.activitiesandfragments.walletfragment.WalletF;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.DrawableCallback;
import com.qboxus.gograbdriver.appinterfaces.LocationServiceCallback;
import com.qboxus.gograbdriver.Constants;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.PermissionUtils;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.mapclasses.MapWorker;
import com.qboxus.gograbdriver.R;
import com.qboxus.gograbdriver.services.BackgroundLocationService;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.luongvo.widget.iosswitchview.SwitchView;


public class MainMenuF extends RootFragment implements OnMapReadyCallback, LocationServiceCallback,
        View.OnClickListener {

    public static DrawerLayout drawer;
    View view;
    SwitchView switchMode;
    View tabSwitchMode;
    FrameLayout fl;
    ImageView earthBtn, currentLocationBtn;
    Preferences preferences;
    //location permission handling
    View noLocationLayoutView;
    RelativeLayout locationLayoutView, toolbar;
    LinearLayout allowAccessLocation, mapBar;
    Dialog dialog;
    String extension = ".jpg";
    String imageFilePath = "";
    View.OnClickListener navClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawer.openDrawer(GravityCompat.START);
        }
    };
    BackgroundLocationService mService;
    boolean mBound = false;
    PermissionUtils takePermissionUtils;

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Uri selectedImage = data.getData();
                    beginCrop(selectedImage);

                }
            });
    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Matrix matrix = new Matrix();
                        try {
                            android.media.ExifInterface exif = new android.media.ExifInterface(imageFilePath);
                            int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                            switch (orientation) {
                                case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postRotate(180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.postRotate(270);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));
                        beginCrop(selectedImage);
                    }
                }
            });
    private RelativeLayout llHome, llMyJob, llMyHistory, llWallet, llEarning, llManageVehicle, llDocuments, llHelp, llSettings, llLogout;
    private TextView txtHome, txtMyHistory, txtMyJob, txtWallet, txtEarning, txtManageVehicle, txtDocuments, txtHelp, txtSettings, txtLogout;
    private ImageView imgHome, ivHistory, imgMyJob, imgWallet, imgEarning, imgManageVehicle, imgDocuments, imgHelp, imgSettings, imgLogout;
    private TextView tvUsername, tvEmail, txtVersionCode;
    private SimpleDraweeView ivProfile;
    //map operation
    private MapView mapView;
    private GoogleMap googleMap;
    private MapWorker mapWorker;
    private Marker mymarker;
    private LatLng myLatlng;
    ActivityResultLauncher<Intent> cropResultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    CropImage.ActivityResult result1 = CropImage.getActivityResult(data);
                    Uri resultUri = result1.getUri();
                    InputStream imageStream = null;
                    try {
                        imageStream = getActivity().getContentResolver().openInputStream(resultUri);
                    } catch (FileNotFoundException e) {
                        Functions.logDMsg("Error : " + e);
                    }
                    final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                    String path = resultUri.getPath();
                    Matrix matrix = new Matrix();
                    android.media.ExifInterface exif = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        try {
                            exif = new android.media.ExifInterface(path);
                            int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                            switch (orientation) {
                                case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postRotate(180);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.postRotate(270);
                                    break;
                            }
                        } catch (IOException e) {
                            Functions.logDMsg("Error : " + e);
                        }
                    }

                    Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    Bitmap converetdImage = getResizedBitmap(rotatedBitmap, 500);
                    callUpdateUserImage(converetdImage);
                }
            });
    private FusedLocationProviderClient mFusedLocationClient;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            BackgroundLocationService.LocalBinder binder = (BackgroundLocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setCallbacks(MainMenuF.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };




    public MainMenuF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        preferences = new Preferences(view.getContext());
        mapView = view.findViewById(R.id.map_main_layout);
        mapView.onCreate(savedInstanceState);
        init();
        actionControl();
        return view;
    }

    private void actionControl() {
        tabSwitchMode.setOnClickListener(this);
    }

    private void init() {
        preferences = new Preferences(view.getContext());
        fl = view.findViewById(R.id.fl_id);
        setNavigationDrawer();
        methodSetupmap();
        tabSwitchMode = view.findViewById(R.id.tab_switch_mode);
        switchMode = view.findViewById(R.id.switch_mode);
        locationLayoutView = view.findViewById(R.id.location_layout_view);
        noLocationLayoutView = view.findViewById(R.id.no_location_layout_view);
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        ivProfile = view.findViewById(R.id.iv_profile);
        ivProfile.setOnClickListener(this);
        txtVersionCode = view.findViewById(R.id.txt_main_home_version_code);
        txtVersionCode.setText("" + getphoneVersion());
        view.findViewById(R.id.iv_menu).setOnClickListener(navClickListener);
        llHome = view.findViewById(R.id.ll_home);
        llHome.setOnClickListener(this);
        llMyHistory = view.findViewById(R.id.ll_my_history);
        llMyJob = view.findViewById(R.id.ll_my_job);
        llMyHistory.setOnClickListener(this);
        llMyJob.setOnClickListener(this);
        llWallet = view.findViewById(R.id.ll_wallet);
        llWallet.setOnClickListener(this);
        llEarning = view.findViewById(R.id.ll_earning);
        llEarning.setOnClickListener(this);
        llManageVehicle = view.findViewById(R.id.ll_manage_vehicle);
        llManageVehicle.setOnClickListener(this);
        llDocuments = view.findViewById(R.id.ll_documents);
        llDocuments.setOnClickListener(this);
        llSettings = view.findViewById(R.id.ll_setting);
        llSettings.setOnClickListener(this);
        llHelp = view.findViewById(R.id.ll_help);
        llHelp.setOnClickListener(this);
        llLogout = view.findViewById(R.id.ll_logout);
        llLogout.setOnClickListener(this);
        allowAccessLocation = view.findViewById(R.id.allow_access_location);
        allowAccessLocation.setOnClickListener(this);
        txtHome = view.findViewById(R.id.txt_home);
        txtMyJob = view.findViewById(R.id.txt_my_job);
        ivHistory = view.findViewById(R.id.iv_history);
        txtMyHistory = view.findViewById(R.id.txt_my_history);
        txtWallet = view.findViewById(R.id.txt_wallet);
        txtEarning = view.findViewById(R.id.txt_earning);
        txtManageVehicle = view.findViewById(R.id.txt_manage_vehicle);
        txtDocuments = view.findViewById(R.id.txt_documents);
        txtSettings = view.findViewById(R.id.txt_setting);
        txtHelp = view.findViewById(R.id.txt_help);
        txtLogout = view.findViewById(R.id.txt_logout);
        mapBar = view.findViewById(R.id.map_bar);
        toolbar = view.findViewById(R.id.toolbar);
        imgHome = view.findViewById(R.id.iv_home);
        imgMyJob = view.findViewById(R.id.iv_job);
        imgWallet = view.findViewById(R.id.iv_wallet);
        imgEarning = view.findViewById(R.id.iv_earning);
        imgManageVehicle = view.findViewById(R.id.iv_manage_vehicle);
        imgDocuments = view.findViewById(R.id.iv_documents);
        imgSettings = view.findViewById(R.id.iv_setting);
        imgHelp = view.findViewById(R.id.iv_help);
        imgLogout = view.findViewById(R.id.iv_logout);

        if (preferences.getKeyUserActive().equals("1")) {
            switchMode.setChecked(true);
        } else {
            switchMode.setChecked(false);
        }

        earthBtn = view.findViewById(R.id.earth_btn);
        earthBtn.setOnClickListener(this);
        currentLocationBtn = view.findViewById(R.id.current_location_btn);
        currentLocationBtn.setOnClickListener(this);


        setUpScreenData();
    }

    private String getphoneVersion() {
        try {
            return view.getContext().getString(R.string.version) + " " + view.getContext().getPackageManager()
                    .getPackageInfo(view.getContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            System.out.println("Error : " + e);
            return "";
        }
    }

    private void setUpScreenData() {
        tvUsername.setText(preferences.getKeyUserName());
        tvEmail.setText(preferences.getKeyUserEmail());
        ivProfile.setController(Functions.frescoImageLoad(preferences.getKeyUserImage(),
                R.drawable.ic_profile_gray,ivProfile,false));
        setColorSelection(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        enablePermission();
    }

    private void enablePermission() {
        takePermissionUtils=new PermissionUtils(getActivity(),locationInitPermissionCallback);
        if (takePermissionUtils.isLocationPermissionGranted()) {
            getCurrentLocation();
            if (preferences.getKeyUserActive().equals("1")) {
                if (takePermissionUtils.isLocationPermissionGranted()) {
                    startLocationService();
                } else {
                    takePermissionUtils.showLocationPermissionDailog(getActivity().getResources().getString(R.string.we_need_acurate_ride_permission));
                }
            }
        } else {
            showNullLocationPermissionUi();
        }
    }



    private ActivityResultLauncher<String[]> locationInitPermissionCallback = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(getActivity(),"location");
                    }
                    else
                    if (allPermissionClear)
                    {
                        enablePermission();
                    }

                }
            });




    public void startLocationService() {
        BackgroundLocationService backgroundLocationService = new BackgroundLocationService();
        Intent mServiceIntent = new Intent(view.getContext(), backgroundLocationService.getClass());
        if (!Functions.isMyServiceRunning(view.getContext(), backgroundLocationService.getClass())) {
            ContextCompat.startForegroundService(view.getContext(), mServiceIntent);
            Functions.logDMsg("Service start");
        }
        view.getContext().bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void getCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Functions.logDMsg("GetCurrentlocation: inside Not Permissioned");
            enablePermission();
            return;
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        myLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                        double lat = (myLatlng.latitude);
                        double lon = (myLatlng.longitude);

                        preferences.setKeyUserLat("" + lat);
                        preferences.setKeyUserLng("" + lon);

                        Functions.logDMsg("Show" + lat + "," + lon);
                        updateRideSetup();
                    }
                }
            });


        }
    }

    public void stopLocationService() {
        BackgroundLocationService backgroundLocationService = new BackgroundLocationService();
        Intent mServiceIntent = new Intent(view.getContext(), backgroundLocationService.getClass());
        if (Functions.isMyServiceRunning(view.getContext(), backgroundLocationService.getClass())) {
            view.getContext().stopService(mServiceIntent);
            view.getContext().unbindService(mConnection);
            Functions.logDMsg("Service stop");
        }
    }

    private void setNavigationDrawer() {
        drawer = view.findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                tvUsername.setText(preferences.getKeyUserName());
                tvEmail.setText(preferences.getKeyUserEmail());
                ivProfile.setController(Functions.frescoImageLoad(preferences.getKeyUserImage(),
                        R.drawable.ic_profile_gray,ivProfile,false));
                drawer.openDrawer(GravityCompat.START);

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                String tagName = Functions.getActiveFragment(getActivity().getSupportFragmentManager());
                updateSelectionColor(tagName);
            }
        });

    }

    private void updateSelectionColor(String name) {
        if (name.equalsIgnoreCase("null")) {
            setColorSelection(1);
        }
        if (name.equalsIgnoreCase("MyJobF")) {
            setColorSelection(2);
        }

        if (name.equalsIgnoreCase("MyJob_F")) {
            setColorSelection(3);
        }
        if (name.equalsIgnoreCase("Wallet_F")) {
            setColorSelection(4);
        }
        if (name.equalsIgnoreCase("Earning_F")) {
            setColorSelection(5);
        }
        if (name.equalsIgnoreCase("ManageVehicle_F")) {
            setColorSelection(6);
        }
        if (name.equalsIgnoreCase("VehicleDocumentManage_F")) {
            setColorSelection(7);
        }
        if (name.equalsIgnoreCase("Setting_F")) {
            setColorSelection(8);
        }
        if (name.equalsIgnoreCase("PrivacyAndTerms_F")) {
            setColorSelection(9);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        mapWorker = new MapWorker(view.getContext(), googleMap);

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        googleMap.setMyLocationEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    //    init map by custom classes smothly
    private void methodSetupmap() {
        MapsInitializer.initialize(view.getContext());
        mapView.onResume();
        mapView.getMapAsync(this);
        updateRideSetup();

    }

    private void updateRideSetup() {

        myLatlng = new LatLng(Double.parseDouble(preferences.getKeyUserLat()), Double.parseDouble(preferences.getKeyUserLng()));

        if (mymarker == null) {
            if (mymarker == null || myLatlng == null) {
                addMarkerPin(false);
            }

        } else {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition
                    (new CameraPosition.Builder().target(myLatlng)
                            .zoom(Variables.mapZoomLevel).build()));
            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
        }


    }

    private void moveCurrentLocation() {
        myLatlng = new LatLng(Double.parseDouble(preferences.getKeyUserLat()), Double.parseDouble(preferences.getKeyUserLng()));
        if (mymarker == null) {
            if (mymarker == null || myLatlng == null) {
                addMarkerPin(false);
            }

        } else {
            mymarker.setPosition(myLatlng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 16));

        }
    }

    private void showNullLocationPermissionUi() {
        noLocationLayoutView.setVisibility(View.VISIBLE);
        locationLayoutView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.GONE);
        mapBar.setVisibility(View.GONE);
    }

    private void showLocationPermissionUi() {
        noLocationLayoutView.setVisibility(View.GONE);
        locationLayoutView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        mapBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.current_location_btn:
                moveCurrentLocation();
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
            case R.id.allow_access_location: {
                takePermissionUtils=new PermissionUtils(getActivity(),locationPermissionCallback);
                if (takePermissionUtils.isLocationPermissionGranted()) {
                    showLocationPermissionUi();
                    enablePermission();
                } else {
                    takePermissionUtils.showLocationPermissionDailog(getActivity().getString(R.string.we_need_acurate_ride_permission));
                }
            }
            break;
            case R.id.ll_home:
                drawer.closeDrawer(GravityCompat.START);
                Functions.clearFragmentByTag(getActivity().getSupportFragmentManager());
                break;


            case R.id.ll_my_history:
                drawer.closeDrawer(GravityCompat.START);
                Functions.methodopennavfragment(new HistroyMainF(navClickListener), getActivity().getSupportFragmentManager(), "MyJob_F", fl);
                break;


            case R.id.ll_my_job:
                openMyJobFragment();
                break;


            case R.id.iv_profile: {

                selectfile();
            }
            break;

            case R.id.ll_wallet:
                drawer.closeDrawer(GravityCompat.START);
                Functions.methodopennavfragment(new WalletF(navClickListener), getActivity().getSupportFragmentManager(), "Wallet_F", fl);
                break;

            case R.id.ll_earning:
                drawer.closeDrawer(GravityCompat.START);
                Functions.methodopennavfragment(new EarningF(navClickListener), getActivity().getSupportFragmentManager(), "Earning_F", fl);
                break;

            case R.id.ll_manage_vehicle:
                drawer.closeDrawer(GravityCompat.START);
                Functions.methodopennavfragment(new ManageVehicleF(navClickListener), getActivity().getSupportFragmentManager(), "ManageVehicle_F", fl);
                break;

            case R.id.tab_switch_mode: {
                if (switchMode.isChecked()) {
                    Functions.showAlert(getActivity(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.are_you_sure_to_offline), view.getContext().getString(R.string.no), view.getContext().getString(R.string.yes), new CallbackResponce() {
                        @Override
                        public void responce(String resp) {
                            if (resp.equals("yes")) {
                                if (preferences.getKeyUserActive().equals("1")) {
                                    callApiForStatus("0");
                                }
                            }
                        }
                    });

                }

                else {
                    if (preferences.getKeyIsVehicleSet()) {
                            Functions.showAlert(getActivity(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.are_you_sure_to_online), view.getContext().getString(R.string.no), view.getContext().getString(R.string.yes), new CallbackResponce() {
                                @Override
                                public void responce(String resp) {
                                    if (resp.equals("yes")) {
                                        if (preferences.getKeyUserActive().equals("0")) {
                                            callApiForStatus("1");
                                        }
                                    }
                                }
                            });
                    } else {
                        Functions.showAlert(getActivity(), view.getContext().getString(R.string.alert), view.getContext().getString(R.string.you_cant_online_until_your_verhicle_is_not_set), view.getContext().getString(R.string.cancel), view.getContext().getString(R.string.add_vehicle), new CallbackResponce() {
                            @Override
                            public void responce(String resp) {
                                if (resp.equals("yes")) {
                                    if (preferences.getKeyUserActive().equals("0")) {

                                        drawer.closeDrawer(GravityCompat.START);
                                        Functions.methodopennavfragment(new ManageVehicleF(navClickListener), getActivity().getSupportFragmentManager(), "ManageVehicle_F", fl);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            break;

            case R.id.ll_documents:
                drawer.closeDrawer(GravityCompat.START);
                Functions.methodopennavfragment(new DocumentManageF(navClickListener), getActivity().getSupportFragmentManager(), "VehicleDocumentManage_F", fl);
                break;

            case R.id.ll_setting:
                drawer.closeDrawer(GravityCompat.START);
                Functions.methodopennavfragment(new SettingF(navClickListener), getActivity().getSupportFragmentManager(), "Setting_F", fl);
                break;

            case R.id.ll_help:
                drawer.closeDrawer(GravityCompat.START);
                Functions.methodopennavfragment(new PrivacyAndTermsF(view.getContext().getString(R.string.help), Constants.HELP_URL, navClickListener), getActivity().getSupportFragmentManager(), "PrivacyAndTerms_F", fl);
                break;

            case R.id.ll_logout: {
                drawer.closeDrawer(GravityCompat.START);
                setColorSelection(8);

                final Dialog dialog = new Dialog(view.getContext());
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.show_defult_two_btn_alert_popup_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                final TextView txtYes, txtNo, txtTitle, txtMessage;
                txtTitle = dialog.findViewById(R.id.defult_alert_txt_title);
                txtMessage = dialog.findViewById(R.id.defult_alert_txt_message);
                txtNo = dialog.findViewById(R.id.defult_alert_btn_cancel_no);
                txtYes = dialog.findViewById(R.id.defult_alert_btn_cancel_yes);

                txtTitle.setText("" + view.getContext().getString(R.string.logout_status));
                txtMessage.setText("" + view.getContext().getString(R.string.are_you_sure));
                txtYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        logoutByThisUserId();
                    }
                });

                txtNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

            break;
        }

    }


    private ActivityResultLauncher<String[]> locationPermissionCallback = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(getActivity(),"location");
                    }
                    else
                    if (allPermissionClear)
                    {
                        showLocationPermissionUi();
                        enablePermission();
                    }

                }
            });




    public void openMyJobFragment() {
        drawer.closeDrawer(GravityCompat.START);
        Functions.methodopennavfragment(new MyJobF(navClickListener), getActivity().getSupportFragmentManager(), "MyJobF", fl);
    }

    private void beginCrop(Uri source) {
        Intent intent = CropImage.activity(source).setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1).getIntent(requireActivity());
        cropResultCallback.launch(intent);
    }

    private void callUpdateUserImage(Bitmap converetdImage) {
        JSONObject params = new JSONObject();

        try {
            params.put("user_id", preferences.getKeyUserId());
            JSONObject fileData = new JSONObject();
            params.put("image", encodetobase64(converetdImage));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(view.getContext(), false, false);
        ApiRequest.callApi(view.getContext(), ApisList.editProfile, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                try {
                    if (resp != null) {
                        JSONObject respobj = new JSONObject(resp);
                        String code = respobj.optString("code");
                        if (code.equals("200")) {
                            Functions.showToast(view.getContext(), view.getContext().getString(R.string.successfully_update_username));
                            methodSaveuserdetails(resp);
                        } else {
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.edit_profile_status), "" + respobj.getString("msg"));
                        }
                    }
                } catch (Exception e) {
                    Functions.logDMsg("Error " + e);
                }
            }
        });
    }

    private void methodSaveuserdetails(String resp) {

        if (resp != null) {
            try {
                JSONObject respobj = new JSONObject(resp);
                if (respobj.getString("code").equals("200")) {

                    JSONObject msgobj = respobj.getJSONObject("msg");

                    JSONObject userObj = msgobj.getJSONObject("User");
                    JSONObject countryObj = msgobj.getJSONObject("Country");

                    preferences.setKeyUserId(userObj.optString("id", ""));
                    preferences.setKeyUserFirstName(userObj.optString("first_name", ""));
                    preferences.setKeyUserLastName(userObj.optString("last_name", ""));
                    preferences.setKeyUserName(userObj.optString("username", ""));
                    preferences.setKeyUserEmail(userObj.optString("email", ""));
                    preferences.setKeyUserPhone(userObj.optString("phone", ""));
                    preferences.setKeyUserRole(userObj.optString("role", ""));
                    preferences.setKeySocialId(userObj.optString("social_id", ""));
                    preferences.setKeySocialType(userObj.optString("social", ""));
                    preferences.setKeyUserToken(userObj.optString("auth_token", ""));
                    preferences.setKeyDOB(userObj.optString("dob", ""));
                    preferences.setKeyGender(userObj.optString("gender", ""));
                    preferences.setKeyUserImage(userObj.optString("image", ""));
                    preferences.setKeyUserActive(userObj.optString("online", ""));
                    preferences.setKeyWallet(userObj.optString("wallet", ""));
                    preferences.setKeyUserAuthToken(userObj.optString("token", ""));


                    preferences.setKeyPhoneCountryCode(countryObj.optString("phonecode", ""));
                    preferences.setKeyPhoneCountryName(countryObj.optString("native", ""));
                    preferences.setKeyPhoneCountryIOS(countryObj.optString("iso", ""));
                    preferences.setKeyPhoneCountryId(countryObj.optString("id", ""));
                    preferences.setKeyUserCountryId(countryObj.optString("id", ""));
                    preferences.setKeyUserCountry(countryObj.optString("name", ""));
                    preferences.setKeyCurrencyName(countryObj.optString("currency", ""));

                    addMarkerPin(true);
                    drawer.openDrawer(GravityCompat.START);


                }

            } catch (Exception e) {
                Functions.logDMsg("Exception " + e);
            }
        }
    }

    private void addMarkerPin(boolean IsUpdate) {

        if (mapWorker != null && googleMap != null) {
            Functions.uriFromURL(preferences.getKeyUserImage(),new DrawableCallback() {
                @Override
                public void Responce(Bitmap bitmap) {
                    Drawable drawable = null;
                    try {
                        drawable = new BitmapDrawable(getActivity().getResources(), bitmap);
                    }catch (Exception e)
                    {}
                    if (bitmap==null)
                    {
                        if (mymarker != null) {
                            if (IsUpdate) {
                                ivProfile.setController(Functions.frescoImageLoad(null,
                                        R.drawable.ic_profile_gray,ivProfile,false));
                            }
                            mapWorker.currentLocationMarker = getMyMarkerPinView(null);
                            mymarker.setIcon(BitmapDescriptorFactory.fromBitmap(mapWorker.currentLocationMarker));
                        } else {
                            if (IsUpdate) {
                                ivProfile.setController(Functions.frescoImageLoad(null,
                                        R.drawable.ic_profile_gray,ivProfile,false));
                            }
                            mapWorker.currentLocationMarker = getMyMarkerPinView(null);
                            mymarker = mapWorker.addMarker(myLatlng, mapWorker.currentLocationMarker);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 16));
                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                        }
                    }
                    else
                    {
                        if (mymarker != null) {
                            if (IsUpdate) {
                                ivProfile.setController(Functions.frescoImageLoad(drawable,ivProfile,
                                        false));
                            }
                            mapWorker.currentLocationMarker = getMyMarkerPinView(Functions.getRoundBitmap(bitmap));
                            mymarker.setIcon(BitmapDescriptorFactory.fromBitmap(mapWorker.currentLocationMarker));
                        } else {
                            if (IsUpdate) {
                                ivProfile.setController(Functions.frescoImageLoad(drawable,ivProfile,
                                        false));
                            }
                            mapWorker.currentLocationMarker = getMyMarkerPinView(Functions.getRoundBitmap(bitmap));
                            mymarker = mapWorker.addMarker(myLatlng, mapWorker.currentLocationMarker);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 16));
                            mapWorker.animateMarkerWithMap(mymarker, myLatlng.latitude, myLatlng.longitude);
                        }
                    }
                }
            });

        }
    }


    public Bitmap getMyMarkerPinView(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_current_marker, null);
        SimpleDraweeView profileImg = customMarkerView.findViewById(R.id.img_pin_profile);
        TextView tvPinProfile = customMarkerView.findViewById(R.id.tv_pin_profile);


        if (bitmap==null || TextUtils.isEmpty(preferences.getKeyUserImage())) {
            profileImg.setVisibility(View.GONE);
            tvPinProfile.setVisibility(View.VISIBLE);
            tvPinProfile.setText("" + preferences.getKeyUserName());
        } else {
            profileImg.setImageBitmap(bitmap);
            profileImg.setVisibility(View.VISIBLE);
            tvPinProfile.setVisibility(View.GONE);
        }


        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    String encodetobase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void selectfile() {

        dialog = new Dialog(view.getContext());
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.select_profile_picture_list_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txtTakePhoto = dialog.findViewById(R.id.txt_take_photo);
        TextView txtGallery = dialog.findViewById(R.id.txt_gallery);
        TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
        txtTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                takePermissionUtils=new PermissionUtils(getActivity(), cameraPermissionCallback);
                if (takePermissionUtils.isCameraPermissionGranted())
                {
                    openCameraIntent();
                }
                else
                {
                    takePermissionUtils.showCameraPermissionDailog(getActivity().getString(R.string.to_upload_image_permission_string));
                }
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                takePermissionUtils=new PermissionUtils(getActivity(), galleryPermissionCallback);
                if (takePermissionUtils.isStoragePermissionGranted())
                {
                    openGalleryIntent();
                }
                else
                {
                    takePermissionUtils.showStoragePermissionDailog(getActivity().getString(R.string.to_upload_image_permission_string));
                }
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private ActivityResultLauncher<String[]> cameraPermissionCallback = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(getActivity(),"camera");
                    }
                    else
                    if (allPermissionClear)
                    {
                        openCameraIntent();
                    }

                }
            });

    private ActivityResultLauncher<String[]> galleryPermissionCallback = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear=true;
                    List<String> blockPermissionCheck=new ArrayList<>();
                    for (String key : result.keySet())
                    {
                        if (!(result.get(key)))
                        {
                            allPermissionClear=false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(getActivity(),key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked"))
                    {
                        Functions.showPermissionSetting(getActivity(),"gallery");
                    }
                    else
                    if (allPermissionClear)
                    {
                        openGalleryIntent();
                    }

                }
            });





    private void openGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(view.getContext(), view.getContext().getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(pictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                extension,         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }


    private void logoutByThisUserId() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", "" + preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(view.getContext(), false, false);
        ApiRequest.callApi(view.getContext(), ApisList.logout, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.optString("code").equals("200")) {
                            stopLocationService();
                            boolean isnightmode = preferences.getKeyIsNightMode();
                            String language = preferences.getKeyLocale();
                            preferences.clearSharedPreferences();
                            preferences.setKeyIsNightMode(isnightmode);
                            preferences.setKeyLocale(language);
                            Intent logoutIntent = new Intent(getActivity(), SignInA.class);
                            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(logoutIntent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void setColorSelection(int selected_tab) {
        switch (selected_tab) {
            case 1: {
                ClearSelection();
                txtHome.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgHome.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;
            case 2: {
                ClearSelection();
                txtMyJob.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgMyJob.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;
            case 3: {
                ClearSelection();
                txtMyHistory.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                ivHistory.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;

            case 4: {
                ClearSelection();
                txtWallet.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgWallet.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;
            case 5: {
                ClearSelection();
                txtEarning.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgEarning.setImageResource(R.drawable.ic_earning_black);

            }
            break;
            case 6: {
                ClearSelection();
                txtManageVehicle.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgManageVehicle.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;
            case 7: {
                ClearSelection();
                txtDocuments.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgDocuments.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;
            case 8: {
                ClearSelection();
                txtSettings.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgSettings.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;
            case 9: {
                ClearSelection();
                txtHelp.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgHelp.setImageResource(R.drawable.ic_help_alert_black);
            }
            break;
            case 10: {
                ClearSelection();
                txtLogout.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorBlack));
                imgLogout.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
            }
            break;
            default:
                break;

        }
    }

    private void ClearSelection() {
        txtHome.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        txtMyHistory.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgHome.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
        ivHistory.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
        txtMyJob.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgMyJob.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
        txtWallet.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgWallet.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
        txtEarning.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgEarning.setImageResource(R.drawable.ic_earning_gray);
        txtManageVehicle.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgManageVehicle.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
        txtDocuments.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgDocuments.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
        txtSettings.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgSettings.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
        txtHelp.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgHelp.setImageResource(R.drawable.ic_help_alert_gray);
        txtLogout.setTextColor(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray));
        imgLogout.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorDarkGray), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void callApiForStatus(String Status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", preferences.getKeyUserId());
            jsonObject.put("online", Status);
        } catch (Exception e) {
        }

        Functions.showLoader(view.getContext(), false, false);
        ApiRequest.callApi(view.getContext(), ApisList.updateDriverOnlineStatus, jsonObject, new CallbackResponce() {
            @Override
            public void responce(String s) {

                Functions.cancelLoader();
                if (s != null) {

                    try {
                        JSONObject respobj = new JSONObject(s);

                        if (respobj.getString("code").equals("200")) {
                            JSONObject msgobj = respobj.getJSONObject("msg");
                            JSONObject user_obj = msgobj.getJSONObject("User");
                            preferences.setKeyUserActive(user_obj.optString("online", "0"));
                            if (preferences.getKeyUserActive().equalsIgnoreCase("1")) {
                                switchMode.setChecked(true);
                                startLocationService();

                            } else {
                                switchMode.setChecked(false);
                                stopLocationService();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    @Override
    public void updatelocation(Double lat, Double lon) {

        if (getActivity() != null) {
            preferences.setKeyUserLat("" + lat);
            preferences.setKeyUserLng("" + lon);

            if (takePermissionUtils.isLocationPermissionGranted()) {
                updateRideSetup();
            }
            Functions.getLocationString(getActivity(), new LatLng(lat, lon), new CallbackResponce() {
                @Override
                public void responce(String resp) {
                    if (resp != null || !resp.equals(""))
                        preferences.setKeyCurrentAdress(resp);

                }
            });
        }
    }





}
