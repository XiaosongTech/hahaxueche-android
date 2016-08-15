package com.hahaxueche.ui.widget.slidingTabLayout;

import android.support.v4.app.Fragment;

/**
 * Created by gibxin on 2016/4/5.
 */
public class PageItem {
    /*item 的信息*/
    private String mMsg;
    /*item的 title*/
    private String mTitle;

    public PageItem(String mTitle, String mMsg) {
        this.mMsg = mMsg;
        this.mTitle = mTitle;
    }

    public Fragment createFragment() {
        return ContentFragment.instance(mMsg);
    }

    public String getTitle() {
        return mTitle;
    }
}
