package com.qboxus.gograbdriver.helpingclasses;

import android.content.Context;
import android.content.SharedPreferences;



public class Preferences {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    private final String KEY_IS_LOGIN = "is_login";
    private final String KEY_LOCALE = "user_locale";
    private final String KEY_NIGHT_MODE = "user_night_mode";
    private final String KEY_USER_ID = "user_id";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_USER_FIRST_NAME = "user_first_name";
    private final String KEY_USER_LAST_NAME = "user_last_name";
    private final String KEY_USER_EMAIL = "user_email";
    private final String KEY_USER_IMAGE = "user_image";
    private final String KEY_USER_PHONE = "user_phone";
    private final String KEY_USER_ROLE = "user_role";
    private final String KEY_USER_COUNTRY = "user_country";
    private final String KEY_USER_COUNTRY_ID = "user_country_id";
    private final String KEY_PHONE_COUNTRY_ID = "phone_country_id";
    private final String KEY_PHONE_COUNTRY_IOS = "phone_country_ios";
    private final String KEY_PHONE_COUNTRY_CODE = "phone_country_code";
    private final String KEY_PHONE_COUNTRY_NAME = "phone_country_name";
    private final String KEY_USER_DEVICE_TOKEN = "user_device_token";
    private final String KEY_USER_AUTH_TOKEN = "user_auth_token";
    private final String KEY_USER_TOKEN = "user_token";
    private final String KEY_USER_LAT = "user_lat";
    private final String KEY_USER_LNG = "user_lng";
    private final String KEY_USER_ACTIVE = "user_active";
    private final String KEY_CURRENCY_SYMBOL = "currency_symbol";
    private final String KEY_CURRENCY_NAME = "currency_Name";
    private final String KEY_USER_CURRENT_ADDRESS = "user_current_address";
    private final String KEY_USER_DOB = "user_dob";
    private final String KEY_USER_GENDER = "user_gender";
    private final String KEY_USER_WALLET = "user_wallet";
    private final String KEY_RATING = "user_rating";
    private final String KEY_SOCIAL_ID = "user_social_id";
    private final String KEY_SOCIAL_TYPE = "user_social_type";
    private final String KEY_REQUEST_ID = "ride_request_id";
    private final String KEY_TRIP_ID = "ride_trip_id";
    private final String KEY_IS_VEHICLE_SET="is_vehicle_set";
    private final String KEY_VEHICLE_ID="vehicle_id";
    private final String KEY_TOTAL_DISTANCE="total_distance";



