package com.hahaxueche.ui.activity.myPage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.Referrer;
import com.hahaxueche.presenter.myPage.ReferrerListPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.myPage.ReferrerAdapter;
import com.hahaxueche.ui.view.myPage.ReferrerListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/2.
 */

public class ReferrerListActivity extends HHBaseActivity implements ReferrerListView, XListView.IXListViewListener {
    private ReferrerListPresenter mPresenter;
    private ImageView mIvBack;
    private TextView mTvTitle;
    @BindView(R.id.xlv_referrer_list)
    XListView mXlvReferrers;
    private ReferrerAdapter mReferrerAdapter;
    private ArrayList<Referrer> mReferrerArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ReferrerListPresenter();
        setContentView(R.layout.activity_referrer_list);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        mXlvReferrers.setPullRefreshEnable(true);
        mXlvReferrers.setPullLoadEnable(true);
        mXlvReferrers.setAutoLoadEnable(true);
        mXlvReferrers.setXListViewListener(this);
        mPresenter.fetchReferrers();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("推荐有奖");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReferrerListActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        mXlvReferrers.setPullLoadEnable(enable);
    }

    @Override
    public void refreshReferrerList(ArrayList<Referrer> ReferrerArrayList) {
        mReferrerArrayList = ReferrerArrayList;
        mReferrerAdapter = new ReferrerAdapter(getContext(), mReferrerArrayList);
        mXlvReferrers.setAdapter(mReferrerAdapter);
        mXlvReferrers.stopRefresh();
        mXlvReferrers.stopLoadMore();
    }

    @Override
    public void addMoreReferrerList(ArrayList<Referrer> ReferrerArrayList) {
        mReferrerArrayList.addAll(ReferrerArrayList);
        mReferrerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchReferrers();
    }

    @Override
    public void onLoadMore() {
        mPresenter.addMoreReferrers();
    }
}
