package com.qboxus.gograbdriver.activitiesandfragments.mainnavigation;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qboxus.gograbdriver.activitiesandfragments.rideandrequest.RideActivity;
import com.qboxus.gograbdriver.appinterfaces.CallBackInternet;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Functions.logDMsg("Type check"+intent.getStringExtra("type"));
            try {
                if (intent.hasExtra("type")) {
                    if (intent.getStringExtra("type").equalsIgnoreCase("taxi_order") ||
                            intent.getStringExtra("type").equalsIgnoreCase("request_vehicle")) {
                        Intent moveIntent = new Intent(MainActivity.this, RideActivity.class);
                        startActivity(moveIntent);
                    }
                    else
                    if (intent.getStringExtra("type").equalsIgnoreCase("food_order") ||
                            intent.getStringExtra("type").equalsIgnoreCase("parcel_order"))
                    {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mainMenuF!=null)
                                        {
                                            mainMenuF.openMyJobFragment();
                                        }

                                    }
                                });
                            }
                        },400);
                    }

                }
            } catch (Exception e) {
                Functions.logDMsg("Error : Notification " + e);

            }
        }
    };
    MainMenuF mainMenuF;
    Preferences preferences;
    long mBackPressed;
    String token;
    private GpsStatusReceiver location_enablel_receiver = new GpsStatusReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);

        preferences = new Preferences(MainActivity.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale("" + preferences.getKeyLocale());


        if (savedInstanceState == null) {
            initScreen();
        } else {
            mainMenuF = (MainMenuF) getSupportFragmentManager().getFragments().get(0);
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    token = task.getResult();

                    Functions.logDMsg("token"+token);
                });

        getSettings();
        sendDeviceData();

        checkLocation();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        Functions.unRegisterConnectivity(MainActivity.this);

        if (location_enablel_receiver != null) {
            unregisterReceiver(location_enablel_receiver);
        }
        Functions.unRegisterConnectivity(MainActivity.this);
    }

    private void sendDeviceData() {
        callApiGetIpTypes();
    }

    private void callApiGetIpTypes() {
        ApiRequest.callApi(MainActivity.this, ApisList.apiForIp, new JSONObject(), new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        callApiSendDeviceData("" + respobj.optString("ip"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, Request.Method.GET);
    }

    private void callApiSendDeviceData(String ip) {

        JSONObject sendobj = new JSONObject();

        try {

            String version = "v" + MainActivity.this.getPackageManager()
                    .getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;

            sendobj.put("user_id", "" + preferences.getKeyUserId());
            sendobj.put("device", "android");
            sendobj.put("version", version);
            sendobj.put("ip", ip);
            sendobj.put("device_token", "" + token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(MainActivity.this, ApisList.addDeviceData, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
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

                            preferences.setKeyPhoneCountryCode(countryObj.optString("phonecode", ""));
                            preferences.setKeyPhoneCountryName(countryObj.optString("native", ""));
                            preferences.setKeyPhoneCountryIOS(countryObj.optString("iso", ""));
                            preferences.setKeyPhoneCountryId(countryObj.optString("id", ""));
                            preferences.setKeyUserCountryId(countryObj.optString("id", ""));
                            preferences.setKeyUserCountry(countryObj.optString("native", ""));

                            preferences.setKeyCurrencyName(countryObj.optString("currency", ""));
                            preferences.setKeyDOB(userObj.optString("dob", ""));
                            preferences.setKeyGender(userObj.optString("gender", ""));
                            preferences.setKeyUserImage(userObj.optString("image", ""));
                            preferences.setKeyUserDeviceToken(token);
                            preferences.setKeyUserRole(userObj.optString("role", ""));
                            preferences.setKeyUserActive(userObj.optString("online", ""));
                            preferences.setKeyWallet(userObj.optString("wallet", ""));
                            preferences.setKeyUserAuthToken(userObj.optString("token", ""));
                            preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+" + preferences.getKeyPhoneCountryCode()), ""));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initScreen() {
        mainMenuF = new MainMenuF();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuF)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (MainMenuF.drawer.isDrawerOpen(GravityCompat.START)) {
            MainMenuF.drawer.closeDrawers();
            return;
        }
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Functions.showToast(MainActivity.this, MainActivity.this.getString(R.string.tap_again));
                mBackPressed = System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void getSettings() {
        JSONObject sendobj = new JSONObject();

        ApiRequest.callApi(getApplicationContext(), ApisList.showSettings, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.optString("code").equals("200")) {

                            JSONArray msg = respobj.optJSONArray("msg");

                            for (int i = 0; i < msg.length(); i++) {

                                JSONObject jsonObject = msg.optJSONObject(i);
                                JSONObject Setting = jsonObject.optJSONObject("Setting");
                                if (Setting.optString("type").equalsIgnoreCase("currency")) {
                                    Toast.makeText(getApplicationContext(), "currency : "+Setting.optString("value"), Toast.LENGTH_SHORT).show();
                                    preferences.setKeyCurrencySymbol(Setting.optString("value"));
                                }

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void checkLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        boolean isEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isEnabled) {
            enableLocation();
        }
    }

    private void enableLocation() {
        startActivity(new Intent(MainActivity.this, EnablelocationA.class));
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("request_responce");
        registerReceiver(broadcastReceiver, intentFilter);

        registerReceiver(location_enablel_receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        Functions.RegisterConnectivity(this, new CallBackInternet() {
            @Override
            public void GetResponse(String requestType, String response) {
                if (response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(MainActivity.this, NoInternetA.class));
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
            }
        });

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

    private class GpsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkLocation();
        }
    }


    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            try {
                if (intent.hasExtra("type")) {
                    if (intent.getStringExtra("type").equalsIgnoreCase("food_order") ||
                            intent.getStringExtra("type").equalsIgnoreCase("parcel_order"))
                    {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mainMenuF!=null)
                                        {
                                            mainMenuF.openMyJobFragment();
                                        }

                                    }
                                });
                            }
                        },400);
                    }
                    else
                    if (intent.getStringExtra("type").equalsIgnoreCase("taxi_order") ||
                            intent.getStringExtra("type").equalsIgnoreCase("request_vehicle")) {
                        Intent intentintent = new Intent(MainActivity.this, RideActivity.class);
                        startActivity(intentintent);
                    }
                }
            } catch (Exception e) {
                Functions.logDMsg( "Error : Notification " + e);
            }
        }

    }
}


