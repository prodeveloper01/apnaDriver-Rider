package com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.qboxus.gograbdriver.activitiesandfragments.settingfragment.PrivacyAndTermsF;
import com.qboxus.gograbdriver.Constants;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.models.RequestRegisterUserModel;
import com.qboxus.gograbdriver.R;

import java.util.ArrayList;
import java.util.List;

public class SignupEmailF extends Fragment implements View.OnClickListener {

    RequestRegisterUserModel model;
    View view;
    ImageView ivBack;
    LinearLayout btnSendCode;
    EditText etEmail;
    TextView tvTermsCondition;
    List<Link> links = new ArrayList<>();


    public SignupEmailF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_signup_email_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);
    }

    private void initControl() {
        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        ivBack =view.findViewById(R.id.iv_back);
        btnSendCode =view.findViewById(R.id.btn_send_code);
        etEmail =view.findViewById(R.id.et_email);
        tvTermsCondition =view.findViewById(R.id.tv_terms_condition);

        setupScreenData();
    }

    private void setupScreenData() {


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
                        ft.replace(R.id.account_container, f,"Terms_and_Condition_F").addToBackStack("Terms_and_Condition_F").commit();
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
                        PrivacyAndTermsF f = new PrivacyAndTermsF(view.getContext().getString(R.string.privacy_policy),Constants.PRIVACY_POLICY);
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                        ft.replace(R.id.account_container, f,"Privacy_Policy_F").addToBackStack("Privacy_Policy_F").commit();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
            {
                getActivity().onBackPressed();
            }
            break;
            case R.id.btn_send_code:
                Functions.hideSoftKeyboard(getActivity());
            {
                if(TextUtils.isEmpty(etEmail.getText().toString()))
                {
                    etEmail.setError(""+getResources().getString(R.string.cant_empty));
                    etEmail.setFocusable(true);
                    return;
                }
               if (!(Functions.isValidEmail(etEmail.getText().toString())))
               {
                   etEmail.setError(""+getResources().getString(R.string.invalid_email));
                   etEmail.setFocusable(true);
                   return;
               }
                model.setEmail(""+ etEmail.getText().toString());
                showPasswordScreen();
            }
            break;

            default:
                break;
        }
    }


    private void showPasswordScreen() {

        //        incase from social login email is not given by facebook i show emailscreen
        if (TextUtils.isEmpty(model.getPassword()))
        {
            SignupPasswordF signupPasswordF = new SignupPasswordF();
            Bundle bundle=new Bundle();
            bundle.putSerializable("UserData",model);
            signupPasswordF.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            ft.replace(R.id.account_container, signupPasswordF,"SignupPassword_F").addToBackStack("SignupPassword_F").commit();
        }
        else
        {
            SignupFullNameF signupUserNameF = new SignupFullNameF();
            Bundle bundle=new Bundle();
            bundle.putSerializable("UserData",model);
            signupUserNameF.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            ft.replace(R.id.account_container, signupUserNameF,"SignupFullName_F").addToBackStack("SignupFullName_F").commit();
        }


    }



    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


