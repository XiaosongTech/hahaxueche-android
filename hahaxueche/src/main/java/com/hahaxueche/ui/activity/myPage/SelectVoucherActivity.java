package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.adapter.myPage.SelectVoucherAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/12.
 */

public class SelectVoucherActivity extends HHBaseActivity {
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.lv_vouchers)
    ListView mLvVouchers;
    SelectVoucherAdapter mAdapter;
    private ArrayList<Voucher> mVoucherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_voucher);
        ButterKnife.bind(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getParcelableArrayListExtra("voucherList") != null) {
            mVoucherList = intent.getParcelableArrayListExtra("voucherList");
            mAdapter = new SelectVoucherAdapter(getContext(), mVoucherList);
            mLvVouchers.setAdapter(mAdapter);
            mLvVouchers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (mVoucherList != null && mVoucherList.size() > 0 && position > -1 && position < mVoucherList.size()) {
                        unSelectAll();
                        mVoucherList.get(position).isSelect = true;
                        mAdapter.notifyDataSetChanged();
                        Intent intent = new Intent();
                        intent.putExtra("voucherList", mVoucherList);
                        setResult(RESULT_OK, intent);
                        SelectVoucherActivity.this.finish();
                    }
                }
            });
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("选择代金券");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectVoucherActivity.this.finish();
            }
        });
    }

    private void unSelectAll() {
        if (mVoucherList == null || mVoucherList.size() < 1) return;
        for (Voucher voucher : mVoucherList) {
            voucher.isSelect = false;
        }
    }
}
