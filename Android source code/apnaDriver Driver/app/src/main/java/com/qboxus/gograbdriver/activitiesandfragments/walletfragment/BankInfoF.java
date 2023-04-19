package com.qboxus.gograbdriver.activitiesandfragments.walletfragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class BankInfoF extends RootFragment implements View.OnClickListener {

    ImageView ivBack;
    LinearLayout btnSubmit;
    EditText edtName, edtBankName, edtIban, edtAccountNumber;
    View view;
    Preferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bank_info_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        callApiShowBankInfo(view.getContext());

        ivBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initControl() {
        preferences = new Preferences(view.getContext());
        ivBack = view.findViewById(R.id.iv_back);
        btnSubmit = view.findViewById(R.id.btn_submit);
        edtName = view.findViewById(R.id.et_user_name);
        edtBankName = view.findViewById(R.id.et_bank_name);
        edtIban = view.findViewById(R.id.et_iban);
        edtAccountNumber = view.findViewById(R.id.et_account_number);
    }

    private void callApiShowBankInfo(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApisList.showUserBankAccount, sendobj, resp -> {

            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);


                    if (respobj.getString("code").equals("200")) {
                        JSONObject object = respobj.getJSONObject("msg").getJSONObject("BankAccount");

                        edtName.setText(object.optString("first_name") + " " + object.optString("last_name"));
                        edtBankName.setText(object.optString("bank_name"));
                        edtIban.setText(object.optString("iban"));
                        edtAccountNumber.setText(object.optString("account_no"));

                    }
                } catch (Exception e) {
                    Functions.logDMsg("Exception " + e);
                }

            }

        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit: {
                if (TextUtils.isEmpty(edtName.getText().toString())) {
                    edtName.setError("" + getResources().getString(R.string.cant_empty));
                    edtName.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(edtBankName.getText().toString())) {
                    edtBankName.setError("" + getResources().getString(R.string.cant_empty));
                    edtBankName.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(edtIban.getText().toString())) {
                    edtIban.setError("" + getResources().getString(R.string.cant_empty));
                    edtIban.setFocusable(true);
                    return;
                }
                if (edtIban.getText().toString().length() < 10) {
                    edtIban.setError("" + getResources().getString(R.string.iban_is_too_short));
                    edtIban.setFocusable(true);
                    return;
                }
                if (edtIban.getText().toString().length() > 34) {
                    edtIban.setError("" + getResources().getString(R.string.iban_is_too_long));
                    edtIban.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(edtAccountNumber.getText().toString())) {
                    edtAccountNumber.setError("" + getResources().getString(R.string.cant_empty));
                    edtAccountNumber.setFocusable(true);
                    return;
                }

                if (!(edtIban.getText().toString().contains(edtAccountNumber.getText().toString()))) {
                    edtAccountNumber.setError("" + getResources().getString(R.string.mis_match_account_number_with_Iban));
                    edtAccountNumber.setFocusable(true);
                    return;
                }

                callApiUpdateBankInfo(view.getContext());
            }
            break;
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;

        }
    }


    private void callApiUpdateBankInfo(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            String firstName = "", lastName = "";
            String str[] = edtName.getText().toString().split(" ", 1);
            if (str.length > 1) {

                firstName = str[0];
                lastName = str[1];
            } else {
                firstName = edtName.getText().toString();
                lastName = "";
            }
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("first_name", "" + firstName);
            sendobj.put("last_name", "" + lastName);
            sendobj.put("bank_name", "" + edtBankName.getText().toString());
            sendobj.put("account_no", "" + edtAccountNumber.getText().toString());
            sendobj.put("iban", "" + edtIban.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApisList.addBankAccount, sendobj, resp -> {

            Functions.cancelLoader();

            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);

                    if (respobj.getString("code").equals("200")) {
                        Functions.showToast(view.getContext(), view.getContext().getString(R.string.change_applied));
                    } else {
                        Functions.showAlert(view.getContext(), view.getContext().getString(R.string.alert), "" + respobj.optString("msg"));
                    }

                } catch (JSONException e) {
                    Functions.logDMsg("Exception " + e);
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