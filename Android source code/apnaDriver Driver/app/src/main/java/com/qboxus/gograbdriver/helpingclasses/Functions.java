package com.qboxus.gograbdriver.helpingclasses;

import static android.content.Context.CONNECTIVITY_SERVICE;

import static com.qboxus.gograbdriver.helpingclasses.Variables.PACKAGE_URL_SCHEME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.LatLng;
import com.qboxus.gograbdriver.appinterfaces.APICallBack;
import com.qboxus.gograbdriver.appinterfaces.CallBackInternet;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.DrawableCallback;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.BuildConfig;
import com.qboxus.gograbdriver.Constants;
import com.qboxus.gograbdriver.models.MyOrdersModel;
import com.qboxus.gograbdriver.R;
import com.qboxus.gograbdriver.models.RecipientModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Functions {
    static FileOutputStream stream = null;
    static BufferedReader br = null;
    static Dialog dialog;
    static BroadcastReceiver broadcastReceiver;
    static IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    static IntentFilter intentFilter1 = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);

    //////////Show KEYBOARD
    public static void showKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }


    //show permission setting screen
    public static void showPermissionSetting(Context context,String type) {

        Functions.customAlertDialogDenied(context, type, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
                context.startActivity(intent);
            }
        });
    }


    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(String url,int resource, SimpleDraweeView simpleDrawee, boolean isGif)
    {
        if (url==null)
        {
            url="null";
        }
        if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .build();

        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(resource);
        simpleDrawee.getHierarchy().setFailureImage(resource);
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDrawee.getController())
                    .build();
        }



        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(Drawable drawable, SimpleDraweeView simpleDrawee, boolean isGif)
    {


        DraweeController controller;
        simpleDrawee.getHierarchy().setPlaceholderImage(drawable);
        simpleDrawee.getHierarchy().setFailureImage(drawable);
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDrawee.getController())
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDrawee.getController())
                    .build();
        }

        return controller;
    }

    // use for image loader and return controller for image load
    public static DraweeController frescoImageLoad(Uri resourceUri, boolean isGif)
    {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(resourceUri)
                .build();
        DraweeController controller;
        if (isGif)
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .build();
        }
        else
        {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
        }



        return controller;
    }


    public static void uriFromURL(String url,DrawableCallback callback) {

        if (url==null)
        {
            url="null";
        }
        if (!url.contains(Variables.http)) {
            url = Constants.BASE_URL + url;
        }
        new GetImageFromUrl(callback).execute(url);
    }

    public static Bitmap getRoundBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }



    //get private storeage directory
    public static String getAppFolder(Context activity)
    {
        try {
            return activity.getExternalFilesDir(null).getPath()+"/";
        }
        catch (Exception e)
        {
            return Environment.getDataDirectory().getPath()+"/";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void wrtieFileOnInternalStorage(Context mcoContext, String sFileName, String sBody, String time, String apllication_name) throws IOException {


        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "ParcelRider");

        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdirs()) {
                createFile(mediaStorageDir, mcoContext, sFileName, sBody, time, apllication_name);
            }

        } else {
            createFile(mediaStorageDir, mcoContext, sFileName, sBody, time, apllication_name);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void createFile(File path, Context mcoContext, String sFileName, String sBody, String time, String apllication_name) throws IOException {


        File file = new File(path, sFileName + ".txt");

        if (file.exists()) {
            String data = getContentFile("" + file, sBody, time, apllication_name);

        } else {

            FileOutputStream stream = new FileOutputStream(file);

            try {
                stream.write((sBody + "   " + time + "   " + apllication_name).getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getContentFile(String path, String sbody, String time, String apllication_name) throws IOException {
        try {
            br = new BufferedReader(new FileReader(path));


            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            String everything = sb.toString();
            br.close();

            stream = new FileOutputStream(path);
            stream.write((sbody + "    " + time + "    " + apllication_name).getBytes());
            return everything;

        } catch (Exception e) {
            return "error";
        } finally {
            if(br!=null)
            br.close();
        }
    }

    public static String convertBitmapToBase64(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    public static void orderParseData(JSONArray jsonArray, String senerio, APICallBack apiCallBack) {

        ArrayList<MyOrdersModel> tempList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject orderObject = jsonArray.getJSONObject(i);

                JSONObject foodObject = orderObject.optJSONObject("FoodOrder");
                JSONObject parcelorder = orderObject.optJSONObject("ParcelOrder");
                JSONObject riderOrder = orderObject.optJSONObject("RiderOrder");

                MyOrdersModel ordersModel = new MyOrdersModel();

                if (!foodObject.optString("id").equals("null")) {

                    ordersModel.id = foodObject.optString("id");
                    ordersModel.orderCreateDate = foodObject.optString("created");
                    ordersModel.orderType = "food";

                    ordersModel.deliveryDatetime = foodObject.optString("delivery_date_time");

                    ordersModel.orderPickupTime = foodObject.optString("delivery_date_time");

                    ordersModel.signature = foodObject.optString("signature");

                    JSONObject userObj = foodObject.optJSONObject("User");
                    JSONObject userPlaceObj = foodObject.optJSONObject("UserPlace");
                    JSONObject restaurantObj = foodObject.optJSONObject("Restaurant");

                    ordersModel.deliveryInstruction = foodObject.optString("rider_instruction");

                    ordersModel.senderLocationString = restaurantObj.optString("location_string");

                    ordersModel.recevierLocationString = userPlaceObj.optString("location_string");

                    ordersModel.orderPersonName = userObj.optString("first_name") + " " + userObj.optString("last_name");
                    ordersModel.orderPersonId = userObj.optString("id");
                    ordersModel.orderPersonPhone = userObj.optString("phone");
                    ordersModel.orderPersonEmail = userObj.optString("email");

                    ordersModel.receiverLat = userPlaceObj.optString("lat");
                    ordersModel.receiverLong = userPlaceObj.optString("long");


                    ordersModel.senderName = restaurantObj.optString("name");
                    ordersModel.senderId = restaurantObj.optString("id");
                    ordersModel.senderPhone = restaurantObj.optString("phone");
                    ordersModel.senderLat = restaurantObj.optString("lat");
                    ordersModel.senderLong = restaurantObj.optString("long");

                }

               else if (!parcelorder.optString("id").equals("null")) {
                    ordersModel.id = parcelorder.optString("id");

                    ordersModel.orderCreateDate = parcelorder.optString("created");
                    ordersModel.orderType = "parcel";
                    ordersModel.deliveryDatetime = parcelorder.optString("delivery_date_time");
                    ordersModel.orderPickupTime = parcelorder.optString("pickup_datetime");
                    ordersModel.signature = parcelorder.optString("signature");
                    ordersModel.senderLocationString = parcelorder.optString("sender_location_string");
                    ordersModel.senderName = parcelorder.optString("sender_name");
                    ordersModel.senderId = parcelorder.optString("id");
                    ordersModel.senderPhone = parcelorder.optString("sender_phone");
                    ordersModel.senderEmail = parcelorder.optString("sender_email");
                    ordersModel.senderLat = parcelorder.optString("sender_location_lat");
                    ordersModel.senderLong = parcelorder.optString("sender_location_long");
                    ordersModel.senderAddressDetail = parcelorder.optString("sender_address_detail");
                    ordersModel.addressInstructionPickUp = parcelorder.optString("sender_note_driver");

                    JSONObject userObj = parcelorder.optJSONObject("User");
                    ordersModel.orderPersonId = userObj.optString("id");


                    JSONArray orderMultipleArray = parcelorder.optJSONArray("ParcelOrderMultiStop");
                    for (int j=0;j<orderMultipleArray.length();j++){
                        JSONObject object=orderMultipleArray.getJSONObject(j);
                        JSONObject PackageSize=object.getJSONObject("PackageSize");
                        RecipientModel recipientModel=new RecipientModel();
                        recipientModel.setRecipientName(object.optString("receiver_name"));
                        recipientModel.setRecipientNumber(object.optString("receiver_phone"));
                        recipientModel.setRecipientAddress(object.optString("receiver_location_string"));
                        recipientModel.setRecipientNote(object.optString("receiver_note_driver"));
                        recipientModel.setDeliveryInstruction(object.optString("delivery_instruction"));
                        recipientModel.setTypeOfItem(object.optString("item_title"));
                        recipientModel.setTypeOfItemId(object.optString("good_type_id"));

                        try {
                            recipientModel.setRecipientLat(Double.parseDouble(object.optString("receiver_location_lat")));
                            recipientModel.setRecipientLong(Double.parseDouble(object.optString("receiver_location_long")));
                        }catch (Exception e){}

                        recipientModel.setPackageSize(PackageSize.optString("title"));
                        recipientModel.setPackageID(PackageSize.optString("id"));
                        recipientModel.setPrice(PackageSize.optString("price"));

                        ordersModel.recipientList.add(recipientModel);
                    }

                }

                ordersModel.delivered = riderOrder.optString("delivered");
                ordersModel.onTheWayToPickup = riderOrder.optString("on_the_way_to_pickup");
                ordersModel.pickupDatetime = riderOrder.optString("pickup_datetime");
                ordersModel.onTheWayToDropoff = riderOrder.optString("on_the_way_to_dropoff");

                ordersModel.currentView = senerio;

                tempList.add(ordersModel);

            } catch (Exception e) {
                e.printStackTrace();
                Functions.logDMsg( "exception while : " + e.toString());
                apiCallBack.ArrayData(tempList);
            }

        }

        apiCallBack.ArrayData(tempList);

    }

    public static String convertDatetime(String date, String isfrom) {
        DateFormat df = new SimpleDateFormat(Variables.df1_pattern, Locale.ENGLISH);

        java.util.Date d = null;
        try {
            d = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (isfrom.equals("convert_dateonly")) {
            df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        } else if (isfrom.equals("history")) {
            df = new SimpleDateFormat("dd MMM hh a", Locale.ENGLISH);
        } else if (isfrom.equals("datetime")) {
            df = new SimpleDateFormat("dd MMM hh a", Locale.ENGLISH);
        } else if (isfrom.equals("member_since")) {
            df = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        } else if (isfrom.equalsIgnoreCase("pickup_date")) {
            df = new SimpleDateFormat("dd MMM hh:mm a", Locale.ENGLISH);
        } else {
            df = new SimpleDateFormat("MMM-dd hh:mm a", Locale.ENGLISH);
        }
        return df.format(d);
    }

    public static void showLoader(Context context, boolean outsideTouch, boolean cancleable) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loader_dialog);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_white_background));

        if (!outsideTouch)
            dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            dialog.setCancelable(false);

        dialog.show();
    }

    //    check email valid formate
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void cancelLoader() {
        if (dialog != null) {
            dialog.cancel();
            dialog.dismiss();
        }
    }

    // first letter captial
    public static String SetFirstLetterCapital(String word) {
        if (word.length() > 1)
            word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        return word;
    }

    /*Clear the Background of AlertDialogues */
    @SuppressLint("ResourceType")
    public static void clearBackgrounds(View view) {

        while (view != null) {
            view.setBackgroundResource(android.graphics.Color.TRANSPARENT);
            final ViewParent parent = view.getParent();
            if (parent instanceof View) {
                view = (View) parent;
            } else {
                view = null;
            }
        }
    }

    //make title case sentence
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null && arr[i].length() > 0) {
                    sb.append(Character.toUpperCase(arr[i].charAt(0)))
                            .append(arr[i].substring(1)).append(" ");
                }
            }
        }
        return sb.toString().trim();
    }

    public static void RemoveSingleFragment(FragmentManager fm, String tagName) {
        Functions.logDMsg( "name " + tagName);
        Fragment fragment = fm.findFragmentByTag(tagName);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
            fm.popBackStack();
        }
    }

    public static int convertDpToPx(Context context, int dp) {
        return (int) ((int) dp * context.getResources().getDisplayMetrics().density);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    // unregister internet broadcaster
    public static void unRegisterConnectivity(Context mContext) {
        try {
            if (broadcastReceiver != null)
                mContext.unregisterReceiver(broadcastReceiver);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    // register internet broadcaster
    public static void RegisterConnectivity(Context context, final CallBackInternet callback) {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isConnectedToInternet(context)) {
                    callback.GetResponse("alert", "connected");
                } else {
                    callback.GetResponse("alert", "disconnected");
                }
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    // check internet conectivity
    public static Boolean isConnectedToInternet(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("NetworkChange", e.getMessage());
            return false;
        }
    }


    //    get top active fragment according to fragment manager
    public static String getActiveFragment(FragmentManager fm) {
        if (fm.getBackStackEntryCount() == 0) {
            return "null";
        }
        String tag = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
        return tag;
    }

    //    open fragment and clear all previously active fragment use in side drawer navigation
    public static void methodopennavfragment(Fragment f, FragmentManager fm, String FragmentName, View view) {

        if (FragmentName.equals(Functions.getActiveFragment(fm))) {
            //do nothing
        } else {
            clearFragmentByTag(fm);
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            ft.replace(view.getId(), f, FragmentName).addToBackStack(FragmentName).commit();
        }

    }

    //  claer previous all support level fragments
    public static void clearFragment(FragmentManager fm) {
        try {

            for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
                fm.popBackStack();
            }


        } catch (Exception e) {
            System.out.print("!====Popbackstack error : " + e);
            e.printStackTrace();
        }
    }

    //    clear previously active fragment
    public static void clearFragmentByTag(FragmentManager fm) {
        try {
            for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
                Fragment fragment = fm.findFragmentByTag(fm.getBackStackEntryAt(i).getName());
                if (fragment != null) {
                    fm.beginTransaction().remove(fragment).commit();
                    fm.popBackStack();
                }

            }

        } catch (Exception e) {
            System.out.print("!====Popbackstack error : " + e);
            e.printStackTrace();
        }
    }


    public static void logDMsg(String msg) {
        if (BuildConfig.DEBUG && !Constants.isSecureInfo)
            Log.d(Constants.tag, msg);
    }


