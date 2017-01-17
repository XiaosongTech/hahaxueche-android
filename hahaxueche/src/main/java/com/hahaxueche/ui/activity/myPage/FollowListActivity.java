package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.myPage.FollowListPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.findCoach.CoachDetailActivity;
import com.hahaxueche.ui.adapter.findCoach.CoachAdapter;
import com.hahaxueche.ui.view.myPage.FollowListView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/9.
 */

public class FollowListActivity extends HHBaseActivity implements FollowListView, XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private FollowListPresenter mPresenter;
    @BindView(R.id.xlv_coaches)
    XListView mXlvCoaches;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    private CoachAdapter mCoachAdapter;
    private ArrayList<Coach> mCoachArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FollowListPresenter();
        setContentView(R.layout.activity_follow_list);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        mXlvCoaches.setPullRefreshEnable(true);
        mXlvCoaches.setPullLoadEnable(true);
        mXlvCoaches.setAutoLoadEnable(true);
        mXlvCoaches.setXListViewListener(this);
        mXlvCoaches.setOnItemClickListener(this);
        mXlvCoaches.setEmptyView(mTvEmpty);
        mPresenter.fetchCoaches();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我关注的教练");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowListActivity.this.finish();
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
        mXlvCoaches.setPullLoadEnable(enable);
    }

    @Override
    public void refreshCoachList(ArrayList<Coach> CoachArrayList) {
        mCoachArrayList = CoachArrayList;
        mCoachAdapter = new CoachAdapter(getContext(), mCoachArrayList);
        mXlvCoaches.setAdapter(mCoachAdapter);
        mXlvCoaches.stopRefresh();
        mXlvCoaches.stopLoadMore();
    }

    @Override
    public void addMoreCoachList(ArrayList<Coach> CoachArrayList) {
        mCoachArrayList.addAll(CoachArrayList);
        mCoachAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCoachArrayList != null && mCoachArrayList.size() > 0 && position > 0 && position - 1 < mCoachArrayList.size()) {
            Intent intent = new Intent(getContext(), CoachDetailActivity.class);
            intent.putExtra("coach", mCoachArrayList.get(position - 1));
            startActivity(intent);
        }
    }


    @Override
    public void onRefresh() {
        mPresenter.fetchCoaches();
    }

    @Override
    public void onLoadMore() {
        mPresenter.addMoreCoaches();
    }
}
