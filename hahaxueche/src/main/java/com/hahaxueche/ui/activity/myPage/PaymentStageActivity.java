package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.R;
import com.hahaxueche.model.payment.PaymentStage;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.myPage.PaymentStagePresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.PaymentStageView;
import com.hahaxueche.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class PaymentStageActivity extends HHBaseActivity implements PaymentStageView {
    private PaymentStagePresenter mPresenter;
    private ImageView mIvBack;
    private TextView mTvTitle;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.tv_coach_name)
    TextView mTvCoachName;
    @BindView(R.id.iv_coach_avatar)
    SimpleDraweeView mIvCoachAvatar;
    @BindView(R.id.tv_paid_at)
    TextView mTvPaidAt;
    @BindView(R.id.tv_order)
    TextView mTvOrder;
    @BindView(R.id.tv_total_amount)
    TextView mTvTotalAmount;
    @BindView(R.id.tv_paid_amount)
    TextView mTvPaidAmount;
    @BindView(R.id.tv_unpaid_amount)
    TextView mTvUnpaidAmount;
    @BindView(R.id.lly_pay_stage)
    LinearLayout mLlyPayStage;
    @BindView(R.id.tv_current_pay_amount)
    TextView mTvCurrentPayAmount;
    @BindView(R.id.tv_congratulation)
    TextView mTvCongratulation;
    @BindView(R.id.lly_payment_stages)
    LinearLayout mLlyPaymentStages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PaymentStagePresenter();
        setContentView(R.layout.activity_payment_stage);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        mPresenter.fetchCoachInfo();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("打款状态");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentStageActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setCoachInfo(Coach coach) {
        mTvCoachName.setText(coach.name);
        mIvCoachAvatar.setImageURI(coach.avatar);
    }

    @Override
    public void showPs(PurchasedService ps) {
        mTvPaidAt.setText(Utils.getDateFromUTC(ps.paid_at));
        mTvOrder.setText(ps.order_no);
        mTvTotalAmount.setText(Utils.getMoney(ps.total_amount));
        mTvPaidAmount.setText(Utils.getMoney(ps.paid_amount));
        mTvUnpaidAmount.setText(Utils.getMoney(ps.unpaid_amount));
        for (PaymentStage paymentStage : ps.payment_stages) {
            mLlyPaymentStages.addView(getPaymentStageAdapter(paymentStage, ps.payment_stages.indexOf(paymentStage)));
        }
    }

    private View getPaymentStageAdapter(PaymentStage paymentStage, int position) {
        RelativeLayout rlyPaymentStage = new RelativeLayout(this);
        rlyPaymentStage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        rlyPaymentStage.setBackgroundResource(R.color.haha_white);

        if (position != 0) {//除了第一行，加分割线
            View dividerView = new View(this);
            RelativeLayout.LayoutParams dividerViewParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelSize(R.dimen.divider_width));
            dividerViewParam.setMargins(Utils.instence(this).dip2px(15), 0, 0, 0);
            dividerView.setLayoutParams(dividerViewParam);
            dividerView.setBackgroundResource(R.color.haha_gray_divider);
            rlyPaymentStage.addView(dividerView);
        }

        TextView tvStageNumber = new TextView(this);
        RelativeLayout.LayoutParams tvStageNumberParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvStageNumberParam.setMargins(Utils.instence(this).dip2px(15), Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));

        return rlyPaymentStage;

    }

    @Override
    public void enablePayStage(boolean enable) {
        if (enable) {
            mLlyPayStage.setVisibility(View.VISIBLE);
            mTvCurrentPayAmount.setVisibility(View.GONE);
        } else {
            mLlyPayStage.setVisibility(View.GONE);
            mTvCurrentPayAmount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setCurrentPayAmountText(Spanned text) {
        mTvCurrentPayAmount.setText(text);
    }
}
