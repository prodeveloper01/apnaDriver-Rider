package com.qboxus.gograbdriver.activitiesandfragments.accounts.signInfragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qboxus.gograbdriver.activitiesandfragments.accounts.forgotfragment.ForgotPasswordF;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class SigninEmailF extends Fragment implements View.OnClickListener {

    View view;
    LinearLayout btnSendCode;
    TextView tvForgotPassword;
    EditText etEmail, etPassword;
    Preferences preferences;
    private ImageView ivPassword;
    private Boolean check = true;


    public static SigninEmailF newInstance() {
        SigninEmailF fragment = new SigninEmailF();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin_email_, container, false);
        initcontrol();
        actionControl();
        return view;
    }

    private void actionControl() {
        btnSendCode.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        ivPassword.setOnClickListener(this);
    }

    private void initcontrol() {
        preferences = new Preferences(view.getContext());
        btnSendCode = view.findViewById(R.id.btn_send_code);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        tvForgotPassword = view.findViewById(R.id.tv_forgot_password);
        ivPassword = view.findViewById(R.id.iv_password);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_code:
                Functions.hideSoftKeyboard(getActivity());
            {
                if (TextUtils.isEmpty(etEmail.getText().toString())) {
                    etEmail.setError("" + getResources().getString(R.string.cant_empty));
                    etEmail.setFocusable(true);
                    return;
                }
                if (!(Functions.isValidEmail(etEmail.getText().toString()))) {
                    etEmail.setError("" + getResources().getString(R.string.invalid_email));
                    etEmail.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString())) {
                    etPassword.setError("" + getResources().getString(R.string.cant_empty));
                    etPassword.setFocusable(true);
                    return;
                }
                callApiLoginWithEmail();
            }
            break;
            case R.id.tv_forgot_password: {
                ForgotPasswordF f = new ForgotPasswordF();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.account_container, f, "ForgotPassword_F").addToBackStack("ForgotPassword_F").commit();
            }
            break;
            case R.id.iv_password: {
                if (check) {
                    etPassword.setTransformationMethod(null);
                    ivPassword.setImageResource(R.drawable.ic_un_hide);
                    check = false;
                    etPassword.setSelection(etPassword.length());
                } else {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ivPassword.setImageResource(R.drawable.ic_hide);
                    check = true;
                    etPassword.setSelection(etPassword.length());
                }
            }
            break;
            default:
                break;
        }
    }


    private void callApiLoginWithEmail() {


        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", "" + etEmail.getText().toString());
            sendobj.put("password", "" + etPassword.getText().toString());
            sendobj.put("role", "driver");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.login, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
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

                            preferences.setKeyUserPhone(preferences.getKeyUserPhone().replace(("+" + preferences.getKeyPhoneCountryCode()), ""));
                            preferences.setKeyIsLogin(true);

                            try {
                                if (vehicleArray.length() > 0) {
                                    preferences.setKeyIsVehicleSet(true);
                                    JSONObject InnerObj = vehicleArray.getJSONObject(0);
                                    preferences.setKeyVehicleId(InnerObj.optString("ride_type_id"));
                                }
                            } catch (Exception e) {
                                Functions.logDMsg("Exception" + e);
                            }

                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Functions.showAlert(view.getContext(), view.getContext().getString(R.string.login_status), "" + respobj.optString("msg"));
                        }


                    } catch (Exception e) {
                        Functions.logDMsg("Exception " + e);
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


