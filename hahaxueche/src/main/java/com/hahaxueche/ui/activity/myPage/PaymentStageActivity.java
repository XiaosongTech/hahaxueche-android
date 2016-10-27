package com.hahaxueche.ui.activity.myPage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.hahaxueche.util.HHLog;
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
            int pos = ps.payment_stages.indexOf(paymentStage);
            mLlyPaymentStages.addView(getPaymentStageAdapter(paymentStage, pos, ps.current_payment_stage), pos);
        }
    }

    private View getPaymentStageAdapter(PaymentStage paymentStage, int position, int currentPaymentStage) {
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

        //序号
        TextView tvStageNumber = new TextView(this);
        RelativeLayout.LayoutParams tvStageNumberParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvStageNumberParam.setMargins(Utils.instence(this).dip2px(15), Utils.instence(this).dip2px(15), 0, Utils.instence(this).dip2px(15));
        tvStageNumber.setLayoutParams(tvStageNumberParam);
        tvStageNumber.setText(String.valueOf(paymentStage.stage_number));
        tvStageNumber.setGravity(Gravity.CENTER);
        if (TextUtils.isEmpty(paymentStage.paid_at)) {//待打款
            if (paymentStage.stage_number == currentPaymentStage) {
                //当前阶段
                tvStageNumber.setBackgroundResource(R.drawable.circle_stage_number_orange);
                tvStageNumber.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            } else {
                tvStageNumber.setBackgroundResource(R.drawable.circle_stage_number_gray);
                tvStageNumber.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            }
        } else {
            //已打款
            tvStageNumber.setBackgroundResource(R.drawable.circle_stage_number_white);
            tvStageNumber.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        }
        int tvStageNumberId = Utils.generateViewId();
        tvStageNumber.setId(tvStageNumberId);
        rlyPaymentStage.addView(tvStageNumber);

        //费用名称
        TextView tvStageName = new TextView(this);
        RelativeLayout.LayoutParams tvStageNameParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvStageNameParam.addRule(RelativeLayout.RIGHT_OF, tvStageNumberId);
        tvStageNameParam.setMargins(Utils.instence(this).dip2px(15), 0, 0, 0);
        tvStageNameParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvStageName.setLayoutParams(tvStageNameParam);
        tvStageName.setText(paymentStage.stage_name);
        if (TextUtils.isEmpty(paymentStage.paid_at)) {
            if (paymentStage.stage_number == currentPaymentStage) {
                tvStageName.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            } else {
                tvStageName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            }
        } else {
            tvStageName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        }
        int tvStageNameId = Utils.generateViewId();
        tvStageName.setId(tvStageNameId);
        rlyPaymentStage.addView(tvStageName);

        //金额
        TextView tvStageAmount = new TextView(this);
        RelativeLayout.LayoutParams tvStageAmountParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvStageAmountParam.addRule(RelativeLayout.RIGHT_OF, tvStageNameId);
        tvStageAmountParam.setMargins(Utils.instence(this).dip2px(15), 0, 0, 0);
        tvStageAmountParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvStageAmount.setLayoutParams(tvStageAmountParam);
        tvStageAmount.setText(Utils.getMoney(paymentStage.stage_amount));
        if (TextUtils.isEmpty(paymentStage.paid_at)) {
            if (paymentStage.stage_number == currentPaymentStage) {
                tvStageAmount.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            } else {
                tvStageAmount.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            }
        } else {
            tvStageAmount.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        }
        rlyPaymentStage.addView(tvStageAmount);

        //状态
        TextView tvState = new TextView(this);
        RelativeLayout.LayoutParams tvStateParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvStateParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        tvStateParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        tvStateParam.setMargins(0, 0, Utils.instence(this).dip2px(40), 0);
        tvState.setLayoutParams(tvStateParam);
        if (TextUtils.isEmpty(paymentStage.paid_at)) {
            tvState.setText("待打款");
            if (paymentStage.stage_number == currentPaymentStage) {
                tvState.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            } else {
                tvState.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            }
        } else {
            tvState.setText("已打款");
            tvState.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        }
        int tvStateId = Utils.generateViewId();
        tvState.setId(tvStateId);
        rlyPaymentStage.addView(tvState);

        //付款时间
        if (!TextUtils.isEmpty(paymentStage.paid_at)) {
            TextView tvPaidAt = new TextView(this);
            RelativeLayout.LayoutParams tvPaidAtParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvPaidAtParam.addRule(RelativeLayout.LEFT_OF, tvStateId);
            tvPaidAtParam.setMargins(0, 0, Utils.instence(this).dip2px(15), 0);
            tvPaidAtParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            tvPaidAt.setLayoutParams(tvPaidAtParam);
            tvPaidAt.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
            tvPaidAt.setText(Utils.getMonthDayFromUTC(paymentStage.paid_at));
            rlyPaymentStage.addView(tvPaidAt);
        }

        //button
        ImageView ivMessage = new ImageView(this);
        RelativeLayout.LayoutParams ivMessageParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ivMessageParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        ivMessageParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        ivMessageParam.setMargins(0, 0, Utils.instence(this).dip2px(15), 0);
        ivMessage.setLayoutParams(ivMessageParam);
        ivMessage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_paylist_message_btn));
        rlyPaymentStage.addView(ivMessage);

        return rlyPaymentStage;
    }

    @Override
    public void enablePayStage(boolean enable) {
        if (enable) {
            mLlyPayStage.setVisibility(View.VISIBLE);
            mTvCongratulation.setVisibility(View.GONE);
        } else {
            mLlyPayStage.setVisibility(View.GONE);
            mTvCongratulation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setCurrentPayAmountText(Spanned text) {
        mTvCurrentPayAmount.setText(text);
    }
}
