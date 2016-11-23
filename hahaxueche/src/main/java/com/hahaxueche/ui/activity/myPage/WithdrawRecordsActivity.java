package com.hahaxueche.ui.activity.myPage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.WithdrawRecord;
import com.hahaxueche.presenter.myPage.WithdrawRecordsPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.myPage.WithdrawRecordAdapter;
import com.hahaxueche.ui.view.myPage.WithdrawRecordsView;
import com.hahaxueche.util.HHLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/1.
 */

public class WithdrawRecordsActivity extends HHBaseActivity implements WithdrawRecordsView {
    private WithdrawRecordsPresenter mPresenter;
    @BindView(R.id.lv_withdraw_records)
    ListView mLvWithdrawRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new WithdrawRecordsPresenter();
        setContentView(R.layout.activity_withdraw_records);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("已提现");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WithdrawRecordsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void loadWithdrawRecords(ArrayList<WithdrawRecord> withdrawRecords) {
        WithdrawRecordAdapter adapter = new WithdrawRecordAdapter(this, withdrawRecords);
        mLvWithdrawRecords.setAdapter(adapter);
    }
}
