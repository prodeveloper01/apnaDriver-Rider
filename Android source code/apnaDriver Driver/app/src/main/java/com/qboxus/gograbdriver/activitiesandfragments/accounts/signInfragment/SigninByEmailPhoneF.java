package com.qboxus.gograbdriver.activitiesandfragments.accounts.signInfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.R;

import java.util.ArrayList;
import java.util.List;


public class SigninByEmailPhoneF extends Fragment implements View.OnClickListener {

    View view;
    TabLayout tabLayout;
    ViewPager viewPager;
    ImageView ivBack;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin_by_email_phone_, container, false);
        initControl();
        actionControl();
        return view;
    }

    private void actionControl() {
        ivBack.setOnClickListener(this);
    }

    private void initControl() {
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.pager);
        ivBack = view.findViewById(R.id.iv_back);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Functions.hideSoftKeyboard(getActivity());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setUpViewPager();
    }

    private void setUpViewPager() {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(SigninPhoneF.newInstance(), view.getContext().getString(R.string.phone));
        adapter.addFragment(SigninEmailF.newInstance(), view.getContext().getString(R.string.email_username));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                getActivity().onBackPressed();
            }
            break;
        }
    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }


}