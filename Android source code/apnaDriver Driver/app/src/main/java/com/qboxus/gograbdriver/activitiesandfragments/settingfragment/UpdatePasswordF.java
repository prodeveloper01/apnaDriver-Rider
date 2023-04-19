package com.qboxus.gograbdriver.activitiesandfragments.settingfragment;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UpdatePasswordF extends RootFragment implements View.OnClickListener {


    View view;
    ImageView ivBack;
    LinearLayout btnSubmit;
    EditText etCurrentPass, etNewPass, etConfirmPass;
    Preferences preferences;
    private ImageView ivCurrentHide, ivNewHide, ivConfirmHide;
    private Boolean oldCheck = true, newCheck = true, confirmCheck = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_password_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ivCurrentHide.setOnClickListener(this);
        ivNewHide.setOnClickListener(this);
        ivConfirmHide.setOnClickListener(this);
    }

    private void initControl() {
        ivBack = view.findViewById(R.id.iv_back);
        preferences = new Preferences(view.getContext());
        etCurrentPass = view.findViewById(R.id.et_current_password);
        etNewPass = view.findViewById(R.id.et_new_password);
        etConfirmPass = view.findViewById(R.id.et_confirm_password);

//        set inputtype here for show/unshow password
        etCurrentPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        btnSubmit = view.findViewById(R.id.btn_submit);


        ivCurrentHide = view.findViewById(R.id.iv_current_password);
        ivNewHide = view.findViewById(R.id.iv_new_password);
        ivConfirmHide = view.findViewById(R.id.iv_confirm_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;

            case R.id.iv_current_password: {
                if (oldCheck) {
                    etCurrentPass.setTransformationMethod(null);
                    ivCurrentHide.setImageResource(R.drawable.ic_un_hide);
                    oldCheck = false;
                    etCurrentPass.setSelection(etCurrentPass.length());
                } else {
                    etCurrentPass.setTransformationMethod(new PasswordTransformationMethod());
                    ivCurrentHide.setImageResource(R.drawable.ic_hide);
                    oldCheck = true;
                    etCurrentPass.setSelection(etCurrentPass.length());
                }
            }
            break;

            case R.id.iv_new_password: {
                if (newCheck) {
                    etNewPass.setTransformationMethod(null);
                    ivNewHide.setImageResource(R.drawable.ic_un_hide);
                    newCheck = false;
                    etNewPass.setSelection(etNewPass.length());
                } else {
                    etNewPass.setTransformationMethod(new PasswordTransformationMethod());
                    ivNewHide.setImageResource(R.drawable.ic_hide);
                    newCheck = true;
                    etNewPass.setSelection(etNewPass.length());
                }
            }
            break;

            case R.id.iv_confirm_password: {
                if (confirmCheck) {
                    etConfirmPass.setTransformationMethod(null);
                    ivConfirmHide.setImageResource(R.drawable.ic_un_hide);
                    confirmCheck = false;
                    etConfirmPass.setSelection(etConfirmPass.length());
                } else {
                    etConfirmPass.setTransformationMethod(new PasswordTransformationMethod());
                    ivConfirmHide.setImageResource(R.drawable.ic_hide);
                    confirmCheck = true;
                    etConfirmPass.setSelection(etConfirmPass.length());
                }
            }
            break;

            case R.id.btn_submit:
                if (TextUtils.isEmpty(etCurrentPass.getText().toString())) {
                    etCurrentPass.setError(getResources().getString(R.string.cant_empty));
                    etCurrentPass.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etNewPass.getText().toString())) {
                    etNewPass.setError(getResources().getString(R.string.cant_empty));
                    etNewPass.setFocusable(true);
                    return;
                }
                if (etNewPass.getText().toString().length() < 8) {
                    etNewPass.setError(view.getContext().getString(R.string.invalid_password));
                    etNewPass.setFocusable(true);
                    return;
                }
                if (etNewPass.getText().toString().length() > 20) {
                    etNewPass.setError("" + getResources().getString(R.string.invalid_password));
                    etNewPass.setFocusable(true);
                    return;
                }
                if (PasswordSpecialValidate(etNewPass.getText().toString())) {
                    etNewPass.setError("" + getResources().getString(R.string.use_8_20_characters_from_at_least_2_categories_letters_numbers_special_characters));
                    etNewPass.setFocusable(true);
                    return;
                }
                if (etNewPass.getText().toString().equalsIgnoreCase(etCurrentPass.getText().toString())) {
                    etNewPass.setError(getResources().getString(R.string.new_password_must_differ));
                    etNewPass.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(etConfirmPass.getText().toString())) {
                    etConfirmPass.setError(getResources().getString(R.string.cant_empty));
                    etConfirmPass.setFocusable(true);
                    return;
                }
                if (etConfirmPass.getText().toString().length() < 8) {
                    etConfirmPass.setError(view.getContext().getString(R.string.invalid_password));
                    etConfirmPass.setFocusable(true);
                    return;
                }

                if (!(etConfirmPass.getText().toString().equalsIgnoreCase(etNewPass.getText().toString()))) {
                    etNewPass.setError(getResources().getString(R.string.password_must_match));
                    etNewPass.setFocusable(true);
                    return;
                }

                callApiChangePassword();
                break;
        }
    }


    private boolean PasswordSpecialValidate(String password) {
        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        boolean spStr = matcher.find();
        Pattern pattern1 = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(password);
        boolean numStr = matcher1.find();
        Pattern pattern2 = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = pattern2.matcher(password);
        boolean letStr = matcher2.find();

        Functions.logDMsg( "number " + numStr);
        Functions.logDMsg( "letter " + letStr);
        Functions.logDMsg( "special " + spStr);
        if (numStr && letStr) {
            return false;
        }
        if (numStr && spStr) {
            return false;
        }
        if (letStr && spStr) {
            return false;
        }
        return true;
    }

    private void callApiChangePassword() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("old_password", etCurrentPass.getText().toString());
            sendobj.put("new_password", etNewPass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.changePassword, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            Functions.showToast(view.getContext(), view.getContext().getString(R.string.successfully_change_password));
                            getActivity().onBackPressed();
                        } else {
                            Functions.showToast(view.getContext(), respobj.optString("msg"));
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