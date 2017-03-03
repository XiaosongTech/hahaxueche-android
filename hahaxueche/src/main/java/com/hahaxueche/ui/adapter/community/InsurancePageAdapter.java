package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hahaxueche.ui.fragment.community.PassAssuranceFragment;
import com.hahaxueche.ui.fragment.community.PayInsuranceFragment;

/**
 * Created by wangshirui on 2017/2/28.
 */

public class InsurancePageAdapter extends FragmentPagerAdapter {
    public final int COUNT = 2;
    private String[] titles = new String[]{"挂科险(免费)", "赔付宝"};

    public InsurancePageAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PassAssuranceFragment.newInstance();
        } else {
            return PayInsuranceFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}