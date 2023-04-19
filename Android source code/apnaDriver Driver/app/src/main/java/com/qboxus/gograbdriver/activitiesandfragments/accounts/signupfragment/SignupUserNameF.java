package com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.messaging.FirebaseMessaging;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.RequestRegisterUserModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SignupUserNameF extends Fragment implements View.OnClickListener {


    RequestRegisterUserModel model;
    View view;
    ImageView ivBack;
    LinearLayout btnSignup;
    EditText etUsername;
    Preferences preferences;
    String token;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_signup_user_name_, container, false);
        initControl();
        actionControl();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    token = task.getResult();
                });

        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    private void initControl() {
        preferences=new Preferences(view.getContext());
        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        ivBack =view.findViewById(R.id.iv_back);
        btnSignup =view.findViewById(R.id.btn_signup);
        etUsername =view.findViewById(R.id.et_username);


        setupScreenData();
    }

    private void setupScreenData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
            {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_signup:
                Functions.hideSoftKeyboard(getActivity());
            {
                if(TextUtils.isEmpty(etUsername.getText().toString()))
                {
                    etUsername.setError(""+getResources().getString(R.string.cant_empty));
                    etUsername.setFocusable(true);
                    return;
                }
                if(etUsername.getText().toString().length()<4 && etUsername.getText().toString().length()>14)
                {
                    etUsername.setError(""+getResources().getString(R.string.username_length_must_be));
                    etUsername.setFocusable(true);
                    return;
                }
                if(etUsername.getText().toString().contains(" "))
                {
                    etUsername.setError(""+getResources().getString(R.string.blank_space_empty));
                    etUsername.setFocusable(true);
                    return;
                }
                model.setUserName(""+ etUsername.getText().toString());
                callRegisterUserApi();


            }
            break;

            default:
                break;
        }
    }

    private void callRegisterUserApi() {
        Functions.logDMsg(""+model.getSocialType());
        if (model.getSocialType().equalsIgnoreCase("email"))
        {
            callApiRegisterUserWithEmail();
        }
        else
        {
            callApiRegisterUserWithSocial();
        }
    }


    private void callApiRegisterUserWithEmail() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("first_name",""+model.getFirstName());
            sendobj.put("last_name", ""+model.getLastName());
            sendobj.put("dob", "");
            sendobj.put("username", etUsername.getText().toString());
            sendobj.put("email", ""+model.getEmail());
            sendobj.put("password", ""+model.getPassword());
            String phoneNo=Functions.getValidPhoneNumber(model.getCountryCode(),
                    model.getPhoneNumber());
            sendobj.put("phone", ""+phoneNo);
            sendobj.put("country_id", ""+model.getCountryId());
            sendobj.put("role", "driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(),false,false);
        Functions.logDMsg("sendobj "+sendobj.toString());
        ApiRequest.callApi(getContext(), ApisList.registerUser, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();

                if (resp != null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            methodSaveUserDetails(resp);
                        }else {
                            Functions.logDMsg( ""+respobj.getString("msg"));
                            Functions.showAlert(view.getContext(),""+view.getContext().getString(R.string.signup_status),""+respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void methodSaveUserDetails(String resp) {

        if (resp!=null){
            try {
                JSONObject respobj = new JSONObject(resp);
                if(respobj.getString("code").equals("200"))
                {

                    JSONObject msgobj = respobj.getJSONObject("msg") ;

                    JSONObject userObj = msgobj.getJSONObject("User") ;
                    JSONObject countryObj = msgobj.getJSONObject("Country") ;
                    JSONArray vehicleArray = msgobj.optJSONArray("Vehicle") ;

                    preferences.setKeyUserId(userObj.optString("id",""));
                    preferences.setKeyUserFirstName(userObj.optString("first_name",""));
                    preferences.setKeyUserLastName( userObj.optString("last_name",""));
                    preferences.setKeyUserName( userObj.optString("username",""));
                    preferences.setKeyUserEmail(userObj.optString("email",""));
                    preferences.setKeyUserPhone(userObj.optString("phone",""));
                    preferences.setKeyUserRole(userObj.optString("role",""));
                    preferences.setKeySocialId(userObj.optString("social_id",""));
                    preferences.setKeySocialType(userObj.optString("social",""));
                    preferences.setKeyUserToken(userObj.optString("auth_token",""));

                    preferences.setKeyPhoneCountryCode(countryObj.optString("phonecode",""));
                    preferences.setKeyPhoneCountryName(countryObj.optString("native",""));
                    preferences.setKeyPhoneCountryIOS(countryObj.optString("iso",""));
                    preferences.setKeyPhoneCountryId(countryObj.optString("id",""));
                    preferences.setKeyUserCountryId(countryObj.optString("id",""));
                    preferences.setKeyUserCountry(countryObj.optString("native",""));

                    preferences.setKeyCurrencyName(countryObj.optString("currency",""));
                    preferences.setKeyDOB(userObj.optString("dob",""));
                    preferences.setKeyGender(userObj.optString("gender",""));
                    preferences.setKeyUserImage(userObj.optString("image",""));
                    preferences.setKeyUserDeviceToken(""+token);
                    preferences.setKeyUserRole(userObj.optString("role",""));
                    preferences.setKeyUserActive(userObj.optString("online",""));
                    preferences.setKeyWallet(userObj.optString("wallet",""));
                    preferences.setKeyUserAuthToken(userObj.optString("token",""));
                    preferences.setKeyIsLogin(true);
                    preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+"+preferences.getKeyPhoneCountryCode()),""));

                    try {
                        if (vehicleArray.length()>0)
                        {
                            preferences.setKeyIsVehicleSet(true);
                            JSONObject InnerObj = vehicleArray.getJSONObject(0) ;
                            preferences.setKeyVehicleId(InnerObj.optString("ride_type_id"));
                        }
                    }
                    catch (Exception e)
                    {
                        Functions.logDMsg("Exception"+e);
                    }
                    Intent intent=new Intent(view.getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            } catch (Exception e) {
                Functions.logDMsg("Exception "+e);
            }
        }
    }


    private void callApiRegisterUserWithSocial() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", model.getEmail());
            sendobj.put("social_id", ""+model.getSocialId());
            sendobj.put("social", ""+model.getSocialType());
            sendobj.put("dob", "");
            sendobj.put("device_token", model.getDeviceToken());
            sendobj.put("auth_token", model.getAuthToken());
            String phoneNo=Functions.getValidPhoneNumber(model.getCountryCode(),
                    model.getPhoneNumber());
            sendobj.put("phone", ""+phoneNo);
            sendobj.put("username", etUsername.getText().toString());
            sendobj.put("first_name",model.getFirstName());
            sendobj.put("last_name", model.getLastName());
            sendobj.put("country_id", ""+model.getCountryId());
            sendobj.put("role", "Driver");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(),false,false);
        ApiRequest.callApi(getContext(), ApisList.registerUser, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();

                if (resp != null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){

                            methodSaveUserDetails(resp);
                        }else {
                            Functions.logDMsg( ""+respobj.getString("msg"));
                            Functions.showAlert(view.getContext(),""+view.getContext().getString(R.string.signup_status),""+respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }






    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


