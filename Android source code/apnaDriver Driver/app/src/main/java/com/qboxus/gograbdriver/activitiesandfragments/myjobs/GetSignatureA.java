package com.qboxus.gograbdriver.activitiesandfragments.myjobs;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;


import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.EnablelocationA;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.NoInternetA;
import com.qboxus.gograbdriver.appinterfaces.CallBackInternet;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.DrawingViewUtils;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

public class GetSignatureA extends AppCompatActivity implements View.OnClickListener {

    private DrawingViewUtils mDrawingViewUtils;
    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke;
    private TextView signedText;
    private String orderId;
    private String orderType;
    private String multiDropId;
    private GpsStatusReceiver receiver = new GpsStatusReceiver();
    Preferences preferences;

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        preferences = new Preferences(GetSignatureA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale("" + preferences.getKeyLocale());



        setContentView(R.layout.activity_getsignature);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            orderId = extras.getString("order_id");
            orderType = extras.getString("order_type");
            multiDropId = extras.getString("multiDropId");
        }

        signedText = findViewById(R.id.signed_text);
        mDrawingViewUtils = findViewById(R.id.main_drawing_view);

        findViewById(R.id.iv_back).setOnClickListener(this::onClick);
        findViewById(R.id.tv_reset).setOnClickListener(this::onClick);
        findViewById(R.id.tv_submit).setOnClickListener(this::onClick);

        initDrawingView();

        checkLocation();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_reset:
                mDrawingViewUtils.clearCanvas();
                break;

            case R.id.tv_submit:
                if (!signedText.getText().toString().isEmpty()) {
                    try {
                        if(orderType.equals("food")) {
                            callApiAddFoodSignature(GetSignatureA.this, Functions.convertBitmapToBase64(mDrawingViewUtils.getBitmap()), orderId);
                        }
                        else {
                            callApiAddParcelSignature(GetSignatureA.this, Functions.convertBitmapToBase64(mDrawingViewUtils.getBitmap()), orderId);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Functions.showAlert(this, getResources().getString(R.string.alert), getResources().getString(R.string.please_enter_name));
                }
                break;

            default:
                return;
        }
    }

    private void initDrawingView() {
        mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
        mCurrentColor = ContextCompat.getColor(this, android.R.color.black);
        mCurrentStroke = 10;

        mDrawingViewUtils.setBackgroundColor(mCurrentBackgroundColor);
        mDrawingViewUtils.setPaintColor(mCurrentColor);
        mDrawingViewUtils.setPaintStrokeWidth(mCurrentStroke);
    }

    /*
        This method will call the api to add the signature and on succesfull response this activity will close.
     */
    private void callApiAddFoodSignature(Context context, String base64, String order_id) throws IOException {

        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("food_order_id", order_id);
            sendobj.put("signature_person_name", signedText.getText().toString());
            sendobj.put("signature", base64);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Functions.wrtieFileOnInternalStorage(context, "signature_api_params", sendobj.toString(), "", "");
        }

        Functions.showLoader(context, false, false);
        ApiRequest.callApi(GetSignatureA.this, ApisList.addSignature, sendobj, resp -> {

            Functions.cancelLoader();

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "ok");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    private void callApiAddParcelSignature(Context context, String base64, String order_id) throws IOException {

        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("parcel_order_id", order_id);
            sendobj.put("rider_order_multi_stop_id", multiDropId);
            sendobj.put("signature_person_name", signedText.getText().toString());
            sendobj.put("signature", base64);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Functions.wrtieFileOnInternalStorage(context, "signature_api_params", sendobj.toString(), "", "");
        }

        Functions.showLoader(context, false, false);
        ApiRequest.callApi(GetSignatureA.this, ApisList.addSignatureParcelOrder, sendobj, resp -> {

            Functions.cancelLoader();

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "ok");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver == null) {
            registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        }

        Functions.RegisterConnectivity(this, new CallBackInternet() {
            @Override
            public void GetResponse(String requestType, String response) {
                if (response.equalsIgnoreCase("disconnected")) {
                    startActivity(new Intent(GetSignatureA.this, NoInternetA.class));
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    private void checkLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        boolean isEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isEnabled) {
            enableLocation();
        }
    }

    private void enableLocation() {
        startActivity(new Intent(GetSignatureA.this, EnablelocationA.class));
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Functions.unRegisterConnectivity(GetSignatureA.this);
    }

    private class GpsStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkLocation();
        }
    }

}
