package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.base.Field;
import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.PurchaseCoachPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.activity.myPage.SelectVoucherActivity;
import com.hahaxueche.ui.view.findCoach.PurchaseCoachView;
import com.hahaxueche.ui.widget.scoreView.ScoreView;
import com.hahaxueche.util.DistanceUtil;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/12.
 */

public class PurchaseCoachActivity extends HHBaseActivity implements PurchaseCoachView {
    private PurchaseCoachPresenter mPresenter;
    @BindView(R.id.iv_coach_avatar)
    SimpleDraweeView mIvCoachAvatar;
    @BindView(R.id.tv_coach_name)
    TextView mTvCoachName;
    @BindView(R.id.iv_is_golden_coach)
    ImageView mIvIsGoldenCoach;
    @BindView(R.id.iv_is_cash_pledge)
    ImageView mIvCashPledge;
    @BindView(R.id.tv_train_school)
    TextView mTvTrainSchool;
    @BindView(R.id.lly_train_school)
    LinearLayout mLlyTrainSchool;
    @BindView(R.id.tv_coach_teach_time)
    TextView mTvCoachTeachTime;
    @BindView(R.id.sv_coach_score)
    ScoreView mSvCoachScore;
    @BindView(R.id.tv_coach_location)
    TextView mTvCoachLocation;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_applaud_count)
    TextView mTvApplaudCount;
    @BindView(R.id.lly_purchase)
    LinearLayout mLlyPurchase;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.tv_C1)
    TextView mTvC1;
    @BindView(R.id.tv_C2)
    TextView mTvC2;
    @BindView(R.id.tv_normal)
    TextView mTvNormal;
    @BindView(R.id.tv_vip)
    TextView mTvVip;
    @BindView(R.id.tv_total_amount)
    TextView mTvTotalAmount;
    @BindView(R.id.rly_voucher)
    RelativeLayout mRlyVoucher;
    @BindView(R.id.tv_voucher_title)
    TextView mTvVoucherTitle;
    @BindView(R.id.tv_voucher_amount)
    TextView mTvVoucherAmount;
    @BindView(R.id.iv_more_voucher)
    ImageView mIvMoreVoucher;
    @BindView(R.id.tv_total_amount_text)
    TextView mTvTotalAmountText;
    @BindView(R.id.tv_total_amount_label)
    TextView mTvTotalAmountLabel;

    private HHBaseApplication application;
    private int[] selectIds = new int[3];
    private int selectId;
    private static final int REQUEST_CODE_SELECT_VOUCHERS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PurchaseCoachPresenter();
        application = HHBaseApplication.get(getContext());
        setContentView(R.layout.activity_purchase_coach);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        if (intent.getParcelableExtra("coach") != null) {
            mPresenter.setCoach((Coach) intent.getParcelableExtra("coach"));
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("购买教练");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseCoachActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.tv_sure_pay,
            R.id.tv_C1,
            R.id.tv_C2,
            R.id.tv_normal,
            R.id.tv_vip,
            R.id.rly_voucher})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure_pay:
                mPresenter.createCharge();
                break;
            case R.id.tv_C1:
                mPresenter.selectLicenseC1();
                break;
            case R.id.tv_C2:
                mPresenter.selectLicenseC2();
                break;
            case R.id.tv_normal:
                mPresenter.selectClassNormal();
                break;
            case R.id.tv_vip:
                mPresenter.selectClassVip();
                break;
            case R.id.rly_voucher:
                Intent intent = new Intent(getContext(), SelectVoucherActivity.class);
                intent.putParcelableArrayListExtra("voucherList", mPresenter.getVoucherList());
                startActivityForResult(intent, REQUEST_CODE_SELECT_VOUCHERS);
                break;
            default:
                break;
        }
    }

    @Override
    public void loadCoachInfo(Coach coach) {
        mIvCoachAvatar.setImageURI(coach.avatar);
        mTvCoachName.setText(coach.name);
        mIvIsGoldenCoach.setVisibility(coach.skill_level.equals("1") ? View.VISIBLE : View.GONE);
        mIvCashPledge.setVisibility(coach.has_cash_pledge == 1 ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(coach.driving_school)) {
            mLlyTrainSchool.setVisibility(View.VISIBLE);
            mTvTrainSchool.setText(coach.driving_school);
        } else {
            mLlyTrainSchool.setVisibility(View.GONE);
        }
        mTvCoachTeachTime.setText(coach.experiences + "年教龄");
        //综合得分
        float averageRating = 0;
        if (!TextUtils.isEmpty(coach.average_rating)) {
            averageRating = Float.parseFloat(coach.average_rating);
        }
        if (averageRating > 5) {
            averageRating = 5;
        }
        mSvCoachScore.setScore(averageRating, true);
        mTvCoachLocation.setText(application.getConstants().getSectionName(coach.coach_group.field_id));
        final Field myField = application.getConstants().getField(coach.coach_group.field_id);
        if (application.getMyLocation() != null && myField != null) {
            String kmString = DistanceUtil.getDistanceKm(application.getMyLocation().lng, application.getMyLocation().lat, myField.lng, myField.lat);
            String infoText = "距您" + kmString + "km";
            SpannableStringBuilder style = new SpannableStringBuilder(infoText);
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.app_theme_color)), 2, 2 + kmString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mTvDistance.setText(style);
        }
        mTvApplaudCount.setText(String.valueOf(coach.liked));
    }

    @Override
    public void loadPaymentMethod(ArrayList<PaymentMethod> paymentMethods) {
        if (paymentMethods == null || paymentMethods.size() < 1) return;
        for (PaymentMethod paymentMethod : paymentMethods) {
            mLlyPurchase.addView(getPaymentAdapter(paymentMethod, paymentMethods.indexOf(paymentMethod)), 1 + paymentMethods.indexOf(paymentMethod));
        }
    }

    @Override
    public void showLicenseC1() {
        mTvC1.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLicenseC2() {
        mTvC2.setVisibility(View.VISIBLE);
    }

    @Override
    public void showClassVIP() {
        mTvVip.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideClassVIP() {
        mTvVip.setVisibility(View.GONE);
    }

    @Override
    public void selectLicenseC1() {
        mTvC1.setBackgroundResource(R.drawable.rect_bg_appcolor_sm);
        mTvC1.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
    }

    @Override
    public void selectLicenseC2() {
        mTvC2.setBackgroundResource(R.drawable.rect_bg_appcolor_sm);
        mTvC2.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
    }

    @Override
    public void unSelectLicense() {
        mTvC1.setBackgroundResource(R.drawable.rect_bg_gray_sm);
        mTvC1.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        mTvC2.setBackgroundResource(R.drawable.rect_bg_gray_sm);
        mTvC2.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
    }

    @Override
    public void selectClassNormal() {
        mTvNormal.setBackgroundResource(R.drawable.rect_bg_appcolor_sm);
        mTvNormal.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
    }

    @Override
    public void selectClassVip() {
        mTvVip.setBackgroundResource(R.drawable.rect_bg_appcolor_sm);
        mTvVip.setTextColor(ContextCompat.getColor(this, R.color.haha_white));
    }

    @Override
    public void unSelectClass() {
        mTvNormal.setBackgroundResource(R.drawable.rect_bg_gray_sm);
        mTvNormal.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        mTvVip.setBackgroundResource(R.drawable.rect_bg_gray_sm);
        mTvVip.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
    }

    private RelativeLayout getPaymentAdapter(final PaymentMethod paymentMethod, int i) {
        RelativeLayout rly = new RelativeLayout(this);
        rly.setBackgroundResource(R.color.haha_white);
        LinearLayout.LayoutParams rlyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rly.setLayoutParams(rlyParams);

        if (i != 0) {
            View view = new View(this);
            RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_width));
            viewParams.setMargins(Utils.instence(this).dip2px(20), 0, 0, 0);
            view.setLayoutParams(viewParams);
            view.setBackgroundResource(R.color.haha_gray_divider);
            rly.addView(view);
        }

        ImageView mIvLogo = new ImageView(this);
        RelativeLayout.LayoutParams ivLogoParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ivLogoParams.setMargins(Utils.instence(this).dip2px(20), Utils.instence(this).dip2px(15), 0, 0);
        mIvLogo.setLayoutParams(ivLogoParams);
        mIvLogo.setImageDrawable(ContextCompat.getDrawable(this, paymentMethod.drawableLogo));
        int ivLogoId = Utils.generateViewId();
        mIvLogo.setId(ivLogoId);
        rly.addView(mIvLogo);


        TextView tvName = new TextView(this);
        RelativeLayout.LayoutParams tvNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvNameParams.setMargins(Utils.instence(this).dip2px(15), 0, 0, 0);
        tvNameParams.addRule(RelativeLayout.ALIGN_TOP, ivLogoId);
        tvNameParams.addRule(RelativeLayout.RIGHT_OF, ivLogoId);
        tvName.setLayoutParams(tvNameParams);
        tvName.setTextSize(16);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
        tvName.setText(paymentMethod.name);
        int tvNameId = Utils.generateViewId();
        tvName.setId(tvNameId);
        rly.addView(tvName);

        TextView tvRemark = new TextView(this);
        RelativeLayout.LayoutParams tvRemarkParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvRemarkParams.setMargins(0, Utils.instence(this).dip2px(6), 0, Utils.instence(this).dip2px(15));
        tvRemarkParams.addRule(RelativeLayout.ALIGN_LEFT, tvNameId);
        tvRemarkParams.addRule(RelativeLayout.BELOW, tvNameId);
        tvRemark.setLayoutParams(tvRemarkParams);
        tvRemark.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
        tvRemark.setTextSize(12);
        tvRemark.setText(paymentMethod.remark);
        rly.addView(tvRemark);

        ImageView mIvSelect = new ImageView(this);
        RelativeLayout.LayoutParams ivSelectParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ivSelectParams.setMargins(0, 0, Utils.instence(this).dip2px(20), 0);
        ivSelectParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        ivSelectParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mIvSelect.setLayoutParams(ivSelectParams);
        mIvSelect.setImageDrawable(ContextCompat.getDrawable(this, i == 0 ? R.drawable.ic_cashout_chack_btn : R.drawable.ic_cashout_unchack_btn));
        final int ivSelectId = Utils.generateViewId();
        mIvSelect.setId(ivSelectId);
        rly.addView(mIvSelect);
        selectIds[i] = ivSelectId;
        rly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectId = ivSelectId;
                selectPayments();
                mPresenter.setPaymentMethod(paymentMethod.id);
            }
        });
        return rly;
    }

    public void selectPayments() {
        for (int id : selectIds) {
            ImageView ivSelect = ButterKnife.findById(this, id);
            if (id == selectId) {
                ivSelect.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cashout_chack_btn));
            } else {
                ivSelect.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cashout_unchack_btn));
            }
        }
    }


    @Override
    public void setTotalAmountText(String text) {
        mTvTotalAmountLabel.setVisibility(View.GONE);
        mTvTotalAmountText.setVisibility(View.GONE);
        mTvTotalAmount.setText(text);
    }

    @Override
    public void setTotalAmountWithVoucher(String voucherText, String amountText) {
        mTvTotalAmountLabel.setVisibility(View.VISIBLE);
        mTvTotalAmountText.setVisibility(View.VISIBLE);
        mTvTotalAmountText.setText(voucherText);
        mTvTotalAmount.setText(amountText);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void callPingpp(String charge) {
        HHLog.v(charge);
        Pingpp.createPayment(this, charge);
    }

    @Override
    public void paySuccess() {
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    public void setVoucherSelectable(boolean select) {
        if (select) {//代金券可选择
            mIvMoreVoucher.setVisibility(View.VISIBLE);
            mRlyVoucher.setClickable(true);
        } else {
            mIvMoreVoucher.setVisibility(View.INVISIBLE);
            mRlyVoucher.setClickable(false);
        }
    }

    @Override
    public void showSelectVoucher(boolean isShow) {
        mRlyVoucher.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setVoucher(Voucher voucher) {
        mTvVoucherAmount.setText("-" + Utils.getMoney(voucher.amount));
        mTvVoucherTitle.setText(voucher.title);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                /* 处理返回值
                 * "success" - 支付成功
                 * "fail"    - 支付失败
                 * "cancel"  - 取消支付
                 * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
                 */
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息

                if (result.equals("success")) {
                    mPresenter.getStudentUtilHasCoach();
                } else if (result.equals("cancel")) {
                    showMessage("取消支付");
                } else if (result.equals("invalid")) {
                    showMessage("支付插件未安装");
                } else {
                    showMessage("支付失败");
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_VOUCHERS) {
            if (resultCode == RESULT_OK) {
                ArrayList<Voucher> vouchers = data.getParcelableArrayListExtra("voucherList");
                mPresenter.setVoucherList(vouchers);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
