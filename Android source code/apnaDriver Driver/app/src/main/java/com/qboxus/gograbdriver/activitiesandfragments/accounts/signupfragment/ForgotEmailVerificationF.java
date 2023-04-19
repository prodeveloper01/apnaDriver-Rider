package com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment;

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
import androidx.fragment.app.FragmentTransaction;

import com.chaos.view.PinView;
import com.qboxus.gograbdriver.activitiesandfragments.accounts.forgotfragment.RecoverPasswordF;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class ForgotEmailVerificationF extends Fragment implements View.OnClickListener {

    View view;
    TextView tvSubTitle, tvCodeResend;
    ImageView ivBack;
    LinearLayout btnResendCode;
    PinView etCode;
    int code = 0;
    CountDownTimer countDownTimer;


    public ForgotEmailVerificationF() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forgot_email_verification_, container, false);
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
            callApiVerifyforgotPasswordCode();
        }

    }

    private void callApiVerifyforgotPasswordCode() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", getArguments().getString("Email"));
            sendobj.put("code", etCode.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(), false, false);
        ApiRequest.callApi(getContext(), ApisList.verifyForgotPasswordcode, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp != null) {
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            showResetPasswordcreen();
                        } else {
                            etCode.setText("");
                            Functions.showToast(view.getContext(), "" + respobj.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void showResetPasswordcreen() {
        RecoverPasswordF f = new RecoverPasswordF();
        Bundle bundle = new Bundle();
        bundle.putString("Email", getArguments().getString("Email"));
        f.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, f).commit();
    }


    private void initControl() {
        tvSubTitle = view.findViewById(R.id.tv_sub_title);
        etCode = view.findViewById(R.id.et_code);
        ivBack = view.findViewById(R.id.iv_back);
        tvCodeResend = view.findViewById(R.id.tv_code_resend);
        btnResendCode = view.findViewById(R.id.btn_resend_code);
        setupScreenData();

    }

    private void setupScreenData() {
        tvSubTitle.setText(view.getContext().getString(R.string.check_your_email_we_ve) + " " + getArguments().getString("Email"));


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


    private void callApiForgotPassword() {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("email", "" + getArguments().getString("Email"));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_resend_code: {
                callApiForgotPassword();
            }
            break;
        }
    }
}