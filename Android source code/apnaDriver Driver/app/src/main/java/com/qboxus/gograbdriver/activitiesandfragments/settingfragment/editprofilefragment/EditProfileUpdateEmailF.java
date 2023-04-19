package com.qboxus.gograbdriver.activitiesandfragments.settingfragment.editprofilefragment;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.helpingclasses.ApiRequest;
import com.qboxus.gograbdriver.helpingclasses.ApisList;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.models.EditProfileUpdatePhoneNoModel;
import com.qboxus.gograbdriver.R;

import org.json.JSONException;
import org.json.JSONObject;


public class EditProfileUpdateEmailF extends RootFragment implements View.OnClickListener{

    EditProfileUpdatePhoneNoModel model;
    View view;
    ImageView ivBack;
    LinearLayout btnSendCode;
    EditText etEmail;
    FragmentCallback callback;
    Preferences preferences;

    public EditProfileUpdateEmailF() {
    }

    public EditProfileUpdateEmailF(FragmentCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_edit_profile_update_email_, container, false);
        initcontrol();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);
    }

    private void initcontrol() {
        preferences=new Preferences(view.getContext());
        model=new EditProfileUpdatePhoneNoModel();
        ivBack =view.findViewById(R.id.iv_back);
        btnSendCode =view.findViewById(R.id.btn_send_code);
        etEmail =view.findViewById(R.id.et_email);

        setupScreenData();
    }

    private void setupScreenData() {
        etEmail.setText(preferences.getKeyUserEmail());
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
                callApiUpdateEmail();
            }
            break;

            default:
                break;
        }
    }

    private void showVerificationScreen() {
        EditProfileVerificationCodeF f = new EditProfileVerificationCodeF(false);
        Bundle bundle=new Bundle();
        bundle.putSerializable("UserData",model);
        f.setArguments(bundle);
        FragmentTransaction ft =getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        ft.replace(R.id.edit_profile_email_container, f).commit();
    }



    private void callApiUpdateEmail(){
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("email", etEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.showLoader(getContext(),false,false);
        ApiRequest.callApi(getContext(), ApisList.changeEmailAddress, sendobj, new CallbackResponce() {
            @Override
            public void responce(String resp) {
                Functions.cancelLoader();
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            showVerificationScreen();

                        }else {
                            Functions.showToast(view.getContext(),""+respobj.getString("msg"));
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
        Bundle bundle=new Bundle();
        bundle.putBoolean("IsResponce",true);
        callback.Responce(bundle);
        Functions.hideSoftKeyboard(getActivity());
        super.onDetach();
    }
}


