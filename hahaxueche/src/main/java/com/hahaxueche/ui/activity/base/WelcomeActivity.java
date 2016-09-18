package com.hahaxueche.ui.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.hahaxueche.R;
import com.hahaxueche.presenter.base.WelcomePresenter;
import com.hahaxueche.ui.activity.ActivityCollector;
import com.hahaxueche.ui.activity.login.StartLoginActivity;
import com.hahaxueche.ui.view.base.WelcomeView;
import com.hahaxueche.ui.view.login.CompleteUserInfoView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangshirui on 16/9/8.
 */
public class WelcomeActivity extends HHBaseActivity implements WelcomeView {
    private WelcomePresenter mPresenter;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new WelcomePresenter();
        mPresenter.attachView(this);
        setTheme(R.style.AppThemeNoTitle);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        mPresenter.startApplication();
    }

    @Override
    public void navigationToStartLogin() {
        startActivity(new Intent(getContext(), StartLoginActivity.class));
        WelcomeActivity.this.finish();
    }

    @Override
    public void navigateToCompleteInfo() {
        startActivity(new Intent(getContext(), CompleteUserInfoView.class));
        WelcomeActivity.this.finish();
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
}
