package com.hahaxueche.ui.activity.myPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.presenter.myPage.PurchaseInsurancePresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.PurchaseInsuranceView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;
import com.pingplusplus.android.Pingpp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2017/2/25.
 */

public class PurchaseInsuranceActivity extends HHBaseActivity implements PurchaseInsuranceView {
    private PurchaseInsurancePresenter mPresenter;
    @BindView(R.id.tv_amount)
    TextView mTvAmount;
    @BindView(R.id.tv_notice)
    TextView mTvNotice;
    @BindView(R.id.lly_purchase)
    LinearLayout mLlyPurchase;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    private int[] selectIds = new int[4];
    private int selectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PurchaseInsurancePresenter();
        setContentView(R.layout.activity_purchase_insurance);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        Intent intent = getIntent();
        mPresenter.setInsuranceType(intent.getIntExtra("insuranceType", Common.PURCHASE_INSURANCE_TYPE_169));
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("购买赔付宝");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseInsuranceActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.tv_sure_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure_pay:
                mPresenter.createCharge();
                break;
            default:
                break;
        }
    }

    @Override
    public void setPayAmount(String text) {
        mTvAmount.setText(text);
    }

    @Override
    public void setNotice(String text) {
        mTvNotice.setText(text);
    }

    @Override
    public void loadPaymentMethod(ArrayList<PaymentMethod> paymentMethods) {
        if (paymentMethods == null || paymentMethods.size() < 1) return;
        for (PaymentMethod paymentMethod : paymentMethods) {
            mLlyPurchase.addView(getPaymentAdapter(paymentMethod, paymentMethods.indexOf(paymentMethod)), 1 + paymentMethods.indexOf(paymentMethod));
        }
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void paySuccess() {
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    public void callPingpp(String result) {
        HHLog.v(result);
        Pingpp.createPayment(this, result);
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
                    mPresenter.getStudentUtilHasInsurance();
                } else if (result.equals("cancel")) {
                    showMessage("取消支付");
                } else if (result.equals("invalid")) {
                    showMessage("支付插件未安装");
                } else {
                    showMessage("支付失败");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
