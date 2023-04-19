package com.qboxus.gograbdriver.helpingclasses;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {


    public static final String PACKAGE_URL_SCHEME = "package:";
    public static boolean isToastShow = false;
    public static final String http = "http";
    public static float mapZoomLevel = 17;
    public static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
    public static SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);
    public static String folderGoGrab = Environment.getExternalStorageDirectory() + "/GrabMyTaxiDriver/";
    public static String folderDcimCareem = Environment.getExternalStorageDirectory() + "/DCIM/GrabMyTaxiDriver/";
    public static boolean isNotificationShow = false;
    public static String df1_pattern = "yyyy-MM-dd HH:mm:ss";
    public static final String emptyTime = "0000-00-00 00:00:00";
}
