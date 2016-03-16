package com.hahaxueche.ui.activity.mySetting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.hahaxueche.MyApplication;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.presenter.mySetting.MSPresenter;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by gibxin on 2016/2/29.
 */
public class MSBaseActivity extends Activity {
    // 上下文实例
    public Context context;
    // 应用实例
    public MyApplication application;
    // 我的页面Presenter
    public MSPresenter msPresenter;
    public FCPresenter fcPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        application = (MyApplication) this.getApplication();
        msPresenter = application.getMsPresenter();
        fcPresenter = application.getFCPresenter();
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
