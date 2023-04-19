package com.qboxus.gograbdriver.activitiesandfragments.accounts.forgotfragment;

import android.content.Intent;
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

import androidx.fragment.app.Fragment;

import com.qboxus.gograbdriver.activitiesandfragments.accounts.SignInA;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecoverPasswordF extends Fragment implements View.OnClickListener {

    View view;
    ImageView ivBack;
    LinearLayout btnResetPassword;
    EditText etNewPass, etConfirmPass;
    private ImageView ivNewHide, ivConfirmHide;
    private Boolean newCheck = true, confirmCheck = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recover_password_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        ivNewHide.setOnClickListener(this);
        ivConfirmHide.setOnClickListener(this);
    }

    private void initControl() {
        ivBack = view.findViewById(R.id.iv_back);
        etNewPass = view.findViewById(R.id.et_new_password);
        etConfirmPass = view.findViewById(R.id.et_confirm_password);

        etNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnResetPassword = view.findViewById(R.id.btn_reset_password);

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
            case R.id.btn_reset_password:
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
                if (TextUtils.isEmpty(etConfirmPass.getText().toString())) {
                    etConfirmPass.setError(getResources().getString(R.string.cant_empty));
                    etConfirmPass.setFocusable(true);
                    return;
                }
                if (!(etConfirmPass.getText().toString().equalsIgnoreCase(etNewPass.getText().toString()))) {
                    etNewPass.setError(getResources().getString(R.string.password_must_match));
                    etNewPass.setFocusable(true);
                    return;
                }

                callApiChangeForgotPassword();
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


    private void callApiChangeForgotPassword() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", getArguments().getString("Email"));
            sendobj.put("password", etConfirmPass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.changePasswordForgot, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            Intent intent = new Intent(view.getContext(), SignInA.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Functions.clearFragment(getActivity().getSupportFragmentManager());

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

    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}