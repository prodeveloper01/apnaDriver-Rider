package com.qboxus.gograbdriver.services;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.appinterfaces.LocationServiceCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONObject;

import static com.qboxus.gograbdriver.helpingclasses.GrabMyTaxiDriver.CHANNEL_ID;

public class BackgroundLocationService extends Service {


    public final IBinder mBinder = new BackgroundLocationService.LocalBinder();
    Location oldLocation = null, newLocation = null;
    Preferences preferences;
    DatabaseReference refRide, refTrip;
    GeoFire geofireRide, geofireTrip;
    double minimumDistance = 10.0f;
    LocationCallback locationCallback;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private int UPDATE_INTERVAL = 10000;
    private int FATEST_INTERVAL = 10000;
    private int DISPLACEMENT = 5;
    private LocationServiceCallback serviceCallbacks;
    private FusedLocationProviderClient mFusedLocationClient;

    public BackgroundLocationService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setCallbacks(LocationServiceCallback callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        createLocationRequest();
        refRide = FirebaseDatabase.getInstance().getReference().child("Drivers");
        refTrip = FirebaseDatabase.getInstance().getReference().child("DriversTrips");
        geofireRide = new GeoFire(refRide);
        geofireTrip = new GeoFire(refTrip);
        preferences = new Preferences(getApplicationContext());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new BackgroundLocationService.Mythreadclass());
        thread.start();

        showNotification();
        startLocationUpdates();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        stopLocationUpdates();
        if (geofireRide != null && geofireTrip != null && preferences != null) {
            geofireRide.removeLocation(preferences.getKeyUserId() + "_" + preferences.getKeyVehicleId());
            geofireTrip.removeLocation(preferences.getKeyTripId() + "_" + preferences.getKeyUserId());
        }
        super.onDestroy();

//        RestartService();

    }


    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        mLastLocation = location;

                        preferences.setKeyUserLat("" + location.getLatitude());
                        preferences.setKeyUserLng("" + location.getLongitude());


                        if (preferences.getKeyUserActive().equalsIgnoreCase("1") && Long.valueOf(preferences.getKeyVehicleId()) > 0) {
                            if (preferences.getKeyRequestId().equalsIgnoreCase("0")) {
                                geofireRide.setLocation(preferences.getKeyUserId() + "_" + preferences.getKeyVehicleId(), new GeoLocation(location.getLatitude(), location.getLongitude()));
                            } else {

                                if (newLocation != null) {
                                    oldLocation = newLocation;
                                    newLocation = location;
                                    float distance = (Float) oldLocation.distanceTo(newLocation);
                                    if (distance > minimumDistance) {
                                        preferences.setKeyRideTotalDistance(preferences.getKeyRideTotalDistance() + distance);
                                    }
                                } else {
                                    oldLocation = newLocation;
                                    newLocation = location;
                                }
                                geofireRide.removeLocation(preferences.getKeyUserId() + "_" + preferences.getKeyVehicleId());
                                geofireTrip.setLocation(preferences.getKeyRequestId() + "_" + preferences.getKeyUserId(), new GeoLocation(location.getLatitude(), location.getLongitude()));
                            }

                            Save_location_in_server(location.getLatitude(), location.getLongitude());
                            if (serviceCallbacks != null) {
                                serviceCallbacks.updatelocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            }

                        } else {
                            Functions.logDMsg( "Service Destroy by self");
                            stopSelf();
                        }
                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback
                , Looper.myLooper());

    }

    protected void stopLocationUpdates() {
        if (locationCallback != null)
            mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void Save_location_in_server(Double lat, Double lng) {

        JSONObject jsonObject = new JSONObject();

        try {
            if (preferences.getKeyTripId().equalsIgnoreCase("0")) {
                jsonObject.put("user_id", preferences.getKeyUserId());
            } else {
                jsonObject.put("trip_id", preferences.getKeyTripId());
            }
            jsonObject.put("lat", "" + lat);
            jsonObject.put("long", "" + lng);
        } catch (Exception e) {
        }

        ApiRequest.callApi(this, ApisList.addUserLatLong, jsonObject, null);

    }

    private void showNotification() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(this.getString(R.string.you_are_online))
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(101, notification);

    }

    public class LocalBinder extends Binder {
        public BackgroundLocationService getService() {
            return BackgroundLocationService.this;
        }
    }

    final class Mythreadclass implements Runnable {

        @Override
        public void run() {
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        }
    }

}
