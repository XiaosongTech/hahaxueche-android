package com.hahaxueche.ui.activity.signupLogin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.hahaxueche.MyApplication;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.presenter.mySetting.MSPresenter;
import com.hahaxueche.presenter.signupLogin.SLPresenter;
import com.hahaxueche.ui.activity.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Activity抽象基类���
 * Created by gibxin on 2016/1/19.
 */
public class SLBaseActivity extends BaseActivity {
    // 上下文实例
    public Context context;
    // 应用实例
    public MyApplication application;
    // 注册登录Presenter
    public SLPresenter slPresenter;
    //
    public FCPresenter fcPresenter;

    public MSPresenter msPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        application = (MyApplication) this.getApplication();
        slPresenter = application.getSLPresenter();
        fcPresenter = application.getFCPresenter();
        msPresenter = application.getMsPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
