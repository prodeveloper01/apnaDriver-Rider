package com.qboxus.gograbdriver.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.qboxus.gograbdriver.activitiesandfragments.chatmodule.ChatA;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Variables;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Set;

import me.leolin.shortcutbadger.ShortcutBadger;


public class FirebaseNotificationService extends FirebaseMessagingService {

    String receiverID;
    String title;
    String message;
    String senderID;
    String type;
    String requestID;
    String image;


    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {

            Functions.logDMsg("Notification : " + new JSONObject(remoteMessage.getData()));
            if (remoteMessage.getData().size() > 0) {
                title = remoteMessage.getData().get("title");
                message = remoteMessage.getData().get("body");
                type = remoteMessage.getData().get("type");
                receiverID = remoteMessage.getData().get("user_id");
                senderID = remoteMessage.getData().get("sender_id");
                requestID = remoteMessage.getData().get("request_id");
                image = remoteMessage.getData().get("image");

                try {
                    JSONObject json = new JSONObject();
                    Set<String> keys = remoteMessage.getData().keySet();
                    for (String key : keys) {
                        json.put(key, JSONObject.wrap(remoteMessage.getData().get(key)));
                        Functions.logDMsg(json.toString());
                    }

                    if(json.has("sender")){
                        JSONObject sender=new JSONObject(json.optString("sender"));
                        title=sender.optString("first_name")+" "+sender.optString("last_name") ;
                        senderID=sender.optString("id");
                        image = sender.optString("image");
                    }

                    if(json.has("receiver")){
                        JSONObject receiver=new JSONObject(json.optString("receiver"));
                        receiverID = receiver.optString("id");
                    }

                } catch(JSONException e) {

                }

                if (Variables.isNotificationShow && type.equalsIgnoreCase("single_message")) {

                } else {
                    if((type!=null && type.equalsIgnoreCase("chat"))){
                        if(requestID!=null && !requestID.equals(ChatA.orderId))
                            showNotification(this);
                    }
                    else
                        showNotification(this);



                }


                HandleBadges(getApplicationContext());
            }
        } catch (Exception e) {
            Functions.logDMsg("Notification Exception " + e);
        }


    }


    private void HandleBadges(Context context) {
        try {
            int count = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                count = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).getActiveNotifications().length;
            }
            ShortcutBadger.applyCountOrThrow(context, count);
        } catch (Exception e) {
            Functions.logDMsg("Exception : " + e);
        }

    }


    public void showNotification(Context context) {
        try {



            // The id of the channel.
            final String CHANNEL_ID = "default";
            final String CHANNEL_NAME = "Default";

            Intent notificationIntent;
            if(type!=null && type.equals("chat")){
                Functions.logDMsg("showNotification:"+type);
                notificationIntent = new Intent(context, ChatA.class);
                notificationIntent.putExtra("senderid", receiverID);
                notificationIntent.putExtra("user_id", senderID);
                notificationIntent.putExtra("user_name", title);
                notificationIntent.putExtra("user_img", image);
                notificationIntent.putExtra("order_id", requestID);
                notificationIntent.putExtra("type", "user_rider_chat");

            }
            else {
                notificationIntent = new Intent(context, MainActivity.class);
                notificationIntent.putExtra("title", title);
                notificationIntent.putExtra("message", message);
                notificationIntent.putExtra("type", type);
            }


            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent=null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            }
            else {
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel defaultChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(defaultChannel);
            }



            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            Notification notification = builder.build();
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(100, notification);



        } catch (Exception e) {
            Functions.logDMsg("Notification Error " + e);
        }
    }

}
