package com.qboxus.gograbdriver.adapters;


import android.content.res.Resources;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.qboxus.gograbdriver.activitiesandfragments.myjobs.DeliveredOrderF;
import com.qboxus.gograbdriver.activitiesandfragments.myjobs.HomeF;
import com.qboxus.gograbdriver.activitiesandfragments.myjobs.ProcessingOrderF;


public class PagerAdapter extends FragmentPagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public PagerAdapter(final Resources resources, FragmentManager fm) {
        super(fm , BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment result;
        switch (position) {
            case 0:
                result = new HomeF();
                break;

            case 1:
                result = new ProcessingOrderF();
                break;

            case 2:
                result=new DeliveredOrderF();
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
