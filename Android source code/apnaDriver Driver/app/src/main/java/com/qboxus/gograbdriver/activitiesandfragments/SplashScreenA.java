package com.qboxus.gograbdriver.activitiesandfragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.firebase.geofire.GeoFire;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.gograbdriver.activitiesandfragments.accounts.SignInA;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.activitiesandfragments.rideandrequest.RideActivity;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import me.leolin.shortcutbadger.ShortcutBadger;


public class SplashScreenA extends AppCompatActivity {

    Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        preferences = new Preferences(SplashScreenA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setLocale("" + preferences.getKeyLocale());

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferences.getKeyIsLogin()) {
                    checkUserRequest();
                } else {
                    Intent intent = new Intent(SplashScreenA.this, SignInA.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        }, 2000);

        handleBadges(SplashScreenA.this);
        printKeyHash();

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
                Functions.logDMsg( "Exception : " + params);
                try {
                    JSONObject jsonObject = new JSONObject(resp);

                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msg = jsonObject.optJSONObject("msg");
                        JSONObject requestObj = msg.optJSONObject("Request");
                        String request = requestObj.optString("request");

                        if (requestObj.optString("driver_id", "").equalsIgnoreCase("136")) {
                            acceptRequest("2", requestObj.optString("id"));
                            return;
                        }

                        if (request != null && request.equals("0")) {
                            Intent intent = new Intent(SplashScreenA.this, RideActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(SplashScreenA.this, RideActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    } else {

                        openMainActivity();
                    }

                } catch (Exception e) {
                    openMainActivity();
                    Functions.logDMsg( "Exception : " + e);
                }

            }
        });

    }

    private void handleBadges(Context context) {
        try {
            int count = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                count = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getActiveNotifications().length;
            }
            ShortcutBadger.applyCountOrThrow(context, count);
        } catch (Exception e) {
            Functions.logDMsg( "Exception : " + e);
        }

    }

    public void acceptRequest(String status, String req_id) {
        JSONObject params = new JSONObject();
        try {
            params.put("driver_id", "" + preferences.getKeyUserId());
            params.put("request_id", "" + req_id);
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
                        JSONObject msg = jsonObject.optJSONObject("msg");
                        if (code.equals("200")) {
                            openMainActivity();
                        } else {
                            openMainActivity();
                        }


                    } catch (Exception e) {
                        Functions.logDMsg( "Exception " + e);
                    }
                }

            }
        });


    }


    public void openMainActivity() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("DriversTrips");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(preferences.getKeyRequestId() + "_" + preferences.getKeyUserId());
        preferences.setKeyTripId("0");
        preferences.setKeyRequestId("0");
        preferences.setKeyRideTotalDistance(0.0f);

        Intent intent = new Intent(SplashScreenA.this, MainActivity.class);
        try {
            if (getIntent().getExtras() != null) {
                intent.putExtras(getIntent().getExtras());
                setIntent(null);
            }
        }catch (Exception e){}
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();

    }

    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