//    for get valid phone number
    public static String getValidPhoneNumber(String code,String PhoneNo) {
        String phoneNumber=PhoneNo;
        if (phoneNumber.charAt(0)=='0')
        {
            phoneNumber=phoneNumber.substring(1);
        }
        if (phoneNumber.charAt(0) != '+') {
            phoneNumber = "+"+phoneNumber;
        }
        String countryCode=code;
        countryCode=countryCode.replace("+","");
        phoneNumber = phoneNumber.replace("+" + countryCode, "");
        phoneNumber = phoneNumber.replace("+", "");
        phoneNumber = phoneNumber.replace(" ", "");
        phoneNumber = phoneNumber.replace("(", "");
        phoneNumber = phoneNumber.replace(")", "");
        phoneNumber = phoneNumber.replace("-", "");
        phoneNumber = "+" + countryCode + phoneNumber;
        return phoneNumber;
    }


    public static double getBearingBetweenTwoPoints1(LatLng latLng1, LatLng latLng2) {
        if (latLng1 == null) {
            latLng1 = latLng2;
        }

        double lat1 = degreesToRadians(latLng1.latitude);
        double long1 = degreesToRadians(latLng1.longitude);
        double lat2 = degreesToRadians(latLng2.latitude);
        double long2 = degreesToRadians(latLng2.longitude);


        double dLon = (long2 - long1);


        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double radiansBearing = Math.atan2(y, x);


        return radiansToDegrees(radiansBearing);
    }

    public static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }

    public static double radiansToDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }

    public static double computeRotation(float fraction, double start, double end) {
        double normalizeEnd = end - start; // rotate start to 0
        double normalizedEndAbs = (normalizeEnd + 360) % 360;

        double direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        double rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        double result = fraction * rotation + start;
        return (result + 360) % 360;
    }


    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static void showAlert(Activity activity, String title, String message, String nagtiveButton, String positiveButton, final CallbackResponce callbackResponce) {

        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.show_defult_alert_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView txt_yes, txt_no, txt_title, txt_message;
        txt_title = dialog.findViewById(R.id.defult_alert_txt_title);
        txt_message = dialog.findViewById(R.id.defult_alert_txt_message);
        txt_no = dialog.findViewById(R.id.defult_alert_btn_cancel_no);
        txt_yes = dialog.findViewById(R.id.defult_alert_btn_cancel_yes);

        txt_no.setVisibility(View.VISIBLE);
        txt_no.setText(nagtiveButton);
        txt_yes.setVisibility(View.VISIBLE);
        txt_yes.setText(positiveButton);

        txt_title.setText("" + title);
        txt_message.setText("" + message);
        txt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callbackResponce.responce("yes");
            }
        });


        txt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                callbackResponce.responce("no");
            }
        });
        dialog.show();
    }


    public static void showAlert(Context context, String title, String Message) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.show_defult_alert_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView txtYes, txtNo, txtTitle, txtMessage;
        txtTitle = dialog.findViewById(R.id.defult_alert_txt_title);
        txtMessage = dialog.findViewById(R.id.defult_alert_txt_message);
        txtNo = dialog.findViewById(R.id.defult_alert_btn_cancel_no);
        txtYes = dialog.findViewById(R.id.defult_alert_btn_cancel_yes);

        txtTitle.setText("" + title);
        txtMessage.setText("" + Message);
        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        txtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        dialog.show();
    }


    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        if (ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)) {
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }

    public static void showDoubleButtonAlert(Context context, String title, String message, String negTitle, String posTitle, boolean isCancelable, FragmentCallback callBack) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.show_double_button_new_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tvtitle, tvMessage, tvPositive, tvNegative;
        tvtitle = dialog.findViewById(R.id.tvtitle);
        tvMessage = dialog.findViewById(R.id.tvMessage);
        tvNegative = dialog.findViewById(R.id.tvNegative);
        tvPositive = dialog.findViewById(R.id.tvPositive);


        tvtitle.setText(title);
        tvMessage.setText(message);
        tvNegative.setText(negTitle);
        tvPositive.setText(posTitle);

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShow", false);
                callBack.Responce(bundle);
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShow", true);
                callBack.Responce(bundle);
            }
        });
        dialog.show();
    }


    public static void customAlertDialogDenied(Context context, String fromWhere, final CallbackResponce callbackResponse) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_alert_dialouge_location_denied);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_white_background));

        TextView okBtn = dialog.findViewById(R.id.tvPositive);
        TextView cancelBtn = dialog.findViewById(R.id.tvNegative);
        TextView tvTwo = dialog.findViewById(R.id.tvTwo);
        TextView title = dialog.findViewById(R.id.title);

        if (fromWhere.equals("location")) {
            title.setText(context.getResources().getString(R.string.location_heading_denied_heading));
            tvTwo.setText(context.getResources().getString(R.string.location_heading_denied_step_2));
        } else if (fromWhere.equals("camera")) {
            title.setText(R.string.to_upload_image_permission_string);
            tvTwo.setText(context.getResources().getString(R.string.genral_permison));
        } else {
            title.setText(context.getResources().getString(R.string.to_upload_image_permission_string_1));
            tvTwo.setText(context.getResources().getString(R.string.genral_permison));
        }

        okBtn.setOnClickListener(view -> {
            callbackResponse.responce("okay");
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }




    public static void showToast(Context context, String msg) {
        if (Variables.isToastShow) {
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
        }
    }


    public static void getLocationString(Activity activity, LatLng latLng, CallbackResponce callbackResponce) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                if (!locality.isEmpty() && !country.isEmpty()) {
                    callbackResponce.responce(locality + " " + country);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String changeDateFormat(String fromFormat, String toFormat, String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat);
        Date sourceDate = null;
        try {
            sourceDate = dateFormat.parse(date);

            SimpleDateFormat targetFormat = new SimpleDateFormat(toFormat);

            return targetFormat.format(sourceDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }


    public static Double roundoffDecimal(Double value) {
        try {
            String patern = "##.###"; //your pattern as per need
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
            decimalFormat.applyPattern(patern);
            return Double.valueOf(decimalFormat.format(value));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }


    public static void open_google_map(Context context,LatLng origin,LatLng destination) {
        if (origin != null && destination!=null) {
            String gurl =
                    "http://maps.google.com/maps?saddr=" + origin.latitude + "," + origin.longitude + "&" + "daddr=" + destination.latitude + "," +destination.longitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gurl));
            context.startActivity(intent);
        }
    }

    public static void openMap(Activity activity,String lat,String lng){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.google.com")
                .appendPath("maps")
                .appendPath("dir")
                .appendPath("")
                .appendQueryParameter("api", "1")
                .appendQueryParameter("destination", lat + "," + lng);
        String url = builder.build().toString();
        Log.d("Directions", url);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    public static void  makePhone(Activity activity,String number){

                if (number != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", number, null));
                    activity.startActivity(intent);
                }
    }

    //                String number = tvDropoffInfoNumber.getText().toString();
//                if (number != null) {
//                    Intent intent = new Intent(Intent.ACTION_DIAL,
//                            Uri.fromParts("tel", number, null));
//                    startActivity(intent);
//                }



}
