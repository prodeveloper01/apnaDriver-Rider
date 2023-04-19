package com.qboxus.gograbdriver.activitiesandfragments.settingfragment.editprofilefragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.chaos.view.PinView;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.EditProfileUpdatePhoneNoModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class EditProfileVerificationCodeF extends Fragment implements View.OnClickListener {

    View view;
    TextView tvSubTitle, tvCodeResend;
    ImageView ivBack;
    LinearLayout btnResendCode;
    EditProfileUpdatePhoneNoModel model;
    PinView etCode;
    int code = 0;
    CountDownTimer countDownTimer;
    Preferences preferences;

    boolean isPhoneNo;

    public EditProfileVerificationCodeF() {
    }

    public EditProfileVerificationCodeF(boolean isPhoneNo) {
        this.isPhoneNo = isPhoneNo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_profile_verification_code_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnResendCode.setOnClickListener(this);


        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strTxt = charSequence.toString();
                if (strTxt == null) {
                    code = 0;
                } else {
                    code = strTxt.length();
                }
                updateVerificationCode(code);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void updateVerificationCode(int code) {
        if (code == 4) {
            if (isPhoneNo) {
                model.setOtpCode(etCode.getText().toString());
                callApi();
            } else {
                model.setOtpCode(etCode.getText().toString());
                callApiEmail();
            }
        }

    }

    private void callApiEmail() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("new_email", model.getEmail());
            sendobj.put("code", etCode.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.verifyChangeEmailCode, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        if (respobj.getString("code").equals("200")) {
                            Functions.showToast(view.getContext(), view.getContext().getString(R.string.successfully_update_username));
                            methodSaveUserDetails(resp);
                        } else {
                            etCode.setText("");
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.edit_profile_status), "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void callUpdateUserPhoneApi() {
        JSONObject params = new JSONObject();
        String phoneNo=Functions.getValidPhoneNumber(model.getCountryCode(),
                model.getPhoneNo());
        try {
            params.put("user_id", preferences.getKeyUserId());
            params.put("phone", phoneNo);
            params.put("country_id", model.getCountryId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(view.getContext(), false, false);
        ApiRequest.callApi(view.getContext(), ApisList.editProfile, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                try {
                    if (resp != null) {
                        JSONObject respobj = new JSONObject(resp);
                        String code = respobj.optString("code");
                        if (code.equals("200")) {
                            Functions.showToast(view.getContext(), view.getContext().getString(R.string.successfully_update_username));
                            methodSaveUserDetails(resp);
                        } else {
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.edit_profile_status), "" + respobj.getString("msg"));
                        }
                    }
                } catch (Exception e) {
                    Functions.logDMsg( "Error " + e);
                }
            }
        });
    }

    private void methodSaveUserDetails(String resp) {

        if (resp != null) {
            try {
                JSONObject respobj = new JSONObject(resp);
                if (respobj.getString("code").equals("200")) {

                    JSONObject msgobj = respobj.getJSONObject("msg");

                    JSONObject userObj = msgobj.getJSONObject("User");
                    JSONObject countryObj = msgobj.getJSONObject("Country");

                    preferences.setKeyUserId(userObj.optString("id", ""));
                    preferences.setKeyUserFirstName(userObj.optString("first_name", ""));
                    preferences.setKeyUserLastName(userObj.optString("last_name", ""));
                    preferences.setKeyUserName(userObj.optString("username", ""));
                    preferences.setKeyUserEmail(userObj.optString("email", ""));
                    preferences.setKeyUserPhone(userObj.optString("phone", ""));
                    preferences.setKeyUserRole(userObj.optString("role", ""));
                    preferences.setKeySocialId(userObj.optString("social_id", ""));
                    preferences.setKeySocialType(userObj.optString("social", ""));
                    preferences.setKeyUserToken(userObj.optString("auth_token", ""));

                    preferences.setKeyPhoneCountryCode(countryObj.optString("phonecode", ""));
                    preferences.setKeyPhoneCountryName(countryObj.optString("native", ""));
                    preferences.setKeyPhoneCountryIOS(countryObj.optString("iso", ""));
                    preferences.setKeyPhoneCountryId(countryObj.optString("id", ""));
                    preferences.setKeyUserCountryId(countryObj.optString("id", ""));
                    preferences.setKeyUserCountry(countryObj.optString("native", ""));

                    preferences.setKeyCurrencyName(countryObj.optString("currency", ""));
                    preferences.setKeyDOB(userObj.optString("dob", ""));
                    preferences.setKeyGender(userObj.optString("gender", ""));
                    preferences.setKeyUserImage(userObj.optString("image", ""));
                    preferences.setKeyUserRole(userObj.optString("role", ""));
                    preferences.setKeyUserActive(userObj.optString("online", ""));
                    preferences.setKeyWallet(userObj.optString("wallet", ""));
                    preferences.setKeyUserAuthToken(userObj.optString("token", ""));
                    preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+" + preferences.getKeyPhoneCountryCode()), ""));

                    getActivity().onBackPressed();
                }

            } catch (Exception e) {
                Functions.logDMsg( "Exception " + e);
            }
        }
    }


    private void callApi() {
        JSONObject sendobj = new JSONObject();

        try {

            String phoneNo=Functions.getValidPhoneNumber(model.getCountryCode(),
                    model.getPhoneNo());

            sendobj.put("phone", phoneNo);
            sendobj.put("verify", "1");
            sendobj.put("code", "" + etCode.getText().toString());
            sendobj.put("role", "driver");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.verifyRegisterphoneAuthcode, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            Functions.showToast(view.getContext(), view.getContext().getString(R.string.successfully_update_username));
                            callUpdateUserPhoneApi();
                        } else {
                            etCode.setText("");
                            Functions.showAlert(view.getContext(), "" + view.getContext().getString(R.string.verification_status), "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void initControl() {
        model = (EditProfileUpdatePhoneNoModel) getArguments().getSerializable("UserData");
        preferences = new Preferences(view.getContext());
        tvSubTitle = view.findViewById(R.id.tv_sub_title);
        etCode = view.findViewById(R.id.et_code);
        ivBack = view.findViewById(R.id.iv_back);
        tvCodeResend = view.findViewById(R.id.tv_code_resend);
        btnResendCode = view.findViewById(R.id.btn_resend_code);
        setupScreenData();

    }

    private void setupScreenData() {
        if (isPhoneNo) {
            tvSubTitle.setText(view.getContext().getString(R.string.check_your_sms_message_we_ve) + " " + model.getPhoneNo());
        } else {
            tvSubTitle.setText(view.getContext().getString(R.string.please_confirm_your_email_your_email) + " " + model.getEmail());
        }
        resetAndStartTime();
    }

    public void resetAndStartTime() {
        tvCodeResend.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                tvCodeResend.setText(view.getContext().getString(R.string.resend_sode_time) + " " + (l / 1000) + " s");
            }

            @Override
            public void onFinish() {
                btnResendCode.setVisibility(View.VISIBLE);
            }

        }.start();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_resend_code: {
                if (isPhoneNo) {
                    callApiPhoneNoVerification();
                } else {
                    callApiUpdateEmail();
                }
            }
            break;
        }
    }


    private void callApiUpdateEmail() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("email", model.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.changeEmailAddress, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            resetAndStartTime();
                            btnResendCode.setVisibility(View.GONE);

                        } else {
                            Functions.showToast(view.getContext(), "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void callApiPhoneNoVerification() {
        JSONObject sendobj = new JSONObject();


        String phoneNumber=Functions.getValidPhoneNumber(model.getCountryCode(),model.getPhoneNo());
        try {
            sendobj.put("phone", phoneNumber);
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
                            resetAndStartTime();
                            btnResendCode.setVisibility(View.GONE);
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
}