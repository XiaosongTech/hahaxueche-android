package com.hahaxueche.ui.fragment.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hahaxueche.R;
import com.hahaxueche.model.community.Article;
import com.hahaxueche.presenter.community.CommunityPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.activity.community.ArticleActivity;
import com.hahaxueche.ui.activity.community.ExamLibraryActivity;
import com.hahaxueche.ui.adapter.community.ArticleAdapter;
import com.hahaxueche.ui.adapter.community.ArticleListPageAdapter;
import com.hahaxueche.ui.fragment.HHBaseFragment;
import com.hahaxueche.ui.view.community.CommunityView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/13.
 */
public class CommunityFragment extends HHBaseFragment implements CommunityView {
    private MainActivity mActivity;
    private CommunityPresenter mPresenter;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CommunityPresenter();
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        ButterKnife.bind(this, view);
        mPresenter.attachView(this);
        ArticleListPageAdapter adapter = new ArticleListPageAdapter(getChildFragmentManager(), getContext(), mPresenter.getArticleCaterories());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mViewPager.setOffscreenPageLimit(mTabLayout.getTabCount());
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.rly_test_lib,
            R.id.rly_group_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_test_lib:
                mPresenter.clickTestLibCount();
                mActivity.startActivity(new Intent(getContext(), ExamLibraryActivity.class));
                break;
            case R.id.rly_group_buy:
                mPresenter.clickGroupBuyCount();
                break;
            default:
                break;
        }
    }
}
