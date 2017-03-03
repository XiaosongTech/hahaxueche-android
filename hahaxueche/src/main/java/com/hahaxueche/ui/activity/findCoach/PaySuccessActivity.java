package com.hahaxueche.ui.activity.findCoach;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.PaySuccessPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.PaySuccessView;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/10/13.
 */

public class PaySuccessActivity extends HHBaseActivity implements PaySuccessView {
    private PaySuccessPresenter mPresenter;
    @BindView(R.id.tv_coach_name)
    TextView mTvCoachName;
    @BindView(R.id.tv_pay_amount)
    TextView mTvPayAmount;
    @BindView(R.id.tv_pay_time)
    TextView mTvPayTime;
    @BindView(R.id.tv_order_code)
    TextView mTvOrderCode;
    @BindView(R.id.tv_customer_service)
    TextView mTvCustomerService;
    @BindView(R.id.frl_main)
    FrameLayout mFrlMain;
    @BindView(R.id.lly_insurance_pay)
    LinearLayout mLlyInsurance;
    @BindView(R.id.lly_coach_pay)
    LinearLayout mLlyCoach;
    @BindView(R.id.tv_sign)
    TextView mTvSign;
    @BindView(R.id.tv_insurance_amount)
    TextView mTvInsuranceAmount;
    @BindView(R.id.tv_insurance_pay_time)
    TextView mTvInsurancePayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new PaySuccessPresenter();
        Intent intent = getIntent();
        mPresenter.isFromPurchaseInsurance = intent != null && intent.getBooleanExtra("isFromPurchaseInsurance", false);
        mPresenter.isPurchasedInsurance = intent != null && intent.getBooleanExtra("isPurchasedInsurance", false);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        initActionBar();
        changeCustomerService();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mIvBack.setVisibility(View.GONE);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("付款成功");
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick(R.id.tv_sign)
    public void sign() {
        Intent intent = new Intent();
        intent.putExtra("isPurchasedInsurance", mPresenter.isPurchasedInsurance);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    public void loadPayInfo(Coach coach, PurchasedService ps) {
        mTvCoachName.setText(coach.name);
        mTvPayTime.setText(Utils.getDateFromUTC(ps.paid_at));
        mTvPayAmount.setText(Utils.getMoney(ps.actual_amount));
        mTvOrderCode.setText(ps.order_no);
    }

    public void changeCustomerService() {
        String customerService = mTvCustomerService.getText().toString();
        SpannableString spCustomerServiceStr = new SpannableString(customerService);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    // Android version is lesser than 6.0 or the permission is already granted.
                    contactService();
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                ds.setUnderlineText(true);
                ds.clearShadowLayer();
            }
        }, customerService.indexOf("400"), customerService.indexOf("6006") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.app_theme_color)),
                customerService.indexOf("400"), customerService.indexOf("6006") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mPresenter.onlineAsk();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                ds.setUnderlineText(true);
                ds.clearShadowLayer();
            }
        }, customerService.indexOf("在线客服"), customerService.indexOf("在线客服") + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spCustomerServiceStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.app_theme_color)),
                customerService.indexOf("在线客服"), customerService.indexOf("在线客服") + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvCustomerService.setText(spCustomerServiceStr);
        mTvCustomerService.setHighlightColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
        mTvCustomerService.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 联系客服
     */
    private void contactService() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:4000016006"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mFrlMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showCoachPayView() {
        mLlyCoach.setVisibility(View.VISIBLE);
        mLlyInsurance.setVisibility(View.GONE);
    }

    @Override
    public void showInsurancePayView() {
        mLlyCoach.setVisibility(View.GONE);
        mLlyInsurance.setVisibility(View.VISIBLE);
    }

    @Override
    public void setSignText(String text) {
        mTvSign.setText(text);
    }

    @Override
    public void setInsuranceAmount(int amount) {
        mTvInsuranceAmount.setText(Utils.getMoney(amount));
    }

    @Override
    public void setInsurancePaidAt(String paidAt) {
        mTvInsurancePayTime.setText(paidAt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        }
    }
}
