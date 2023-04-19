package com.qboxus.gograbdriver.activitiesandfragments.accounts.signupfragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.models.RequestRegisterUserModel;
import com.qboxus.gograbdriver.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupPasswordF extends Fragment implements View.OnClickListener {
    RequestRegisterUserModel model;
    View view;
    ImageView ivBack;
    LinearLayout btnNext;
    EditText etPassword;
    private Boolean check = true;
    private ImageView ivPassword;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_signup_password_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivPassword.setOnClickListener(this);
    }

    private void initControl() {
        model= (RequestRegisterUserModel) getArguments().getSerializable("UserData");
        ivBack =view.findViewById(R.id.iv_back);
        btnNext =view.findViewById(R.id.btn_next);
        etPassword =view.findViewById(R.id.et_password);
        ivPassword =view.findViewById(R.id.iv_password);

        setupScreenData();
    }

    private void setupScreenData() {
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
            {
                getActivity().onBackPressed();
            }
            break;
            case R.id.iv_password:
            {
                if (check){
                    etPassword.setTransformationMethod(null);
                    ivPassword.setImageResource(R.drawable.ic_un_hide);
                    check = false;
                    etPassword.setSelection(etPassword.length());
                }else {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                    ivPassword.setImageResource(R.drawable.ic_hide);
                    check = true;
                    etPassword.setSelection(etPassword.length());
                }
            }
            break;
            case R.id.btn_next:
                Functions.hideSoftKeyboard(getActivity());
            {
                if(TextUtils.isEmpty(etPassword.getText().toString()))
                {
                    etPassword.setError(""+getResources().getString(R.string.cant_empty));
                    etPassword.setFocusable(true);
                    return;
                }
                if (etPassword.getText().toString().length()<8)
                {
                    etPassword.setError(""+getResources().getString(R.string.invalid_password));
                    etPassword.setFocusable(true);
                    return;
                }
                if (etPassword.getText().toString().length()>20)
                {
                    etPassword.setError(""+getResources().getString(R.string.invalid_password));
                    etPassword.setFocusable(true);
                    return;
                }
                if (passwordSpecialValidate(etPassword.getText().toString()))
                {
                    etPassword.setError(""+getResources().getString(R.string.use_8_20_characters_from_at_least_2_categories_letters_numbers_special_characters));
                    etPassword.setFocusable(true);
                    return;
                }

                model.setPassword(""+ etPassword.getText().toString());
                showUserNameScreen();


            }
            break;

            default:
                break;
        }
    }

    private boolean passwordSpecialValidate(String password) {
        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        boolean spStr = matcher.find();
        Pattern pattern1 = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(password);
        boolean numStr = matcher1.find();
        Pattern pattern2 = Pattern.compile("[a-z]", Pattern.CASE_INSENSITIVE);
        Matcher matcher2 = pattern2.matcher(password);
        boolean letStr = matcher2.find();

        Functions.logDMsg("number "+numStr);
        Functions.logDMsg("letter "+letStr);
        Functions.logDMsg("special "+spStr);
        if (numStr && letStr)
        {
            return false;
        }
        if (numStr && spStr)
        {
            return false;
        }
        if (letStr && spStr)
        {
            return false;
        }
        return true;
    }

    private void showUserNameScreen() {
        SignupFullNameF signupUserNameF = new SignupFullNameF();
        Bundle bundle=new Bundle();
        bundle.putSerializable("UserData",model);
        signupUserNameF.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.account_container, signupUserNameF,"SignupFullName_F").addToBackStack("SignupFullName_F").commit();
    }


    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


