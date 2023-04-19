package com.qboxus.gograbdriver.activitiesandfragments.accounts.forgotfragment;

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

import com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment.ForgotEmailVerificationF;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotPasswordF extends Fragment implements View.OnClickListener {

    View view;
    ImageView ivBack;
    LinearLayout btnResetPassword;
    EditText etEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forgot_password_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
    }

    private void initControl() {
        ivBack = view.findViewById(R.id.iv_back);
        btnResetPassword = view.findViewById(R.id.btn_reset_password);
        etEmail = view.findViewById(R.id.et_email);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_reset_password:
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
                callApiForgotPassword();
            }
            break;

            default:
                break;
        }
    }

    private void showVerificationScreen() {
        ForgotEmailVerificationF f = new ForgotEmailVerificationF();
        Bundle bundle = new Bundle();
        bundle.putString("Email", etEmail.getText().toString());
        f.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, f, "SignupEmailVerification_F").addToBackStack("SignupEmailVerification_F").commit();
    }

    private void callApiForgotPassword() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", etEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.forgotPassword, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            showVerificationScreen();

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


