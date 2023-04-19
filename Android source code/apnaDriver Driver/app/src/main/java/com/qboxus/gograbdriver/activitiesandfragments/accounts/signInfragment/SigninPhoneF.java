package com.qboxus.gograbdriver.activitiesandfragments.accounts.signInfragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment.SignupPhoneVerificationF;
import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.CityAndGenderF;
import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.PrivacyAndTermsF;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.Constants;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.models.CityAndGenderModel;
import com.qboxus.gograbdriver.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SigninPhoneF extends Fragment implements View.OnClickListener {

    View view;
    LinearLayout btnSendCode;
    EditText etPhoneNumber;
    RelativeLayout tabCountryCode;
    ImageView ivcountrycodearrow;
    TextView tvCountryCode;
    CountryCodePicker ccp;
    TextView tvTermsCondition;
    List<Link> links = new ArrayList<>();
    String selectedId = "";

    public static SigninPhoneF newInstance() {
        SigninPhoneF fragment = new SigninPhoneF();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin_phone_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        tabCountryCode.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);

    }

    private void initControl() {
        tabCountryCode = view.findViewById(R.id.no_start);
        btnSendCode = view.findViewById(R.id.btn_send_code);
        tvCountryCode = view.findViewById(R.id.tv_country_code);
        ivcountrycodearrow = view.findViewById(R.id.iv_country_code_arrow);
        etPhoneNumber = view.findViewById(R.id.et_phoneno);
        tvTermsCondition = view.findViewById(R.id.tv_terms_condition);
        ccp = new CountryCodePicker(view.getContext());
        ccp.registerPhoneNumberTextView(etPhoneNumber);


        setupScreenData();
    }

    private void setupScreenData() {
        tvCountryCode.setHint(ccp.getSelectedCountryNameCode() + " " + ccp.getSelectedCountryCodeWithPlus());


        callApiShowCountries();

        Link link = new Link(view.getContext().getString(R.string.terms_of_use));
        link.setTextColor(getResources().getColor(R.color.ColorBlack));
        link.setTextColorOfHighlightedLink(getResources().getColor(R.color.ColorDarkGray));
        link.setUnderlined(true);
        link.setBold(false);
        link.setHighlightAlpha(.20f);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                PrivacyAndTermsF f = new PrivacyAndTermsF(view.getContext().getString(R.string.terms_and_condition), Constants.TERMS_CONDITIONS);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.account_container, f, "Terms_and_Condition_F").addToBackStack("Terms_and_Condition_F").commit();
            }
        });

        Link link2 = new Link(view.getContext().getString(R.string.privacy_policy));
        link2.setTextColor(getResources().getColor(R.color.ColorBlack));
        link2.setTextColorOfHighlightedLink(getResources().getColor(R.color.ColorDarkGray));
        link2.setUnderlined(true);
        link2.setBold(false);
        link2.setHighlightAlpha(.20f);
        link2.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                PrivacyAndTermsF f = new PrivacyAndTermsF(view.getContext().getString(R.string.privacy_policy), Constants.PRIVACY_POLICY);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.account_container, f, "Privacy_Policy_F").addToBackStack("Privacy_Policy_F").commit();
            }
        });

        links.add(link);
        links.add(link2);
        CharSequence sequence = LinkBuilder.from(view.getContext(), tvTermsCondition.getText().toString())
                .addLinks(links)
                .build();
        tvTermsCondition.setText(sequence);
        tvTermsCondition.setMovementMethod(TouchableMovementMethod.getInstance());
    }


    private void callApiShowCountries() {
        ApiRequest.callApi(getContext(), ApisList.showCountries, new JSONObject(), new CallbackResponce() {
            @Override
            public void responce(String resp) {
                if (resp != null) {

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")) {
                            JSONArray msgarray = respobj.getJSONArray("msg");
                            methodGettingCountriesList(msgarray);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void methodGettingCountriesList(JSONArray msgarray) {
        try {

            for (int i = 0; i < msgarray.length(); i++) {
                JSONObject countriesobj = msgarray.getJSONObject(i).getJSONObject("Country");

                CityAndGenderModel model = new CityAndGenderModel();
                model.setId(countriesobj.optString("id"));
                model.setName(countriesobj.optString("name"));
                model.setPhonecode(countriesobj.optString("phonecode"));
                model.setIso(countriesobj.optString("iso"));
                model.setSelected(false);

                if (model.getIso().equalsIgnoreCase(ccp.getSelectedCountryNameCode())) {
                    ccp.setCountryForNameCode("" + model.getIso());
                    tvCountryCode.setText(ccp.getSelectedCountryNameCode() + " " + ccp.getSelectedCountryCodeWithPlus());
                    ivcountrycodearrow.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                    selectedId = model.getId();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_code: {
                Functions.hideSoftKeyboard(getActivity());
                if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
                    etPhoneNumber.setError("" + getResources().getString(R.string.cant_empty));
                    etPhoneNumber.setFocusable(true);
                    return;
                }
                if (!ccp.isValid()) {
                    etPhoneNumber.setError("" + getResources().getString(R.string.invalid_phone_no));
                    etPhoneNumber.setFocusable(true);
                    return;
                }

                String phoneNo=Functions.getValidPhoneNumber(ccp.getSelectedCountryCodeWithPlus(),
                        ""+etPhoneNumber.getText().toString());
                callApiPhoneNoVerification(phoneNo);
            }
            break;
            case R.id.no_start: {
                Functions.hideSoftKeyboard(getActivity());
                CityAndGenderF f = new CityAndGenderF(view.getContext().getString(R.string.select_country), selectedId, new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getBoolean("IsResponce", false)) {
                                CityAndGenderModel model = (CityAndGenderModel) bundle.getSerializable("Data");
                                Functions.logDMsg("ccp.setCountryForNameCode"+model.getIso()+model.getName()+model.id);

                                ccp.setCountryForNameCode("" + model.getIso());
                                tvCountryCode.setText(ccp.getSelectedCountryNameCode() + " " + ccp.getSelectedCountryCodeWithPlus());
                                ivcountrycodearrow.setColorFilter(ContextCompat.getColor(view.getContext(), R.color.ColorBlack), android.graphics.PorterDuff.Mode.SRC_IN);
                            }
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.main_signin_container, f, "CitySelection_F").addToBackStack("CitySelection_F").commit();
            }
            break;
            default:
                break;
        }
    }

    private void showVerificationScreen(String phoneNo) {
        SignupPhoneVerificationF f = new SignupPhoneVerificationF(false);
        Bundle bundle = new Bundle();
        bundle.putString("PhoneNo", "" + phoneNo);
        bundle.putString("IOS", "" + ccp.getSelectedCountryNameCode());
        bundle.putString("CuntryCode", "" + ccp.getSelectedCountryCodeWithPlus());
        f.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, f, "SignuphoneVerification_F").addToBackStack("SignuphoneVerification_F").commit();
    }

    private void callApiPhoneNoVerification(String phoneNo) {
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("phone", phoneNo);
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
                            showVerificationScreen(phoneNo);
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
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


