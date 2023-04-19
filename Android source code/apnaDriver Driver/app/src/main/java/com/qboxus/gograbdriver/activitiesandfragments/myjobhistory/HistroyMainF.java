package com.qboxus.gograbdriver.activitiesandfragments.myjobhistory;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qboxus.gograbdriver.helpingclasses.fragmentbackpresshelper.RootFragment;
import com.qboxus.gograbdriver.R;


public class HistroyMainF extends RootFragment {

    View view;
    ImageView ivNavMenu;
    View.OnClickListener navClickListener;
    Pager_Adapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static int selectedPosiotion = 0;

    public HistroyMainF() {
        // Required empty public constructor
    }

    public HistroyMainF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_main, container, false);
        initControl();
        actionControl();
        methodCustomTabs();
        return view;
    }

    private void actionControl() {
        pagerAdapter = new Pager_Adapter(getResources(), getChildFragmentManager());
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
        tv1.setText(R.string.rides);
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv1.setBackground(getResources().getDrawable(R.drawable.d_round_corner_colored));
        tabLayout.getTabAt(0).setCustomView(v1);

        View v2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_tabs_layout, null);
        TextView tv2 = v2.findViewById(R.id.tv_tab_title);
        tv2.setText(R.string.parcel);
        tv2.setTextColor(getResources().getColor(R.color.black));
        tv2.setBackground(getResources().getDrawable(R.drawable.d_round_button_white_tab));
        tabLayout.getTabAt(1).setCustomView(v2);

        View v3 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_tabs_layout, null);
        TextView tv3 = v3.findViewById(R.id.tv_tab_title);
        tv3.setText(R.string.food);
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

    class Pager_Adapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public Pager_Adapter(final Resources resources, FragmentManager fm) {
            super(fm , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new MyJobHistoryF();
                    break;

                case 1:
                    result = new ParcelOrderHistoryF();
                    break;

                case 2:
                    result=new FoodOrderHistoryF();
                    break;

                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        public Fragment getRegisteredFragment(int position) {

            return registeredFragments.get(position);
        }
    }

}
