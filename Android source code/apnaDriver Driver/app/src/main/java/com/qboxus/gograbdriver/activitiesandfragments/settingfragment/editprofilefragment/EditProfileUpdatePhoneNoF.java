package com.qboxus.gograbdriver.activitiesandfragments.settingfragment.editprofilefragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.CityAndGenderF;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.CityAndGenderModel;
import com.qboxus.gograbdriver.models.EditProfileUpdatePhoneNoModel;
import com.qboxus.gograbdriver.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;


public class EditProfileUpdatePhoneNoF extends RootFragment implements View.OnClickListener {


    EditProfileUpdatePhoneNoModel model;
    View view;
    ImageView ivBack, ivCountryCodeArrow;
    LinearLayout btnSendCode;
    EditText etPhoneNumber;
    RelativeLayout viewCountryCode;
    TextView tvCountryCode;
    CountryCodePicker ccp;
    FragmentCallback callback;
    Preferences preferences;

    public EditProfileUpdatePhoneNoF() {
    }

    public EditProfileUpdatePhoneNoF(FragmentCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile_update_phone_no_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        viewCountryCode.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);

    }

    private void initControl() {
        preferences = new Preferences(view.getContext());
        model = new EditProfileUpdatePhoneNoModel();
        ivBack = view.findViewById(R.id.iv_back);
        ivCountryCodeArrow = view.findViewById(R.id.iv_country_code_arrow);
        viewCountryCode = view.findViewById(R.id.no_start);
        btnSendCode = view.findViewById(R.id.btn_send_code);
        tvCountryCode = view.findViewById(R.id.tv_country_code);
        etPhoneNumber = view.findViewById(R.id.et_phoneno);
        ccp = new CountryCodePicker(view.getContext());
        ccp.registerPhoneNumberTextView(etPhoneNumber);
        ccp.enableHint(false);
        setupScreenData();
    }

    private void setupScreenData() {
        String phoneNumber=preferences.getKeyUserPhone();
        try {


            if (phoneNumber.charAt(0) != '+') {
                phoneNumber = "+"+phoneNumber;
            }
            phoneNumber = phoneNumber.replace("+" + preferences.getKeyPhoneCountryCode(), "");
            phoneNumber = phoneNumber.replace("+", "");
            phoneNumber = "+" + preferences.getKeyPhoneCountryCode() + phoneNumber;

            ccp.setFullNumber(phoneNumber);
            ivCountryCodeArrow.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);

        } catch (Exception e) {
            Functions.logDMsg( "Number Exception " + e);
        }
        tvCountryCode.setText(ccp.getSelectedCountryCodeWithPlus());

        model.setCountryCode("" + ccp.getSelectedCountryCodeWithPlus());
        model.setCountryIos("" + ccp.getSelectedCountryNameCode());
        model.setCountryName("" + ccp.getSelectedCountryName());
        model.setPhoneNo(phoneNumber);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_send_code:
                Functions.hideSoftKeyboard(getActivity());
            {
                if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
                    etPhoneNumber.setError("" + getResources().getString(R.string.cant_empty));
                    etPhoneNumber.setFocusable(true);
                    return;
                }
                if (!ccp.isValid()) {
                    etPhoneNumber.setError("" + getResources().getString(R.string.invalid_phone_no));
                    etPhoneNumber.setFocusable(true);
                    return;
                }

                String phoneNo = etPhoneNumber.getText().toString();
                if (phoneNo.charAt(0) == '0') {
                    phoneNo = phoneNo.substring(1);
                }
                if (phoneNo.charAt(0) != '+') {
                    phoneNo = "+"+phoneNo;
                }
                phoneNo = phoneNo.replace(ccp.getSelectedCountryCodeWithPlus(), "");
                phoneNo = phoneNo.replace("+", "");
                phoneNo = ccp.getSelectedCountryCodeWithPlus() + phoneNo;
                phoneNo = phoneNo.replace(" ", "");
                phoneNo = phoneNo.replace("(", "");
                phoneNo = phoneNo.replace(")", "");
                phoneNo = phoneNo.replace("-", "");
                model.setPhoneNo("" + phoneNo);
                callApiPhoneNoVerification(phoneNo);
            }
            break;
            case R.id.no_start: {
                CityAndGenderF f = new CityAndGenderF(view.getContext().getString(R.string.select_country), preferences.getKeyUserCountryId(), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getBoolean("IsResponce", false)) {
                                CityAndGenderModel cityAndGender_model = (CityAndGenderModel) bundle.getSerializable("Data");

                                Functions.logDMsg( "IOS " + cityAndGender_model.getIso());
                                Functions.logDMsg( "ID " + cityAndGender_model.getId());

                                ccp.setCountryForNameCode("" + cityAndGender_model.getIso());
                                tvCountryCode.setText(ccp.getSelectedCountryNameCode() + " " + ccp.getSelectedCountryCodeWithPlus());
                                ivCountryCodeArrow.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                                model.setCountryId("" + cityAndGender_model.getId());
                                model.setCountryCode("" + ccp.getSelectedCountryCodeWithPlus());
                                model.setCountryIos("" + ccp.getSelectedCountryNameCode());
                                model.setCountryName("" + ccp.getSelectedCountryName());

                            }
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.edit_profile_phone_container, f, "CitySelection_F").addToBackStack("CitySelection_F").commit();
            }
            break;
            default:
                break;
        }
    }

    private void showVerificationScreen() {
        EditProfileVerificationCodeF f = new EditProfileVerificationCodeF(true);
        Bundle bundle = new Bundle();
        bundle.putSerializable("UserData", model);
        f.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.edit_profile_phone_container, f).commit();
    }


    private void callApiPhoneNoVerification(String phoneNo) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("phone", phoneNo);
            sendobj.put("user_id", preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.changePhoneNo, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            showVerificationScreen();
                        } else {
                            Functions.showToast(view.getContext(), "" + respobj.optString("msg"));
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
        Bundle bundle = new Bundle();
        bundle.putBoolean("IsResponce", true);
        callback.Responce(bundle);
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


