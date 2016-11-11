package com.hahaxueche.ui.activity.myPage;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.presenter.myPage.MyVoucherPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.MyVoucherView;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/11/11.
 */

public class MyVoucherActivity extends HHBaseActivity implements MyVoucherView, SwipeRefreshLayout.OnRefreshListener {
    private MyVoucherPresenter mPresenter;
    ImageView mIvBack;
    TextView mTvTitle;
    @BindView(R.id.lly_vouchers)
    LinearLayout mLlyVouchers;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout mSrlMain;
    @BindView(R.id.iv_no_voucher)
    ImageView mIvNoVoucher;
    @BindView(R.id.tv_no_voucher)
    TextView mTvNoVoucher;
    @BindView(R.id.tv_voucher_rules)
    TextView mTvVoucherRules;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MyVoucherPresenter();
        setContentView(R.layout.activity_my_voucher);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        mSrlMain.setOnRefreshListener(this);
        mSrlMain.setColorSchemeResources(R.color.app_theme_color);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("我的代金券");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyVoucherActivity.this.finish();
            }
        });
    }

    @Override
    public void loadVouchers(ArrayList<Voucher> vouchers) {
        mIvNoVoucher.setVisibility(View.GONE);
        mTvNoVoucher.setVisibility(View.GONE);
        mTvVoucherRules.setVisibility(View.VISIBLE);
        for (Voucher voucher : vouchers) {
            RelativeLayout rly = new RelativeLayout(this);
            LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rlyParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(18), Utils.instence(this).dip2px(20), 0);
            rly.setLayoutParams(rlyParams);
            rly.setBackgroundResource(R.color.haha_white);

            TextView tvAmount = new TextView(this);
            tvAmount.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (voucher.status == 0) {
                tvAmount.setBackgroundResource(R.drawable.orange_vouvher);
            } else {
                tvAmount.setBackgroundResource(R.drawable.gray_vouvher);
            }
            tvAmount.setGravity(Gravity.CENTER);
            tvAmount.setText(Utils.getMoney(voucher.amount));
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
            tvAmount.setTextSize(20);
            TextPaint tp = tvAmount.getPaint();//加粗
            tp.setFakeBoldText(true);
            int tvAmountId = Utils.generateViewId();
            tvAmount.setId(tvAmountId);
            rly.addView(tvAmount);

            TextView tvTitle = new TextView(this);
            RelativeLayout.LayoutParams tvTitleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvTitleParams.setMargins(Utils.instence(this).dip2px(15), Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0);
            tvTitleParams.addRule(RelativeLayout.RIGHT_OF, tvAmountId);
            tvTitle.setLayoutParams(tvTitleParams);
            tvTitle.setMaxLines(2);
            tvTitle.setEllipsize(TextUtils.TruncateAt.END);
            tvTitle.setText(voucher.title);
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
            int tvTitleId = Utils.generateViewId();
            tvTitle.setId(tvTitleId);
            rly.addView(tvTitle);

            TextView tvExpiredAt = new TextView(this);
            RelativeLayout.LayoutParams tvExpiredAtParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvExpiredAtParams.addRule(RelativeLayout.ALIGN_LEFT, tvTitleId);
            tvExpiredAtParams.addRule(RelativeLayout.BELOW, tvTitleId);
            tvExpiredAtParams.setMargins(0, Utils.instence(this).dip2px(5), Utils.instence(this).dip2px(15), 0);
            tvExpiredAt.setLayoutParams(tvExpiredAtParams);
            tvExpiredAt.setText("有效期至 " + voucher.expired_at);
            tvExpiredAt.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            tvExpiredAt.setTextSize(12);
            rly.addView(tvExpiredAt);

            ImageView ivStatus = new ImageView(this);
            RelativeLayout.LayoutParams ivStatusParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ivStatusParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            ivStatusParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            ivStatusParams.setMargins(0, 0, Utils.instence(this).dip2px(15), 0);
            ivStatus.setLayoutParams(ivStatusParams);
            if (voucher.status == 0) {
                ivStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_unused));
            } else if (voucher.status == 1) {
                ivStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_used));
            } else if (voucher.status == 2) {
                ivStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_overdue));
            }
            rly.addView(ivStatus);

            mLlyVouchers.addView(rly);
        }
    }

    @Override
    public void showNoVoucher() {
        mIvNoVoucher.setVisibility(View.VISIBLE);
        mTvNoVoucher.setVisibility(View.VISIBLE);
        mTvVoucherRules.setVisibility(View.GONE);
    }

    @Override
    public void changeCustomerService() {

    }

    @Override
    public void startRefresh() {
        mSrlMain.setRefreshing(true);
    }

    @Override
    public void stopRefresh() {
        mSrlMain.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mSrlMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void clearVouchers() {
        mLlyVouchers.removeAllViews();
    }

    @Override
    public void onRefresh() {
        mPresenter.getVouchers();
    }
}
