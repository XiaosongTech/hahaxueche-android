package com.hahaxueche.ui.fragment.index.exam;


import android.support.v4.app.Fragment;

/**
 * Created by wangshirui on 16/8/10.
 */
public class ExamPageItem {
    private String mTitle;

    public ExamPageItem(String title) {
        this.mTitle = title;
    }

    public Fragment createFragment() {
        return ExamLibraryFragment.instance(mTitle);
    }

    public String getTitle() {
        return mTitle;
    }
}
