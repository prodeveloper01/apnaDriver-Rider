package com.qboxus.gograbdriver.activitiesandfragments.walletfragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;

import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class WalletF extends RootFragment implements View.OnClickListener {

    View view;
    ImageView imgMenu;
    Preferences preferences;
    TextView tvCurrentBalance;
    LinearLayout btnWithdraw;
    View.OnClickListener clickListener;

    public WalletF() {
        // Required empty public constructor
    }

    public WalletF(View.OnClickListener navClickListener) {
        this.clickListener =navClickListener;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wallet,container,false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        callApiShowUserdetail(view.getContext());
        imgMenu.setOnClickListener(clickListener);
        btnWithdraw.setOnClickListener(this);
    }

    private void initControl() {
        preferences=new Preferences(view.getContext());
        imgMenu =view.findViewById(R.id.iv_nav_menu);
        tvCurrentBalance =view.findViewById(R.id.tv_current_balance);
        btnWithdraw =view.findViewById(R.id.btn_withdraw);
    }

    private void callApiShowUserdetail(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApisList.showUserDetails, sendobj, resp -> {

            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);


                    if (respobj.getString("code").equals("200")) {
                        JSONObject msgobj = respobj.getJSONObject("msg") ;

                        JSONObject user_obj = msgobj.getJSONObject("User") ;
                        JSONObject country_obj = msgobj.getJSONObject("Country") ;

                        preferences.setKeyUserId(user_obj.optString("id",""));
                        preferences.setKeyUserFirstName(user_obj.optString("first_name",""));
                        preferences.setKeyUserLastName( user_obj.optString("last_name",""));
                        preferences.setKeyUserName( user_obj.optString("username",""));
                        preferences.setKeyUserEmail(user_obj.optString("email",""));
                        preferences.setKeyUserPhone(user_obj.optString("phone",""));
                        preferences.setKeyUserRole(user_obj.optString("role",""));
                        preferences.setKeySocialId(user_obj.optString("social_id",""));
                        preferences.setKeySocialType(user_obj.optString("social",""));
                        preferences.setKeyUserToken(user_obj.optString("auth_token",""));

                        preferences.setKeyPhoneCountryCode(country_obj.optString("phonecode",""));
                        preferences.setKeyPhoneCountryName(country_obj.optString("native",""));
                        preferences.setKeyPhoneCountryIOS(country_obj.optString("iso",""));
                        preferences.setKeyPhoneCountryId(country_obj.optString("id",""));
                        preferences.setKeyUserCountryId(country_obj.optString("id",""));
                        preferences.setKeyUserCountry(country_obj.optString("native",""));

                        preferences.setKeyCurrencyName(country_obj.optString("currency",""));
                        preferences.setKeyDOB(user_obj.optString("dob",""));
                        preferences.setKeyGender(user_obj.optString("gender",""));
                        preferences.setKeyUserImage(user_obj.optString("image",""));
                        preferences.setKeyUserRole(user_obj.optString("role",""));
                        preferences.setKeyUserActive(user_obj.optString("online",""));
                        preferences.setKeyWallet(user_obj.optString("wallet","0"));
                        preferences.setKeyUserAuthToken(user_obj.optString("token",""));
                        preferences.setKeyIsLogin(true);
                        preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+"+preferences.getKeyPhoneCountryCode()),""));


                        SetupScreenData();
                    }
                } catch (Exception e) {
                    Functions.logDMsg("Exception "+e);
                }

            }

        });

    }

    private void SetupScreenData() {
        tvCurrentBalance.setText(preferences.getKeyCurrencySymbol()+" "+String.format("%.1f",Double.valueOf(preferences.getKeyWallet())));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_withdraw:
            {
               if (Double.valueOf(preferences.getKeyWallet())<1)
               {
                   Functions.showToast(view.getContext(),view.getContext().getString(R.string.you_have_insuficent_balance));
                   return;
               }

                CallApi_WithDrawFromBank(view.getContext());
            }
                break;

        }
    }

    private void CallApi_WithDrawFromBank(Context context) {

        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("amount", preferences.getKeyWallet());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApisList.withDrawRequest, sendobj, resp -> {

            Functions.cancelLoader();

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        Functions.showToast(view.getContext(),view.getContext().getString(R.string.change_applied));
                        callApiShowUserdetail(context);
                    }
                    else
                    {
                        Functions.showAlert(view.getContext(),view.getContext().getString(R.string.alert),""+respobj.optString("msg"));
                    }

                } catch (JSONException e) {
                    Functions.logDMsg("Exception "+e);
                }

            }

        });

    }

}
