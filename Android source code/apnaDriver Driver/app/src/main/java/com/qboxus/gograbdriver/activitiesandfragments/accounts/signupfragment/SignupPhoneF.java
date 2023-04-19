package com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.CityAndGenderF;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.models.CityAndGenderModel;
import com.qboxus.gograbdriver.models.RequestRegisterUserModel;
import com.qboxus.gograbdriver.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SignupPhoneF extends Fragment implements View.OnClickListener {


    RequestRegisterUserModel model;
    View view;
    ImageView ivBack, ivCountryCodeArrow;
    LinearLayout btnSendCode;
    EditText etPhoneNumber;
    TextView tvCountryCode;
    RelativeLayout tabCountrySelect;
    CountryCodePicker ccp;
    boolean isSocial;

    public SignupPhoneF() {
    }

    public SignupPhoneF(boolean isSocial) {
        this.isSocial = isSocial;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_sign_up_phone, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        tabCountrySelect.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);

    }

    private void initControl() {
        if (isSocial)
        {
            model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        }
        else
        {
            model=new RequestRegisterUserModel();
            model.setSocialType("email");
        }
        ivBack =view.findViewById(R.id.iv_back);
        ivCountryCodeArrow =view.findViewById(R.id.iv_country_code_arrow);
        tabCountrySelect =view.findViewById(R.id.no_start);
        btnSendCode =view.findViewById(R.id.btn_send_code);
        tvCountryCode =view.findViewById(R.id.tv_country_code);
        etPhoneNumber =view.findViewById(R.id.et_phoneno);
        ccp=new CountryCodePicker(view.getContext());
        ccp.registerPhoneNumberTextView(etPhoneNumber);


        setupScreenData();
    }

    private void setupScreenData() {
        tvCountryCode.setHint(ccp.getSelectedCountryNameCode()+" "+ccp.getSelectedCountryCodeWithPlus());
        model.setCountryCode(""+ccp.getSelectedCountryCodeWithPlus());
        model.setCountryIos(""+ccp.getSelectedCountryNameCode());
        model.setCountryName(""+ccp.getSelectedCountryName());
        callApiShowCountries();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
            {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_send_code:
                Functions.hideSoftKeyboard(getActivity());
            {
                if(TextUtils.isEmpty(etPhoneNumber.getText().toString()))
                {
                    etPhoneNumber.setError(""+getResources().getString(R.string.cant_empty));
                    etPhoneNumber.setFocusable(true);
                    return;
                }
                if (model.getCountryId()==null)
                {
                    etPhoneNumber.setError(""+getResources().getString(R.string.select_country));
                    etPhoneNumber.setFocusable(true);
                    return;
                }
                if(!ccp.isValid())
                {
                    etPhoneNumber.setError(""+getResources().getString(R.string.invalid_phone_no));
                    etPhoneNumber.setFocusable(true);
                    return;
                }

                String phoneNo=Functions.getValidPhoneNumber(ccp.getSelectedCountryCodeWithPlus(),
                        ""+etPhoneNumber.getText().toString());
                model.setPhoneNumber(""+phoneNo);

                callApiPhoneNoVerification();
            }
            break;
            case R.id.no_start:
            {
                CityAndGenderF f = new CityAndGenderF(view.getContext().getString(R.string.select_country),model.getCountryId(), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle!=null)
                        {
                            if (bundle.getBoolean("IsResponce",false))
                            {
                                CityAndGenderModel cityAndGenderModel= (CityAndGenderModel) bundle.getSerializable("Data");
                                ccp.setCountryForNameCode(""+cityAndGenderModel.getIso());

                                Functions.logDMsg("country short Code:"+cityAndGenderModel.getIso());

                                tvCountryCode.setText(ccp.getSelectedCountryNameCode()+" "+ccp.getSelectedCountryCodeWithPlus());
                                ivCountryCodeArrow.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                                model.setCountryId(""+cityAndGenderModel.getId());
                                model.setCountryCode(""+ccp.getSelectedCountryCodeWithPlus());
                                model.setCountryIos(""+ccp.getSelectedCountryNameCode());
                                model.setCountryName(""+ccp.getSelectedCountryName());
                            }
                        }
                    }
                });
                FragmentTransaction ft =getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.signup_container, f,"CitySelection_F").addToBackStack("CitySelection_F").commit();
            }
            break;
            default:
                break;
        }
    }


    private void callApiShowCountries() {
        ApiRequest.callApi(getContext(), ApisList.showCountries, new JSONObject(), new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            JSONArray msgarray = respobj.getJSONArray("msg");

                            methodGettingCountriesList(msgarray);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void methodGettingCountriesList(JSONArray msgarray) {
        try {

            for (int i = 0; i<msgarray.length(); i++){
                JSONObject countriesobj = msgarray.getJSONObject(i).getJSONObject("Country");

                CityAndGenderModel model= new CityAndGenderModel();
                model.setId(countriesobj.optString("id"));
                model.setName(countriesobj.optString("name"));
                model.setPhonecode(countriesobj.optString("phonecode"));
                model.setIso(countriesobj.optString("iso"));
                model.setSelected(false);

                if(model.getIso().equalsIgnoreCase(ccp.getSelectedCountryNameCode()))
                {
                    ccp.setCountryForNameCode(""+model.getIso());
                    tvCountryCode.setText(ccp.getSelectedCountryNameCode()+" "+ccp.getSelectedCountryCodeWithPlus());
                    ivCountryCodeArrow.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                    this.model.setCountryId(""+model.getId());
                    this.model.setCountryCode(""+ccp.getSelectedCountryCodeWithPlus());
                    this.model.setCountryIos(""+ccp.getSelectedCountryNameCode());
                    this.model.setCountryName(""+ccp.getSelectedCountryName());

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showVerificationScreen() {
        SignupPhoneVerificationF signupPhoneVerificationF = new SignupPhoneVerificationF(true);
        Bundle bundle=new Bundle();
        bundle.putSerializable("UserData",model);
        signupPhoneVerificationF.setArguments(bundle);
        FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, signupPhoneVerificationF,"SignuphoneVerification_F").addToBackStack("SignuphoneVerification_F").commit();
    }

    private void callApiPhoneNoVerification() {
        JSONObject sendobj = new JSONObject();
        String phoneNo=Functions.getValidPhoneNumber(model.getCountryCode(),
                model.getPhoneNumber());
        try {
            sendobj.put("phone", phoneNo);
            sendobj.put("verify", "0");
            sendobj.put("role","driver");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(),false,false);
        ApiRequest.callApi(getContext(), ApisList.verifyRegisterphoneAuthcode, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            showVerificationScreen();
                        }else {
                            Functions.showToast(view.getContext(), ""+respobj.optString("msg"));
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


