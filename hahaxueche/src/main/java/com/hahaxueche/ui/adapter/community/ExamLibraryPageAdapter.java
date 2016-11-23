package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hahaxueche.ui.fragment.community.ExamLibraryFragment;
import com.hahaxueche.util.ExamLib;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class ExamLibraryPageAdapter extends FragmentPagerAdapter {
    public final int COUNT = 2;
    private String[] titles = new String[]{"科目一", "科目四"};

    public ExamLibraryPageAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ExamLibraryFragment.newInstance(ExamLib.EXAM_TYPE_1);
        } else {
            return ExamLibraryFragment.newInstance(ExamLib.EXAM_TYPE_4);
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
