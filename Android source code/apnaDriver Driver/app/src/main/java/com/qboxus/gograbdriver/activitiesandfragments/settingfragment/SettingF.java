package com.qboxus.gograbdriver.activitiesandfragments.settingfragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.activitiesandfragments.walletfragment.BankInfoF;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;
import com.qboxus.gograbdriver.Constants;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import java.util.Locale;

public class SettingF extends RootFragment implements View.OnClickListener {

    View view;
    RelativeLayout tabEditProfile, tabChangePassword, tabPrivacyPolicy, tabTermsCondition, tabPayout, llChangeLanguage;
    View.OnClickListener navClickListener;
    ImageView imgNavMenu;
    TextView tvLanguage, tvContactUs;
    Preferences preferences;
    LinearLayout tabPassword, tabContactUs;

    public SettingF() {
        // Required empty public constructor
    }

    public SettingF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        InitControl();
        ActionControl();
        return view;
    }

    private void ActionControl() {
        imgNavMenu.setOnClickListener(navClickListener);
        tabEditProfile.setOnClickListener(this);
        tabContactUs.setOnClickListener(this);
        tabChangePassword.setOnClickListener(this);
        tabPrivacyPolicy.setOnClickListener(this);
        tabTermsCondition.setOnClickListener(this);
        tabPayout.setOnClickListener(this);
        llChangeLanguage.setOnClickListener(this);
    }

    private void InitControl() {
        preferences = new Preferences(view.getContext());
        tabPassword = view.findViewById(R.id.tab_password);
        imgNavMenu = view.findViewById(R.id.iv_nav_menu);
        tvContactUs = view.findViewById(R.id.tv_contact_us);
        tabEditProfile = view.findViewById(R.id.ll_edit_profile);
        tabChangePassword = view.findViewById(R.id.ll_change_password);
        tabPrivacyPolicy = view.findViewById(R.id.ll_privacy_policy);
        tabTermsCondition = view.findViewById(R.id.ll_terms_and_condition);
        tabPayout = view.findViewById(R.id.ll_payout);
        llChangeLanguage = view.findViewById(R.id.ll_change_language);
        tvLanguage = view.findViewById(R.id.tv_language);
        tabContactUs = view.findViewById(R.id.tab_contact_us);

        setScreenData();
    }

    private void setScreenData() {
        if (preferences.getKeyLocale().equalsIgnoreCase("en")) {
            tvLanguage.setText("English");
        } else {
            tvLanguage.setText("عربى");
        }


        if (preferences.getKeySocialType().equalsIgnoreCase("google") || preferences.getKeySocialType().equalsIgnoreCase("facebook")) {
            tabPassword.setVisibility(View.GONE);
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab_contact_us: {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + tvContactUs.getText().toString()));
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }
            break;
            case R.id.ll_edit_profile: {
                EditProfileF f = new EditProfileF();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.setting_container, f, "EditProfile_F").addToBackStack("EditProfile_F").commit();
            }
            break;
            case R.id.ll_change_password: {
                UpdatePasswordF f = new UpdatePasswordF();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.setting_container, f, "UpdatePassword_F").addToBackStack("UpdatePassword_F").commit();
            }
            break;
            case R.id.ll_privacy_policy: {
                PrivacyAndTermsF f = new PrivacyAndTermsF(view.getContext().getString(R.string.privacy_policy), Constants.PRIVACY_POLICY);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.setting_container, f, "Privacy_Policy_F").addToBackStack("Privacy_Policy_F").commit();
            }
            break;
            case R.id.ll_terms_and_condition: {
                PrivacyAndTermsF f = new PrivacyAndTermsF(view.getContext().getString(R.string.terms_and_condition), Constants.TERMS_CONDITIONS);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.setting_container, f, "Terms_and_Condition_F").addToBackStack("Terms_and_Condition_F").commit();
            }
            break;
            case R.id.ll_payout: {
                BankInfoF f = new BankInfoF();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.setting_container, f, "BankInfo_F").addToBackStack("BankInfo_F").commit();
            }
            break;
            case R.id.ll_change_language: {
                CityAndGenderF f = new CityAndGenderF(view.getContext().getString(R.string.select_language), preferences.getKeyLocale(), new FragmentCallback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        if (bundle != null) {
                            if (bundle.getBoolean("IsResponce", false)) {
                                if (bundle.getString("Data").equalsIgnoreCase("English")) {
                                    preferences.setKeyLocale("en");
                                } else if (bundle.getString("Data").equalsIgnoreCase("عربى")) {
                                    preferences.setKeyLocale("ar");
                                }
                                setLocale("" + preferences.getKeyLocale());


                                if (preferences.getKeyLocale().equalsIgnoreCase("en")) {
                                    tvLanguage.setText("English");
                                } else {
                                    tvLanguage.setText("عربى");
                                }
                            }
                        }
                    }
                });
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.setting_container, f, "CitySelection_F").addToBackStack("CitySelection_F").commit();
            }
            break;

        }
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = new Configuration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        onConfigurationChanged(conf);

        Intent intent = new Intent(view.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}
