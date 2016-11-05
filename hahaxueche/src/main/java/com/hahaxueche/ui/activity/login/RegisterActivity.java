package com.hahaxueche.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hahaxueche.R;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.base.HHBaseActivity;
import com.hahaxueche.presenter.login.RegisterPresenter;
import com.hahaxueche.ui.activity.base.MainActivity;
import com.hahaxueche.ui.view.login.RegisterView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/10.
 */
public class RegisterActivity extends HHBaseActivity implements RegisterView {
    @BindView(R.id.et_cell_phone)
    TextView mEtCellPhone;
    @BindView(R.id.lly_auth_code)
    LinearLayout mLlyAuthCode;
    @BindView(R.id.et_auth_code)
    EditText mEtAuthCode;
    @BindView(R.id.tv_resend)
    TextView mTvResend;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.tv_get_auth_code)
    TextView mTvGetAuthCode;
    @BindView(R.id.tv_login)
    TextView mTvLogin;
    @BindView(R.id.crl_main)
    CoordinatorLayout mClyMain;
    ImageView mIvBack;
    TextView mTvTitle;

    private RegisterPresenter mPresenter;
    private boolean isResetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new RegisterPresenter();
        mPresenter.attachView(this);
        setContentView(R.layout.activity_register);
        isResetPwd = getIntent().getBooleanExtra("isResetPwd", false);
        ButterKnife.bind(this);
        initActionBar();
        initView();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_base);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.finish();
            }
        });
    }

    @Override
    public void initView() {
        if (isResetPwd) {
            mTvTitle.setText("重置密码");
        } else {
            mTvTitle.setText("注册");
        }
        mLlyAuthCode.setVisibility(View.GONE);
        mEtPassword.setVisibility(View.GONE);
        mTvGetAuthCode.setVisibility(View.VISIBLE);
        mTvLogin.setVisibility(View.GONE);
    }

    @Override
    public void showViewAfterSendingAuthCode() {
        mLlyAuthCode.setVisibility(View.VISIBLE);
        mEtPassword.setVisibility(View.VISIBLE);
        mEtPassword.setHint("请设置6~20位密码");
        mTvGetAuthCode.setVisibility(View.GONE);
        mTvLogin.setVisibility(View.VISIBLE);
        mTvLogin.setText("完成");
        mTvResend.setClickable(false);
        mTvResend.setTextColor(ContextCompat.getColor(getContext(), R.color.haha_orange_light));
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                mTvResend.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                mTvResend.setTextColor(ContextCompat.getColor(getContext(), R.color.app_theme_color));
                mTvResend.setText("重发");
                mTvResend.setClickable(true);
            }
        }.start();
    }

    @Override
    public void enableButtons() {
        mIvBack.setClickable(true);
        mTvResend.setClickable(true);
        mTvLogin.setClickable(true);
        mTvGetAuthCode.setClickable(true);
    }

    @Override
    public void disableButtons() {
        mIvBack.setClickable(false);
        mTvResend.setClickable(false);
        mTvLogin.setClickable(false);
        mTvGetAuthCode.setClickable(false);
    }

    @Override
    public void navigateToCompleteInfo() {
        Intent intent = new Intent(getContext(), CompleteUserInfoActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.tv_get_auth_code, R.id.tv_resend})
    public void sendAuthCode() {
        mPresenter.getAuthCode(mEtCellPhone.getText().toString(), isResetPwd);
    }

    @OnClick(R.id.tv_login)
    public void resetPassword() {
        if (isResetPwd) {
            mPresenter.resetPassword(mEtCellPhone.getText().toString(), mEtAuthCode.getText().toString(), mEtPassword.getText().toString());
        } else {
            mPresenter.register(mEtCellPhone.getText().toString(), mEtAuthCode.getText().toString(), mEtPassword.getText().toString());
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
    public void showMessage(String message) {
        Snackbar.make(mClyMain, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
