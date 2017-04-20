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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.FixedCostItem;
import com.hahaxueche.model.payment.OtherFee;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.findCoach.ClassTypeIntroPresenter;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.findCoach.ClassTypeIntroView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.RequestCode;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2017/3/9.
 */

public class ClassTypeIntroActivity extends HHBaseActivity implements ClassTypeIntroView {
    private ClassTypeIntroPresenter mPresenter;
    @BindView(R.id.lly_main)
    LinearLayout mLlyMain;
    @BindView(R.id.tv_customer_service)
    TextView mTvCustomerService;
    @BindView(R.id.lly_fixed_fees)
    LinearLayout mLlyFixedFees;
    @BindView(R.id.lly_other_fees)
    LinearLayout mLlyOtherFees;
    @BindView(R.id.lly_normal_service)
    LinearLayout mLlyNormalService;
    @BindView(R.id.lly_vip_service)
    LinearLayout mLlyVIPService;
    @BindView(R.id.lly_wuyou)
    LinearLayout mLlyWuyouService;
    @BindView(R.id.tv_training_cost)
    TextView mTvTrainingCost;
    @BindView(R.id.lly_insurance)
    LinearLayout mLlyInsurance;
    @BindView(R.id.tv_total_amount)
    TextView mTvTotalAmount;
    @BindView(R.id.iv_dash1)
    ImageView mIvDash1;
    @BindView(R.id.iv_dash2)
    ImageView mIvDash2;
    @BindView(R.id.iv_dash3)
    ImageView mIvDash3;
    @BindView(R.id.iv_dash4)
    ImageView mIvDash4;
    @BindView(R.id.iv_dash5)
    ImageView mIvDash5;
    @BindView(R.id.iv_dash6)
    ImageView mIvDash6;
    @BindView(R.id.lly_moni)
    LinearLayout mLlyMoni;
    @BindView(R.id.tv_pay)
    TextView mTvPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ClassTypeIntroPresenter();
        setContentView(R.layout.activity_class_type_intro);
        ButterKnife.bind(this);
        mPresenter.attachView(this);
        Intent intent = getIntent();
        if (intent.getParcelableExtra("coach") != null) {
            mPresenter.setFeeDetail(intent.getIntExtra("totalAmount", 0),
                    (ClassType) intent.getParcelableExtra("classType"),
                    (Coach) intent.getParcelableExtra("coach"),
                    intent.getBooleanExtra("isShowPurchase", true));
        }
        initActionBar();
        initCustomerService();
        mIvDash1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mIvDash2.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mIvDash3.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mIvDash4.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mIvDash5.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mIvDash6.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        ImageView mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        TextView mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTvTitle.setText("班别介绍");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassTypeIntroActivity.this.finish();
            }
        });
    }

    private void initCustomerService() {
        String customerService = mTvCustomerService.getText().toString();
        SpannableString spCustomerServiceStr = new SpannableString(customerService);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE);
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

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.tv_pay,
            R.id.tv_prepay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pay:
                mPresenter.addDataTrack("price_detail_page_deposit_tapped", getContext());
                Intent intent = new Intent();
                intent.putExtra("classType", mPresenter.mClassType);
                setResult(RESULT_OK, intent);
                ClassTypeIntroActivity.this.finish();
                break;
            case R.id.tv_prepay:
                mPresenter.addDataTrack("price_detail_page_purchase_tapped", getContext());
                intent = new Intent();
                intent.putExtra("prepay", true);
                setResult(RESULT_OK, intent);
                ClassTypeIntroActivity.this.finish();
                break;
            default:
                break;
        }
    }

    public void showMessage(String message) {
        Snackbar.make(mLlyMain, message, Snackbar.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RequestCode.PERMISSIONS_REQUEST_CELL_PHONE_FOR_CUSTOMER_SERVICE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        }
    }

    @Override
    public void setFixedFees(ArrayList<FixedCostItem> fixedFees) {
        if (fixedFees == null || fixedFees.size() < 1) return;
        int length10 = Utils.instence(this).dip2px(10);
        for (FixedCostItem fixedCostItem : fixedFees) {
            LinearLayout llyFixedCostItem = new LinearLayout(this);
            LinearLayout.LayoutParams llyFixedCostItemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llyFixedCostItemParams.setMargins(0, length10, 0, 0);
            llyFixedCostItem.setLayoutParams(llyFixedCostItemParams);
            llyFixedCostItem.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvFeeName = new TextView(this);
            tvFeeName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tvFeeName.setText(fixedCostItem.name);
            tvFeeName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray_dark));
            tvFeeName.setTextSize(16);
            llyFixedCostItem.addView(tvFeeName);

            ImageView ivDash = new ImageView(this);
            LinearLayout.LayoutParams ivDashParams = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            ivDashParams.setMargins(length10, 0, length10, 0);
            ivDashParams.gravity = Gravity.CENTER_VERTICAL;
            ivDash.setLayoutParams(ivDashParams);
            ivDash.setBackgroundResource(R.drawable.divider_dash_line);
            ivDash.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            llyFixedCostItem.addView(ivDash);

            TextView tvFeeCost = new TextView(this);
            tvFeeCost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tvFeeCost.setText(Utils.getMoney(fixedCostItem.cost));
            tvFeeCost.setTextColor(ContextCompat.getColor(this, R.color.app_theme_color));
            tvFeeCost.setTextSize(16);
            llyFixedCostItem.addView(tvFeeCost);

            mLlyFixedFees.addView(llyFixedCostItem);
        }
    }

    @Override
    public void setOtherFees(ArrayList<OtherFee> otherFees, boolean isForceInsurance, int coachGroupType) {
        if (otherFees == null || otherFees.size() < 1) return;
        int insertLine = 0;
        for (OtherFee otherFee : otherFees) {
            if (isForceInsurance && otherFee.name.contains("补考费"))
                continue;
            if (coachGroupType == Common.GROUP_TYPE_CHEYOU_WUYOU && otherFee.name.contains("模拟费"))
                continue;
            FrameLayout frTitle = new FrameLayout(this);
            LinearLayout.LayoutParams frTitleParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            frTitleParam.setMargins(0, Utils.instence(this).dip2px(10), 0, 0);
            frTitle.setLayoutParams(frTitleParam);

            FrameLayout frIcon = new FrameLayout(this);
            FrameLayout.LayoutParams frIconParam = new FrameLayout.LayoutParams(Utils.instence(this).dip2px(5), Utils.instence(this).dip2px(18));
            frIconParam.gravity = Gravity.CENTER_VERTICAL;
            frIcon.setLayoutParams(frIconParam);
            frIcon.setBackgroundResource(R.drawable.rect_bg_appcolor);
            frTitle.addView(frIcon);

            TextView tvFeeName = new TextView(this);
            FrameLayout.LayoutParams tvFeeNameParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            tvFeeNameParam.gravity = Gravity.CENTER_VERTICAL;
            tvFeeNameParam.setMargins(Utils.instence(this).dip2px(10), 0, 0, 0);
            tvFeeName.setLayoutParams(tvFeeNameParam);
            tvFeeName.setText(otherFee.name);
            tvFeeName.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            frTitle.addView(tvFeeName);

            mLlyOtherFees.addView(frTitle, insertLine++);

            TextView tvDescription = new TextView(this);
            LinearLayout.LayoutParams tvDescriptionParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvDescriptionParam.setMargins(0, Utils.instence(this).dip2px(5), 0, 0);
            tvDescription.setLayoutParams(tvDescriptionParam);
            tvDescription.setText(otherFee.description.replace("\\n", "\n"));
            HHLog.v(otherFee.description);
            tvDescription.setTextColor(ContextCompat.getColor(this, R.color.haha_gray));
            tvDescription.setLineSpacing(0, 1.2f);
            mLlyOtherFees.addView(tvDescription, insertLine++);
        }
    }

    @Override
    public void setServiceContentNormal() {
        mLlyNormalService.setVisibility(View.VISIBLE);
        mLlyVIPService.setVisibility(View.GONE);
        mLlyWuyouService.setVisibility(View.GONE);
        mLlyInsurance.setVisibility(View.GONE);
    }

    @Override
    public void setServiceContentVIP() {
        mLlyNormalService.setVisibility(View.GONE);
        mLlyVIPService.setVisibility(View.VISIBLE);
        mLlyWuyouService.setVisibility(View.GONE);
        mLlyInsurance.setVisibility(View.GONE);
    }

    @Override
    public void setServiceContentWuyou() {
        mLlyNormalService.setVisibility(View.GONE);
        mLlyVIPService.setVisibility(View.GONE);
        mLlyWuyouService.setVisibility(View.VISIBLE);
        mLlyInsurance.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTrainingCost(String cost) {
        mTvTrainingCost.setText(cost);
    }

    @Override
    public void setTotalAmount(String cost) {
        mTvTotalAmount.setText(cost);
    }

    @Override
    public void hidePurchase() {
        mTvPay.setVisibility(View.GONE);
    }

    @Override
    public void showMoniInFeeDetail() {
        mLlyMoni.setVisibility(View.VISIBLE);
    }
}
