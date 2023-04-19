package com.qboxus.gograbdriver.activitiesandfragments.settingfragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.editprofilefragment.EditProfileUpdateEmailF;
import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.editprofilefragment.EditProfileUpdatePhoneNoF;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class EditProfileF extends RootFragment implements View.OnClickListener {

    View view;
    Preferences preferences;
    View llGender, llEmail, llPhone;
    LinearLayout btnSubmit;
    ImageView ivBack;
    EditText etName,etFirstName,etLastName, etMobileNumber, etEmail, etGender;

    public EditProfileF() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_details, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        btnSubmit.setOnClickListener(this);
        llGender.setOnClickListener(this);
        llEmail.setOnClickListener(this);
        llPhone.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void initControl() {
        preferences = new Preferences(view.getContext());
        ivBack = view.findViewById(R.id.iv_back);
        llGender = view.findViewById(R.id.ll_gender);
        llEmail = view.findViewById(R.id.ll_email);
        llPhone = view.findViewById(R.id.ll_phone);
        btnSubmit = view.findViewById(R.id.btn_submit);
        etName = view.findViewById(R.id.et_user_name);
        etFirstName = view.findViewById(R.id.et_first_name);
        etLastName = view.findViewById(R.id.et_last_name);
        etMobileNumber = view.findViewById(R.id.et_mobile_number);
        etEmail = view.findViewById(R.id.et_email);
        etGender = view.findViewById(R.id.et_gender);

        setUpScreenData();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_gender: {
                CityAndGenderF f = new CityAndGenderF(view.getContext().getString(R.string.select_gender), preferences.getKeyGender(), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getBoolean("IsResponce", false)) {
                                etGender.setText("" + bundle.getString("Data"));
                            }
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.edit_profile_container, f, "GenderSelection_F").addToBackStack("GenderSelection_F").commit();
            }
            break;
            case R.id.ll_phone: {
                EditProfileUpdatePhoneNoF f = new EditProfileUpdatePhoneNoF(new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getBoolean("IsResponce", false)) {
                                etMobileNumber.setText("+" + preferences.getKeyPhoneCountryCode() + preferences.getKeyUserPhone());
                            }
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.edit_profile_container, f, "EditProfileUpdatePhoneNo_F").addToBackStack("EditProfileUpdatePhoneNo_F").commit();
            }
            break;
            case R.id.ll_email: {
                EditProfileUpdateEmailF f = new EditProfileUpdateEmailF(new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getBoolean("IsResponce", false)) {
                                etEmail.setText("" + preferences.getKeyUserEmail());
                            }
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.edit_profile_container, f, "EditProfileUpdateEmail_F").addToBackStack("EditProfileUpdateEmail_F").commit();
            }
            break;
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_submit: {
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    etName.setError("" + getResources().getString(R.string.cant_empty));
                    etName.setFocusable(true);
                    return;
                }
                if (etName.getText().toString().length() < 4 && etName.getText().toString().length() > 14) {
                    etName.setError("" + getResources().getString(R.string.username_length_must_be));
                    etName.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etFirstName.getText().toString())) {
                    etFirstName.setError("" + getResources().getString(R.string.cant_empty));
                    etFirstName.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etLastName.getText().toString())) {
                    etLastName.setError("" + getResources().getString(R.string.cant_empty));
                    etLastName.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etGender.getText().toString())) {
                    etGender.setError("" + getResources().getString(R.string.cant_empty));
                    etGender.setFocusable(true);
                    return;
                }

                callUpdateUserNameApi();
            }
            break;
        }
    }

    private void callUpdateUserNameApi() {
        JSONObject params = new JSONObject();

        try {
            params.put("user_id", preferences.getKeyUserId());
            params.put("first_name", etFirstName.getText().toString());
            params.put("last_name", etLastName.getText().toString());
            params.put("username", etName.getText().toString());
            params.put("gender", etGender.getText().toString());
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

                }

            } catch (Exception e) {
                Functions.logDMsg( "Exception " + e);
            }
        }
    }


    private void setUpScreenData() {
        etName.setText(preferences.getKeyUserName());
        etFirstName.setText(preferences.getKeyUserFirstName());
        etLastName.setText(preferences.getKeyUserLastName());
        etEmail.setText(preferences.getKeyUserEmail());
        etGender.setText(preferences.getKeyGender());

        etMobileNumber.setText("+" + preferences.getKeyPhoneCountryCode() + preferences.getKeyUserPhone());
    }


    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}
