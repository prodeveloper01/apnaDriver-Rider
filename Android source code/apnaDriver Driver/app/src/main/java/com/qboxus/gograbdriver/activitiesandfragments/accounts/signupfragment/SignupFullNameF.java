package com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.messaging.FirebaseMessaging;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.RequestRegisterUserModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class SignupFullNameF extends Fragment implements View.OnClickListener{

    RequestRegisterUserModel model;
    View view;
    ImageView ivBack;
    LinearLayout btnNext;
    EditText etFirstname,etLastname;
    Preferences preferences;
    String token;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_signup_full_name_, container, false);
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
        btnNext.setOnClickListener(this);
    }

    private void initControl() {
        preferences=new Preferences(view.getContext());
        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        ivBack =view.findViewById(R.id.iv_back);
        btnNext =view.findViewById(R.id.btn_next);
        etFirstname =view.findViewById(R.id.et_firstname);
        etLastname =view.findViewById(R.id.et_lastname);


        setupScreenData();
    }

    private void setupScreenData() {
        if (model.getFirstName()!=null && TextUtils.isEmpty(model.getFirstName()))
        {
            etFirstname.setText(""+model.getFirstName());
        }
        if (model.getLastName()!=null && TextUtils.isEmpty(model.getLastName()))
        {
            etLastname.setText(""+model.getLastName());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
            {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_next:
                Functions.hideSoftKeyboard(getActivity());
            {
                if(TextUtils.isEmpty(etFirstname.getText().toString()))
                {
                    etFirstname.setError(""+getResources().getString(R.string.cant_empty));
                    etFirstname.setFocusable(true);
                    return;
                }
                if(TextUtils.isEmpty(etLastname.getText().toString()))
                {
                    etLastname.setError(""+getResources().getString(R.string.cant_empty));
                    etLastname.setFocusable(true);
                    return;
                }
                model.setFirstName(""+ etFirstname.getText().toString());
                model.setLastName(""+ etLastname.getText().toString());
                moveToNext();


            }
            break;

            default:
                break;
        }
    }

    private void moveToNext() {
        SignupUserNameF signupUserNameF = new SignupUserNameF();
        Bundle bundle=new Bundle();
        bundle.putSerializable("UserData",model);
        signupUserNameF.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, signupUserNameF,"SignupUserName_F").addToBackStack("SignupUserName_F").commit();
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


    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


