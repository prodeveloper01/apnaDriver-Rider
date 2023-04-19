package com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.chaos.view.PinView;
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


public class SignupPhoneVerificationF extends Fragment implements View.OnClickListener {


    View view;
    TextView tvSubTitle, tvCodeResend;
    ImageView ivBack;
    LinearLayout btnResendCode;
    RequestRegisterUserModel model;
    PinView etCode;
    int code = 0;
    CountDownTimer countDownTimer;
    Preferences preferences;
    boolean isSignUp;

    public SignupPhoneVerificationF() {
    }

    public SignupPhoneVerificationF(boolean isSignUp) {
        this.isSignUp = isSignUp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_phone_verification_signup, container, false);
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
                updateverificationcode(code);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void updateverificationcode(int code) {
        if (code == 4) {
            if (isSignUp) {
                model.setOtpCode(etCode.getText().toString());
                callApiVerifySignUpPhoneNO();
            } else {
                callApiVerifyLogInPhoneNO();
            }
        }

    }

    private void callApiVerifySignUpPhoneNO() {
        JSONObject sendobj = new JSONObject();

        try {

            String phoneNo=Functions.getValidPhoneNumber(model.getCountryCode(),
                    model.getPhoneNumber());

            sendobj.put("phone",  phoneNo);
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
                            showEmailScreen();
                        } else if (respobj.getString("code").contains("202")) {
                            Functions.showAlert(view.getContext(), "" + view.getContext().getString(R.string.verification_status), "Already found");

                        } else {
                            Functions.showAlert(view.getContext(), "" + view.getContext().getString(R.string.verification_status), "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void callApiVerifyLogInPhoneNO() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("phone", getArguments().getString("PhoneNo"));
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
                        Functions.logDMsg( "Response at callApiVerifyLogInPhoneNO: " + resp);
                        if (respobj.getString("code").equals("200")) {
                            callApiLoginWithPhone();
                        } else if (respobj.getString("code").equals("202")) {
                            JSONObject msgobj = respobj.getJSONObject("msg");

                            JSONObject user_obj = msgobj.getJSONObject("User");
                            JSONObject countryObj = msgobj.getJSONObject("Country");
                            JSONArray vehicleArray = msgobj.optJSONArray("Vehicle");

                            preferences.setKeyUserId(user_obj.optString("id", ""));
                            preferences.setKeyUserFirstName(user_obj.optString("first_name", ""));
                            preferences.setKeyUserLastName(user_obj.optString("last_name", ""));
                            preferences.setKeyUserName(user_obj.optString("username", ""));
                            preferences.setKeyUserEmail(user_obj.optString("email", ""));
                            preferences.setKeyUserPhone(user_obj.optString("phone", ""));
                            preferences.setKeyUserRole(user_obj.optString("role", ""));
                            preferences.setKeySocialId(user_obj.optString("social_id", ""));
                            preferences.setKeySocialType(user_obj.optString("social", ""));
                            preferences.setKeyUserToken(user_obj.optString("auth_token", ""));

                            preferences.setKeyPhoneCountryCode(countryObj.optString("phonecode", ""));
                            preferences.setKeyPhoneCountryName(countryObj.optString("native", ""));
                            preferences.setKeyPhoneCountryIOS(countryObj.optString("iso", ""));
                            preferences.setKeyPhoneCountryId(countryObj.optString("id", ""));
                            preferences.setKeyUserCountryId(countryObj.optString("id", ""));
                            preferences.setKeyUserCountry(countryObj.optString("native", ""));

                            preferences.setKeyCurrencyName(countryObj.optString("currency", ""));
                            preferences.setKeyDOB(user_obj.optString("dob", ""));
                            preferences.setKeyGender(user_obj.optString("gender", ""));
                            preferences.setKeyUserImage(user_obj.optString("image", ""));
                            preferences.setKeyUserRole(user_obj.optString("role", ""));
                            preferences.setKeyUserActive(user_obj.optString("online", ""));
                            preferences.setKeyWallet(user_obj.optString("wallet", ""));
                            preferences.setKeyUserAuthToken(user_obj.optString("token", ""));
                            preferences.setKeyIsLogin(true);
                            preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+" + preferences.getKeyPhoneCountryCode()), ""));

                            try {
                                if (vehicleArray.length() > 0) {
                                    preferences.setKeyIsVehicleSet(true);
                                    JSONObject InnerObj = vehicleArray.getJSONObject(0);
                                    preferences.setKeyVehicleId(InnerObj.optString("ride_type_id"));
                                }
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception" + e);
                            }

                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                        } else {
                            Functions.showAlert(view.getContext(), "" + view.getContext().getString(R.string.verification_status), "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void callApiLoginWithPhone() {


        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("role", "driver");
            sendobj.put("phone", "" + getArguments().getString("PhoneNo"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequest.callApi(getContext(), ApisList.registerUser, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        if (respobj.getString("code").equals("200")) {

                            JSONObject msgobj = respobj.getJSONObject("msg");

                            JSONObject userObj = msgobj.getJSONObject("User");
                            JSONObject countryObj = msgobj.getJSONObject("Country");
                            JSONArray vehicleArray = msgobj.optJSONArray("Vehicle");

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
                            preferences.setKeyIsLogin(true);
                            preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+" + preferences.getKeyPhoneCountryCode()), ""));

                            try {
                                if (vehicleArray.length() > 0) {
                                    preferences.setKeyIsVehicleSet(true);
                                    JSONObject InnerObj = vehicleArray.getJSONObject(0);
                                    preferences.setKeyVehicleId(InnerObj.optString("ride_type_id"));
                                }
                            } catch (Exception e) {
                                Functions.logDMsg( "Exception" + e);
                            }

                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            etCode.setText("");
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.login_status), "" + respobj.optString("msg"));
                        }


                    } catch (Exception e) {
                        Functions.logDMsg( "Exception " + e);
                    }
                }
            }
        });
    }


