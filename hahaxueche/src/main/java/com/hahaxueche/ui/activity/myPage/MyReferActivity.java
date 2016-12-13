package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.user.Referrer;
import com.hahaxueche.presenter.myPage.MyReferPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.myPage.ReferrerAdapter;
import com.hahaxueche.ui.view.myPage.MyReferView;
import com.hahaxueche.ui.widget.pullToRefreshView.XListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/12/13.
 */

public class MyReferActivity extends HHBaseActivity implements MyReferView, XListView.IXListViewListener {
    private MyReferPresenter mPresenter;
    @BindView(R.id.xlv_referrer_list)
    XListView mXlvReferrers;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.tv_withdraw_money)
    TextView mTvWithdrawMoney;
    private ReferrerAdapter mReferrerAdapter;
    private ArrayList<Referrer> mReferrerArrayList;
    private static final int REQUEST_CODE_WITHDRAW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MyReferPresenter();
        setContentView(R.layout.activity_my_refer);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        mXlvReferrers.setPullRefreshEnable(true);
        mXlvReferrers.setPullLoadEnable(true);
        mXlvReferrers.setAutoLoadEnable(true);
        mXlvReferrers.setXListViewListener(this);
        mXlvReferrers.setEmptyView(mTvEmpty);
        mPresenter.fetchReferrers();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("推荐有奖");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyReferActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.tv_withdraw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_withdraw:
                mPresenter.clickWithdraw();
                break;
            default:
                break;
        }
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
    public void navigateToWithdraw() {
        Intent intent = new Intent(getContext(), WithdrawActivity.class);
        startActivityForResult(intent, REQUEST_CODE_WITHDRAW);
    }

    @Override
    public void setWithdrawMoney(String money) {
        mTvWithdrawMoney.setText(money);
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchReferrers();
    }

    @Override
    public void onLoadMore() {
        mPresenter.addMoreReferrers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WITHDRAW) {
            if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("isUpdate", false)) {
                mPresenter.refreshBonus();
                mPresenter.fetchReferrers();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
