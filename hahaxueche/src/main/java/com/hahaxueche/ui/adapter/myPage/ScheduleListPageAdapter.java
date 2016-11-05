package com.hahaxueche.ui.adapter.myPage;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hahaxueche.ui.fragment.myPage.ScheduleListFragment;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class ScheduleListPageAdapter extends FragmentPagerAdapter {
    public final int COUNT = 2;
    private String[] titles = new String[]{"教练课程", "我的课程"};
    private Context context;

    public ScheduleListPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ScheduleListFragment.newInstance(0);
        } else {
            return ScheduleListFragment.newInstance(1);
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