    private void showEmailScreen() {
//        incase from social login email is not given by facebook i show emailscreen
        if (TextUtils.isEmpty(model.getEmail())) {
            etCode.setText("");
            Functions.RemoveSingleFragment(getActivity().getSupportFragmentManager(), getTag());
            SignupEmailF f = new SignupEmailF();
            Bundle bundle = new Bundle();
            bundle.putSerializable("UserData", model);
            f.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            ft.replace(R.id.account_container, f, "SignupEmail_F").addToBackStack("SignupEmail_F").commit();
        } else {
            etCode.setText("");
            Functions.RemoveSingleFragment(getActivity().getSupportFragmentManager(), getTag());
            SignupFullNameF f = new SignupFullNameF();
            Bundle bundle = new Bundle();
            bundle.putSerializable("UserData", model);
            f.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            ft.replace(R.id.account_container, f, "SignupFullName_F").addToBackStack("SignupFullName_F").commit();
        }


    }

    private void initControl() {
        if (isSignUp) {
            model = (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        }
        preferences = new Preferences(view.getContext());
        tvSubTitle = view.findViewById(R.id.tv_sub_title);
        etCode = view.findViewById(R.id.et_code);
        ivBack = view.findViewById(R.id.iv_back);
        tvCodeResend = view.findViewById(R.id.tv_code_resend);
        btnResendCode = view.findViewById(R.id.btn_resend_code);
        setupScreenData();

    }

    private void setupScreenData() {
        if (isSignUp) {
            tvSubTitle.setText(view.getContext().getString(R.string.check_your_sms_message_we_ve) + " " + model.getPhoneNumber());
        } else {
            tvSubTitle.setText(view.getContext().getString(R.string.check_your_sms_message_we_ve) + " " + getArguments().getString("PhoneNo"));
        }
        ResetAndStartTime();
    }

    public void ResetAndStartTime() {
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


    private void callApiPhoneNoVerification() {
        JSONObject sendobj = new JSONObject();
        try {
            if (isSignUp) {
                String phoneNo=Functions.getValidPhoneNumber(model.getCountryCode(),
                        model.getPhoneNumber());
                sendobj.put("phone", phoneNo);
            } else {
                sendobj.put("phone", getArguments().getString("PhoneNo"));
            }
            sendobj.put("verify", "0");
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
                            ResetAndStartTime();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_resend_code: {

                callApiPhoneNoVerification();
            }
            break;
        }
    }
}