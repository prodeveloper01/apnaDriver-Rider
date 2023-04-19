package com.qboxus.gograbdriver.activitiesandfragments.myjobs;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qboxus.gograbdriver.activitiesandfragments.mainnavigation.MainActivity;
import com.qboxus.gograbdriver.adapters.PagerAdapter;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.R;


public class MyJobF extends RootFragment {

    View view;
    ImageView ivNavMenu;
    View.OnClickListener navClickListener;
    PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static int selectedPosiotion = 0;

    public MyJobF() {
        // Required empty public constructor
    }

    public MyJobF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_job_main, container, false);
        initControl();
        actionControl();
        methodCustomTabs();
        return view;
    }

    private void actionControl() {
        pagerAdapter = new PagerAdapter(getResources(), getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initControl() {
        ivNavMenu = view.findViewById(R.id.iv_nav_menu);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        ivNavMenu.setOnClickListener(navClickListener);
    }


    @SuppressLint("SetTextI18n")
    private void methodCustomTabs() {

        View v1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_tabs_layout, null);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.black));

        TextView tv1 = v1.findViewById(R.id.tv_tab_title);
        tv1.setText(R.string.new_order);
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv1.setBackground(getResources().getDrawable(R.drawable.d_round_corner_colored));
        tabLayout.getTabAt(0).setCustomView(v1);

        View v2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_tabs_layout, null);
        TextView tv2 = v2.findViewById(R.id.tv_tab_title);
        tv2.setText(R.string.processing);
        tv2.setTextColor(getResources().getColor(R.color.black));
        tv2.setBackground(getResources().getDrawable(R.drawable.d_round_button_white_tab));
        tabLayout.getTabAt(1).setCustomView(v2);

        View v3 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_tabs_layout, null);
        TextView tv3 = v3.findViewById(R.id.tv_tab_title);
        tv3.setText(R.string.delivered);
        tv3.setTextColor(getResources().getColor(R.color.black));
        tv3.setBackground(getResources().getDrawable(R.drawable.d_round_button_white_tab));
        tabLayout.getTabAt(2).setCustomView(v3);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                selectedPosiotion = tab.getPosition();

                if (v != null) {
                    TextView tv = v.findViewById(R.id.tv_tab_title);
                    tv.setTextColor(getResources().getColor(R.color.white));
                    tv.setBackground(getResources().getDrawable(R.drawable.d_round_corner_colored));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();

                if (v != null) {
                    TextView tv = v.findViewById(R.id.tv_tab_title);
                    tv.setTextColor(getResources().getColor(R.color.black));
                    tv.setBackground(getResources().getDrawable(R.drawable.d_round_button_white_tab));
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // on tab reselect this method will be ca
            }
        });

    }
}
