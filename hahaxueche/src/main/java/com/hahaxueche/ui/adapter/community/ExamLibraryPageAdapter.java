package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hahaxueche.ui.fragment.community.ExamLibraryFragment;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryPageAdapter extends FragmentPagerAdapter {
    public final int COUNT = 2;
    private String[] titles = new String[]{"科目一", "科目四"};
    private Context context;

    public ExamLibraryPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return ExamLibraryFragment.newInstance(position + 1);
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