    public Preferences(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("GrabMyTaxiDriver", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }


    public Boolean getKeyIsNightMode() {
        return prefs.getBoolean(KEY_NIGHT_MODE, false);
    }

    public void setKeyIsNightMode(Boolean keyIsNightMode) {
        editor.putBoolean(KEY_NIGHT_MODE, keyIsNightMode);
        editor.commit();
    }

    public String getKeyGender() {
        return prefs.getString(KEY_USER_GENDER, "Male");
    }

    public void setKeyGender(String KeyGender) {
        editor.putString(KEY_USER_GENDER, KeyGender);
        editor.commit();
    }

    public boolean getKeyIsVehicleSet() {
        return prefs.getBoolean(KEY_IS_VEHICLE_SET, false);
    }

    public void setKeyIsVehicleSet(boolean KeyIsVehicleSet) {
        editor.putBoolean(KEY_IS_VEHICLE_SET, KeyIsVehicleSet);
        editor.commit();
    }

    public String getKeyVehicleId() {
        return prefs.getString(KEY_VEHICLE_ID, "0");
    }

    public void setKeyVehicleId(String KeyVehicleId) {
        editor.putString(KEY_VEHICLE_ID, KeyVehicleId);
        editor.commit();
    }


    public Float getKeyRideTotalDistance() {
        return prefs.getFloat(KEY_TOTAL_DISTANCE, 0.0f);
    }

    public void setKeyRideTotalDistance(Float KeyRideTotalDistance) {
        editor.putFloat(KEY_TOTAL_DISTANCE, KeyRideTotalDistance);
        editor.commit();
    }



    public String getKeyRequestId() {
        return prefs.getString(KEY_REQUEST_ID, "0");
    }

    public void setKeyRequestId(String KeyRequestId) {
        editor.putString(KEY_REQUEST_ID, KeyRequestId);
        editor.commit();
    }


    public String getKeyTripId() {
        return prefs.getString(KEY_TRIP_ID, "0");
    }

    public void setKeyTripId(String KeyTripId) {
        editor.putString(KEY_TRIP_ID, KeyTripId);
        editor.commit();
    }


    public String getKeyDOB() {
        return prefs.getString(KEY_USER_DOB, "00-00-0000");
    }

    public void setKeyDOB(String KeyDOB) {
        editor.putString(KEY_USER_DOB, KeyDOB);
        editor.commit();
    }

    public String getKeyWallet() {
        return prefs.getString(KEY_USER_WALLET, "0");
    }

    public void setKeyWallet(String KeyWallet) {
        editor.putString(KEY_USER_WALLET, KeyWallet);
        editor.commit();
    }

    public String getKeySocialId() {
        return prefs.getString(KEY_SOCIAL_ID, "0");
    }

    public void setKeySocialId(String KeySocialId) {
        editor.putString(KEY_SOCIAL_ID, KeySocialId);
        editor.commit();
    }


    public String getKeySocialType() {
        return prefs.getString(KEY_SOCIAL_TYPE, "0");
    }

    public void setKeySocialType(String KeySocialType) {
        editor.putString(KEY_SOCIAL_TYPE, KeySocialType);
        editor.commit();
    }


    public String getKeyRating() {
        return prefs.getString(KEY_RATING, "0");
    }

    public void setKeyRating(String KeyPickupAdress) {
        editor.putString(KEY_RATING, KeyPickupAdress);
        editor.commit();
    }


    public String getKeyPhoneCountryName() {
        return prefs.getString(KEY_PHONE_COUNTRY_NAME, "PAKISTAN");
    }

    public void setKeyPhoneCountryName(String KeyCountryName) {
        editor.putString(KEY_PHONE_COUNTRY_NAME, KeyCountryName);
        editor.commit();
    }


    public String getKeyCurrentAdress() {
        return prefs.getString(KEY_USER_CURRENT_ADDRESS, "0");
    }

    public void setKeyCurrentAdress(String KeyCurrentAdress) {
        editor.putString(KEY_USER_CURRENT_ADDRESS, KeyCurrentAdress);
        editor.commit();
    }


    public String getKeyPhoneCountryCode() {
        return prefs.getString(KEY_PHONE_COUNTRY_CODE, "92");
    }

    public void setKeyPhoneCountryCode(String KeyCountryCode) {
        editor.putString(KEY_PHONE_COUNTRY_CODE, KeyCountryCode);
        editor.commit();
    }


    public String getKeyUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public void setKeyUserName(String KeyUser_Name) {
        editor.putString(KEY_USER_NAME, KeyUser_Name);
        editor.commit();
    }



    public String getKeyCurrencyName() {
        return prefs.getString(KEY_CURRENCY_NAME, "USD");
    }

    public void setKeyCurrencyName(String KeyCurrencyName) {
        editor.putString(KEY_CURRENCY_NAME, KeyCurrencyName);
        editor.commit();
    }



    public String getKeyCurrencySymbol() {
        return prefs.getString(KEY_CURRENCY_SYMBOL, "$");
    }

    public void setKeyCurrencySymbol(String KeyCurrency) {
        editor.putString(KEY_CURRENCY_SYMBOL, KeyCurrency);
        editor.commit();
    }


    public String getKeyLocale() {
        return prefs.getString(KEY_LOCALE, "en");
    }

    public void setKeyLocale(String keyLocale) {
        editor.putString(KEY_LOCALE, keyLocale);
        editor.commit();
    }

    public String getKeyUserFirstName() {
        return prefs.getString(KEY_USER_FIRST_NAME, "");
    }

    public void setKeyUserFirstName(String key_user_first_name) {
        editor.putString(KEY_USER_FIRST_NAME, key_user_first_name);
        editor.commit();
    }

    public String getKeyUserLastName() {
        return prefs.getString(KEY_USER_LAST_NAME, "");
    }

    public void setKeyUserLastName(String key_user_last_name) {
        editor.putString(KEY_USER_LAST_NAME, key_user_last_name);
        editor.commit();
    }


    public String getKeyPhoneCountryIOS() {
        return prefs.getString(KEY_PHONE_COUNTRY_IOS, "PK");
    }

    public void setKeyPhoneCountryIOS(String key_user_ios) {
        editor.putString(KEY_PHONE_COUNTRY_IOS, key_user_ios);
        editor.commit();
    }

    public String getKeyPhoneCountry() {
        return prefs.getString(KEY_USER_COUNTRY, "Pakistan");
    }

    public void setKeyUserCountry(String key_user_country) {
        editor.putString(KEY_USER_COUNTRY, key_user_country);
        editor.commit();
    }

    public String getKeyUserCountryId() {
        return prefs.getString(KEY_USER_COUNTRY_ID, "0");
    }

    public void setKeyUserCountryId(String key_user_country_id) {
        editor.putString(KEY_USER_COUNTRY_ID, key_user_country_id);
        editor.commit();
    }



    public String getKeyPhoneCountryId() {
        return prefs.getString(KEY_PHONE_COUNTRY_ID, "0");
    }

    public void setKeyPhoneCountryId(String key_country_id) {
        editor.putString(KEY_PHONE_COUNTRY_ID, key_country_id);
        editor.commit();
    }


    public String getKeyUserImage() {
        return prefs.getString(KEY_USER_IMAGE, "");
    }

    public void setKeyUserImage(String key_user_image) {
        editor.putString(KEY_USER_IMAGE, key_user_image);
        editor.commit();
    }

    public String getKeyUserToken() {
        return prefs.getString(KEY_USER_TOKEN, "");
    }

    public void setKeyUserToken(String key_user_token) {
        editor.putString(KEY_USER_TOKEN, key_user_token);
        editor.commit();
    }



    public String getKeyUserDeviceToken() {
        return prefs.getString(KEY_USER_DEVICE_TOKEN, "");
    }

    public void setKeyUserDeviceToken(String key_user_device_token) {
        editor.putString(KEY_USER_DEVICE_TOKEN, key_user_device_token);
        editor.commit();
    }

    public String getKeyUserAuthToken() {
        return prefs.getString(KEY_USER_AUTH_TOKEN, "");
    }

    public void setKeyUserAuthToken(String key_user_auth_token) {
        editor.putString(KEY_USER_AUTH_TOKEN, key_user_auth_token);
        editor.commit();
    }

    public String getKeyUserRole() {
        return prefs.getString(KEY_USER_ROLE, "driver");
    }

    public void setKeyUserRole(String key_user_role) {
        editor.putString(KEY_USER_ROLE, key_user_role);
        editor.commit();
    }

    public String getKeyUserLat() {
        return prefs.getString(KEY_USER_LAT, "0");
    }

    public void setKeyUserLat(String key_user_lat) {
        editor.putString(KEY_USER_LAT, key_user_lat);
        editor.commit();
    }

    public String getKeyUserLng() {
        return prefs.getString(KEY_USER_LNG, "0");
    }

    public void setKeyUserLng(String key_user_lng) {
        editor.putString(KEY_USER_LNG, key_user_lng);
        editor.commit();
    }



    public String getKeyUserActive() {
        return prefs.getString(KEY_USER_ACTIVE, "0");
    }

    public void setKeyUserActive(String key_user_active) {
        editor.putString(KEY_USER_ACTIVE, key_user_active);
        editor.commit();
    }



    public Boolean getKeyIsLogin() {
        return prefs.getBoolean(KEY_IS_LOGIN, false);
    }

    public void setKeyIsLogin(Boolean keyIsLogin) {
        editor.putBoolean(KEY_IS_LOGIN, keyIsLogin);
        editor.commit();
    }

    public String getKeyUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public void setKeyUserEmail(String keyEmail) {
        editor.putString(KEY_USER_EMAIL, keyEmail);
        editor.commit();
    }

    public String getKeyUserId() {
        return prefs.getString(KEY_USER_ID, "0");
    }

    public void setKeyUserId(String keyUserId) {
        editor.putString(KEY_USER_ID, keyUserId);
        editor.commit();
    }

    public String getKeyUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "0000000000");
    }

    public void setKeyUserPhone(String keyUserPhone) {
        editor.putString(KEY_USER_PHONE, keyUserPhone);
        editor.commit();
    }

    public void clearSharedPreferences() {
        editor.clear();
        editor.commit();
    }




}
