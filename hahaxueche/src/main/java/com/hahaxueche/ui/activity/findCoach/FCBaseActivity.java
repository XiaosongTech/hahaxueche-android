package com.hahaxueche.ui.activity.findCoach;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.hahaxueche.MyApplication;
import com.hahaxueche.presenter.appointment.APPresenter;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.presenter.mySetting.MSPresenter;
import com.hahaxueche.ui.activity.base.BaseActivity;

/**
 * 寻找教练基本Activity
 * Created by gibxin on 2016/2/21.
 */
public class FCBaseActivity extends BaseActivity {
    // 上下文实例
    public Context context;
    // 应用实例
    public MyApplication application;
    // 寻找教练Presenter
    public FCPresenter fcPresenter;
    public MSPresenter msPresenter;
    public APPresenter apPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        application = (MyApplication) this.getApplication();
        fcPresenter = application.getFCPresenter();
        msPresenter = application.getMsPresenter();
        apPresenter = application.getApPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
