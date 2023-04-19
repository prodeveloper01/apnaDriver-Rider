package com.qboxus.gograbdriver.activitiesandfragments.rideandrequest;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.RideModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class RideCompletePaymentF extends Fragment implements View.OnClickListener {

    FragmentCallback callback;
    View view;
    ImageView ivBack;
    LinearLayout btnPay;
    TextView tvName, tvFare, tvWalletValue, tvFinalFare, tvFinalFare2;
    RideModel rideModel;
    Preferences preferences;
    EditText signedText;
    String stwalletAmount;
    String finalWalletAmount;
    String walletType, walletDebit;
    double walletAmount;
    double finalAmount;

    double userWallet, fianlFare;

    public RideCompletePaymentF(FragmentCallback callback) {
        this.callback = callback;
    }

    public RideCompletePaymentF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ride_complete_payment_, container, false);
        initcontrol();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnPay.setOnClickListener(this);
    }

    private void initcontrol() {
        preferences = new Preferences(view.getContext());
        rideModel = (RideModel) getArguments().getSerializable("user_data");
        ivBack = view.findViewById(R.id.iv_back);
        btnPay = view.findViewById(R.id.btn_pay);
        tvName = view.findViewById(R.id.tv_name);
        tvFinalFare = view.findViewById(R.id.tv_final_fare);
        tvFinalFare2 =view.findViewById(R.id.tv_final_fare2);
        tvWalletValue = view.findViewById(R.id.tv_wallet_value);
        tvFare = view.findViewById(R.id.tv_fare);
        signedText = view.findViewById(R.id.signed_text);
        callApiForUserDetail(getActivity());

    }



    private void setUpScreenData() {
        tvName.setText("from " + rideModel.getUserModel().getUsername());
        tvFare.setText(preferences.getKeyCurrencySymbol() + rideModel.getRideRequestModel().getFinalFare());

        fianlFare = Double.parseDouble(rideModel.getRideRequestModel().getFinalFare());


        if (stwalletAmount.contains("-")) {
            walletType = "negative";
            stwalletAmount = stwalletAmount.substring(1);
            finalAmount = Double.parseDouble(stwalletAmount) + Double.parseDouble(rideModel.getRideRequestModel().getFinalFare());

            tvWalletValue.setText(preferences.getKeyCurrencySymbol() + "0");

        } else {
            walletType = "positive";
            walletDebit = "1";
            finalWalletAmount = String.valueOf(stwalletAmount);

            double walletDouble = Double.parseDouble(stwalletAmount);
            double Fare = Double.parseDouble(rideModel.getRideRequestModel().getFinalFare());
            if (walletDouble >= Fare) {
                finalAmount = 0.0;
                finalWalletAmount = rideModel.getRideRequestModel().getFinalFare();

            }
            else {
                finalAmount = Double.parseDouble(rideModel.getRideRequestModel().getFinalFare()) - Double.parseDouble(stwalletAmount);
                finalWalletAmount = stwalletAmount;
                if (finalAmount < 0) {
                    finalAmount = finalAmount * -1;
                }
            }

            tvWalletValue.setText(preferences.getKeyCurrencySymbol() + finalWalletAmount);

        }
        tvFinalFare.setText(preferences.getKeyCurrencySymbol() +  Functions.roundoffDecimal(finalAmount));
        tvFinalFare2.setText(preferences.getKeyCurrencySymbol() +  Functions.roundoffDecimal(finalAmount));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_pay: {
                double amountCollectecd;
                if (signedText.getText().toString() != null && !signedText.getText().toString().equals("")) {
                    amountCollectecd = Double.parseDouble(signedText.getText().toString());
                } else {
                    amountCollectecd = 0;

                }

                if (walletAmount > fianlFare) {
                    stwalletAmount = String.valueOf(fianlFare);
                    userWallet = Functions.roundoffDecimal(walletAmount - fianlFare);
                } else if (walletAmount < fianlFare) {
                    stwalletAmount = String.valueOf(walletAmount);
                    userWallet = 0;
                } else if (walletAmount == 0) {
                    stwalletAmount = "0";
                    userWallet = 0;
                }


                double finalValue = Functions.roundoffDecimal(amountCollectecd - finalAmount);

                if (finalValue == 0) {
                    finalWalletAmount = "0";
                    walletDebit = "0";
                } else if (finalValue < 0) {
                    Toast.makeText(getActivity(), "you have entered less amount", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    walletDebit = "2";
                    walletType = "negative";
                    finalWalletAmount = String.valueOf(finalValue);
                    userWallet = Double.parseDouble(finalWalletAmount)+ userWallet;
                }

                changeStatus();
            }
            break;
        }
    }

    public void changeStatus() {
        JSONObject params = new JSONObject();
        try {
            params.put("request_id", "" + rideModel.getRideRequestModel().getId());
            params.put("status", "collect_payment");
            if (signedText.getText().toString().equals("")) {
                params.put("collected_amount", "0");
            } else {
                params.put("collected_amount", "" + signedText.getText().toString());
            }

            params.put("wallet_amount", "" + stwalletAmount);

            params.put("debit_credit_amount", "" + finalWalletAmount);

            params.put("wallet_type", "" + walletType);

            params.put("wallet_debit", "" + walletDebit);

            params.put("user_wallet", "" + Functions.roundoffDecimal(userWallet));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Functions.showLoader(getContext(),false,false);
        ApiRequest.callApi(view.getContext(), ApisList.startTrip, params, new CallbackResponce() {
            @Override
            public void responce(String resp) {

                Functions.cancelLoader();

                if (params.optString("status").equals("collect_payment")) {

                    RideCompleteFeedbackF f = new RideCompleteFeedbackF(new FragmentCallback() {
                        @Override
                        public void Responce(Bundle bundle) {

                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user_data", rideModel);
                    f.setArguments(bundle);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top);
                    ft.replace(R.id.ride_status_container, f, "RideCompleteFeedback_F").addToBackStack("RideCompleteFeedback_F").commit();
                }

            }
        });
    }


    private void callApiForUserDetail(Context context) {

        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", rideModel.getUserModel().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.showLoader(context, false, false);
        ApiRequest.callApi(context, ApisList.showUserDetails, sendobj, resp -> {

            Functions.cancelLoader();
            if (resp != null) {

                try {
                    JSONObject respobj = new JSONObject(resp);


                    if (respobj.getString("code").equals("200")) {
                        JSONObject msgobj = respobj.getJSONObject("msg");

                        JSONObject user_obj = msgobj.getJSONObject("User");
                        stwalletAmount = user_obj.optString("wallet", "0");
                        walletAmount = Double.parseDouble(stwalletAmount);
                        setUpScreenData();
                    }
                } catch (Exception e) {
                   e.printStackTrace();
                }

            }

        });

    }
}