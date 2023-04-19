package com.qboxus.gograbdriver.activitiesandfragments.mainnavigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.qboxus.gograbdriver.appinterfaces.CallBackInternet;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import java.util.Locale;

public class NoInternetA extends AppCompatActivity {

    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet_);
        preferences=new Preferences(NoInternetA.this);
        if (preferences.getKeyIsNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setLocale(""+preferences.getKeyLocale());
        initControl();
    }

    private void initControl() {
        Functions.RegisterConnectivity(this, new CallBackInternet() {
            @Override
            public void GetResponse(String requestType, String response) {
                if(response.equalsIgnoreCase("connected")) {
                    finish();
                    overridePendingTransition(R.anim.in_from_top,R.anim.out_from_bottom);
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
}