package com.hahaxueche.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.model.base.City;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.presenter.login.CompleteUserInfoPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.dialog.login.CityChoseDialog;
import com.hahaxueche.ui.view.login.CompleteUserInfoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/10.
 */
public class CompleteUserInfoActivity extends HHBaseActivity implements CompleteUserInfoView {
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_complete)
    TextView mTvComplete;
    @BindView(R.id.tv_coupon)
    TextView mTvCoupon;
    @BindView(R.id.et_coupon)
    TextView mEtCoupon;
    @BindView(R.id.crl_main)
    CoordinatorLayout mClyMain;
    ImageView mIvBack;
    TextView mTvTitle;

    private CompleteUserInfoPresenter mPresenter;
    private CityChoseDialog mCityChoseDialog;
    private int mSelectCityId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CompleteUserInfoPresenter();
        mPresenter.attachView(this);
        setContentView(R.layout.activity_complete_user_info);
        ButterKnife.bind(this);
        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setVisibility(View.GONE);
        mTvTitle.setText("个人信息");
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mClyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void enableButtons() {
        mTvComplete.setClickable(true);
        mTvCoupon.setClickable(true);
    }

    @Override
    public void disableButtons() {
        mTvComplete.setClickable(false);
        mTvCoupon.setClickable(false);
    }

    @OnClick(R.id.tv_complete)
    public void completeUserInfo() {
        mPresenter.completeUserInfo(mEtUsername.getText().toString(), mSelectCityId, mEtCoupon.getText().toString());
    }

    @OnClick(R.id.tv_city)
    public void showCitySelectDialog() {
        if (mCityChoseDialog == null) {
            mCityChoseDialog = new CityChoseDialog(getContext(), new CityChoseDialog.onConfirmListener() {
                @Override
                public boolean selectCity(City city) {
                    if (city != null) {
                        mTvCity.setText(city.name);
                        mSelectCityId = city.id;
                    }
                    return true;
                }
            });
        }
        mCityChoseDialog.show();
    }

    @OnClick(R.id.tv_coupon)
    public void showCouponEdit() {
        if (mEtCoupon.getVisibility() == View.VISIBLE) {
            mEtCoupon.setVisibility(View.GONE);
        } else {
            mEtCoupon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void navigateToHomepage() {
        ActivityCollector.finishAll();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    /**
     * 拦截返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }
}
