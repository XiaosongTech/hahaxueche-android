package com.hahaxueche.ui.activity.myPage;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.presenter.myPage.NotLoginVoucherPresenter;
import com.hahaxueche.ui.activity.base.BaseWebViewActivity;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.ui.view.myPage.NotLoginVoucherView;
import com.hahaxueche.util.HHLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 2016/12/6.
 */

public class NotLoginVoucherActivity extends HHBaseActivity implements NotLoginVoucherView {
    private NotLoginVoucherPresenter mPresenter;
    @BindView(R.id.tv_customer_service)
    TextView mTvCustomerService;
    @BindView(R.id.sv_main)
    ScrollView mSvMain;
    private static final int PERMISSIONS_REQUEST_CELL_PHONE = 601;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new NotLoginVoucherPresenter();
        setContentView(R.layout.activity_not_login_voucher);
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
        mTvTitle.setText("我的代金券");
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotLoginVoucherActivity.this.finish();
            }
        });
    }

    @Override
    public void openWebView(String url) {
        this.openWebView(url, "", false);
    }

    @Override
    public void openWebView(String url, String title, boolean isShowShare) {
        Intent intent = new Intent(getContext(), BaseWebViewActivity.class);
        Bundle bundle = new Bundle();
        HHLog.v("webview url -> " + url);
        bundle.putString("url", url);
        bundle.putString("title", title);
        bundle.putBoolean("isShowShare", isShowShare);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @OnClick({R.id.tv_free_try})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_free_try:
                mPresenter.freeTry();
                break;
            default:
                break;
        }
    }

    @Override
    public void changeCustomerService() {
        String customerService = mTvCustomerService.getText().toString();
        CharSequence customerServiceStr = customerService;
        SpannableString spCustomerServiceStr = new SpannableString(customerServiceStr);
        spCustomerServiceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CELL_PHONE);
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
    public void showMessage(String message) {
        Snackbar.make(mSvMain, message, Snackbar.LENGTH_SHORT).show();
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
        if (requestCode == PERMISSIONS_REQUEST_CELL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                contactService();
            } else {
                showMessage("请允许拨打电话权限，不然无法直接拨号联系客服");
            }
        }
    }
}
