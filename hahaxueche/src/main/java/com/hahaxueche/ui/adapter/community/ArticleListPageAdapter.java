package com.hahaxueche.ui.adapter.community;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hahaxueche.model.base.ArticleCategory;
import com.hahaxueche.ui.fragment.community.ArticleListFragment;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/3.
 */

public class ArticleListPageAdapter extends FragmentPagerAdapter {
    private Context context;
    private ArrayList<ArticleCategory> mArticleCategories;

    public ArticleListPageAdapter(FragmentManager fm, Context context, ArrayList<ArticleCategory> articleCategories) {
        super(fm);
        this.context = context;
        this.mArticleCategories = articleCategories;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ArticleListFragment.newInstance(1, 0);
        } else {
            return ArticleListFragment.newInstance(0, mArticleCategories.get(position - 1).type);
        }
    }

    @Override
    public int getCount() {
        return mArticleCategories.size() + 1;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "热门新闻";
        } else
            return mArticleCategories.get(position - 1).name;
    }
}
