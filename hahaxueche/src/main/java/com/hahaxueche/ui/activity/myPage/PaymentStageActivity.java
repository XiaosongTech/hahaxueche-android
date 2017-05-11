package com.hahaxueche.ui.activity.myPage;

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
import com.hahaxueche.ui.dialog.ShareAppDialog;
import com.hahaxueche.ui.dialog.myPage.PaymentStageInfoDialog;
import com.hahaxueche.ui.dialog.myPage.ReviewDialog;
import com.hahaxueche.ui.dialog.myPage.TransferConfirmDialog;
import com.hahaxueche.ui.view.myPage.PaymentStageView;
import com.hahaxueche.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/26.
 */

public class PaymentStageActivity extends HHBaseActivity implements PaymentStageView {
    private PaymentStagePresenter mPresenter;
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
    private ShareAppDialog mShareDialog;

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
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
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
        mLlyPaymentStages.removeAllViews();
        for (PaymentStage paymentStage : ps.payment_stages) {
            int pos = ps.payment_stages.indexOf(paymentStage);
            mLlyPaymentStages.addView(getPaymentStageAdapter(paymentStage, pos, ps.current_payment_stage), pos);
        }
        TextView tvHints = new TextView(this);
        LinearLayout.LayoutParams tvHintsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = Utils.instence(this).dip2px(20);
        tvHintsParams.setMargins(margin, margin, margin, margin);
        tvHints.setLayoutParams(tvHintsParams);
        tvHints.setLineSpacing(0, 1.2f);
        tvHints.setText(getResources().getString(R.string.payment_stage_hints));
        tvHints.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_text));
        tvHints.setTextSize(12);
        mLlyPaymentStages.addView(tvHints);
    }

    private View getPaymentStageAdapter(final PaymentStage paymentStage, int position, int currentPaymentStage) {
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
        tvStateParam.setMargins(0, 0, Utils.instence(this).dip2px(55), 0);
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

        //right button
        if (TextUtils.isEmpty(paymentStage.paid_at) || !paymentStage.reviewable) {
            ImageView ivMessage = new ImageView(this);
            RelativeLayout.LayoutParams ivMessageParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            ivMessageParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            ivMessageParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            ivMessageParam.setMargins(0, 0, Utils.instence(this).dip2px(15), 0);
            ivMessage.setLayoutParams(ivMessageParam);
            ivMessage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_paylist_message_btn));
            ivMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.addDataTrack("pay_coach_status_page_i_tapped", getContext());
                    PaymentStageInfoDialog dialog = new PaymentStageInfoDialog(getContext(), paymentStage.stage_name, paymentStage.description, !TextUtils.isEmpty(paymentStage.paid_at));
                    dialog.show();
                }
            });
            rlyPaymentStage.addView(ivMessage);
        } else {
            if (paymentStage.reviewed) {
                //已评价
                TextView tvReviewded = new TextView(this);
                RelativeLayout.LayoutParams tvReviewdedParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvReviewdedParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                tvReviewdedParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                tvReviewdedParam.setMargins(0, 0, Utils.instence(this).dip2px(10), 0);
                tvReviewded.setLayoutParams(tvReviewdedParam);
                tvReviewded.setText("已评价");
                tvReviewded.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
                tvReviewded.setGravity(Gravity.CENTER);
                tvReviewded.setBackgroundResource(R.drawable.rect_bg_gray_divider_corner);
                tvReviewded.setTextSize(10);
                rlyPaymentStage.addView(tvReviewded);
            } else {
                //待评价
                TextView tvForReview = new TextView(this);
                RelativeLayout.LayoutParams tvForReviewParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                tvForReviewParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                tvForReviewParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                tvForReviewParam.setMargins(0, 0, Utils.instence(this).dip2px(10), 0);
                tvForReview.setLayoutParams(tvForReviewParam);
                tvForReview.setText("待评价");
                tvForReview.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
                tvForReview.setGravity(Gravity.CENTER);
                tvForReview.setBackgroundResource(R.drawable.rect_bg_appcolor_corner);
                tvForReview.setTextSize(10);
                tvForReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showReview(false, paymentStage, false);
                    }
                });
                rlyPaymentStage.addView(tvForReview);
            }
        }

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

    @Override
    public void showShareAppDialog() {
        if (mShareDialog == null) {
            String shareText = getResources().getString(R.string.upload_share_dialog_text);
            mShareDialog = new ShareAppDialog(getContext(), shareText, true, null);
        }
        mShareDialog.show();
    }

    @OnClick({R.id.tv_sure_transfer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure_transfer:
                mPresenter.addDataTrack("pay_coach_status_page_pay_coach_tapped", getContext());
                TransferConfirmDialog dialog = new TransferConfirmDialog(this, mPresenter.getCurrentPaymentStage().description
                        , new TransferConfirmDialog.OnBtnClickListener() {
                    @Override
                    public void onTransfer() {
                        mPresenter.pay();
                    }
                });
                dialog.show();
                break;
            default:
                break;
        }
    }

    /**
     * 显示评价dialog
     *
     * @param isShowTitle
     */
    @Override
    public void showReview(boolean isShowTitle, PaymentStage paymentStage, final boolean isShowShare) {
        ReviewDialog reviewDialog = new ReviewDialog(this, isShowTitle, String.valueOf(paymentStage.stage_number), paymentStage.coach_user_id, paymentStage.stage_name, new ReviewDialog.OnBtnClickListener() {
            @Override
            public void onReview(String review, float score, String paymentStageNumber, String coachUserId) {
                mPresenter.makeReview(paymentStageNumber, String.valueOf(score), review);
            }

            @Override
            public void onCancel() {
                if (isShowShare) {
                    showShareAppDialog();
                }
            }
        });
        reviewDialog.show();
    }
}
