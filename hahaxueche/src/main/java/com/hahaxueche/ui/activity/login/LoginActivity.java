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
import com.hahaxueche.ui.activity.HHBaseActivity;
import com.hahaxueche.presenter.login.LoginPresenter;
import com.hahaxueche.ui.view.login.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangshirui on 16/9/9.
 */
public class LoginActivity extends HHBaseActivity implements LoginView {
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
    @BindView(R.id.tv_change_type)
    TextView mTvChangeType;
    @BindView(R.id.crl_main)
    CoordinatorLayout mClyMain;
    ImageView mIvBack;
    TextView mTvTitle;
    TextView mTvForgetPwd;

    private LoginPresenter mPresenter;
    private int mLoginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new LoginPresenter();
        mPresenter.attachView(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initActionBar();
        initAuthLogin();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_login);
        mIvBack = ButterKnife.findById(actionBar.getCustomView(), R.id.iv_back);
        mTvTitle = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_title);
        mTvForgetPwd = ButterKnife.findById(actionBar.getCustomView(), R.id.tv_forget_pwd);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.finish();
            }
        });
        mTvForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                intent.putExtra("isResetPwd", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initAuthLogin() {
        mLoginType = mPresenter.AUTH_LOGIN;
        mTvTitle.setText("验证码登录");
        mTvChangeType.setText("我想使用密码登录我的帐号");
        mTvForgetPwd.setVisibility(View.INVISIBLE);
        mLlyAuthCode.setVisibility(View.GONE);
        mEtPassword.setVisibility(View.GONE);
        mTvGetAuthCode.setVisibility(View.VISIBLE);
        mTvLogin.setVisibility(View.GONE);
    }

    @Override
    public void initPasswordLogin() {
        mLoginType = mPresenter.PASSWORD_LOGIN;
        mTvTitle.setText("密码登录");
        mTvChangeType.setText("我想使用验证码登录我的帐号");
        mTvForgetPwd.setVisibility(View.VISIBLE);
        mLlyAuthCode.setVisibility(View.GONE);
        mEtPassword.setVisibility(View.VISIBLE);
        mTvGetAuthCode.setVisibility(View.GONE);
        mTvLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void showViewAfterSendingAuthCode() {
        mTvTitle.setText("验证码登录");
        mTvChangeType.setText("我想使用密码登录我的帐号");
        mTvForgetPwd.setVisibility(View.INVISIBLE);
        mLlyAuthCode.setVisibility(View.VISIBLE);
        mEtPassword.setVisibility(View.GONE);
        mTvGetAuthCode.setVisibility(View.GONE);
        mTvLogin.setVisibility(View.VISIBLE);
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
        mTvForgetPwd.setClickable(true);
        mTvResend.setClickable(true);
        mTvLogin.setClickable(true);
        mTvChangeType.setClickable(true);
        mTvGetAuthCode.setClickable(true);
    }

    @Override
    public void disableButtons() {
        mIvBack.setClickable(false);
        mTvForgetPwd.setClickable(false);
        mTvResend.setClickable(false);
        mTvLogin.setClickable(false);
        mTvChangeType.setClickable(false);
        mTvGetAuthCode.setClickable(false);
    }

    @Override
    public void navigateToCompleteInfo() {
        Intent intent = new Intent(getContext(), CompleteUserInfoActivity.class);
        startActivity(intent);
    }

    @OnClick({R.id.tv_get_auth_code, R.id.tv_resend})
    public void sendAuthCode() {
        mPresenter.getAuthCode(mEtCellPhone.getText().toString());
    }

    @OnClick(R.id.tv_change_type)
    public void changeLoginType() {
        mPresenter.changeLoginType(mLoginType);
    }

    @OnClick(R.id.tv_login)
    public void login() {
        mPresenter.login(mEtCellPhone.getText().toString(), mEtAuthCode.getText().toString(), mEtPassword.getText().toString(), mLoginType);
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
