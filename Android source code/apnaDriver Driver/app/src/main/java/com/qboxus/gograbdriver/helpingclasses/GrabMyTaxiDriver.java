package com.qboxus.gograbdriver.helpingclasses;

import static com.qboxus.gograbdriver.helpingclasses.ImagePipelineConfigUtils.getDefaultImagePipelineConfig;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;

public class GrabMyTaxiDriver extends Application {

    public static final String CHANNEL_ID = "GrabMyTaxiDriverNotificationId";
    public static final String CHANNEL_NAME = "GrabMyTaxiDriverNotificationName";

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this,getDefaultImagePipelineConfig(this));
        FirebaseApp.initializeApp(this);
        createNotificationChannel();
    }




    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "You are Online",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
