package com.hahaxueche.ui.activity.index;

import android.content.Context;
import android.os.Bundle;

import com.hahaxueche.MyApplication;
import com.hahaxueche.presenter.findCoach.FCPresenter;
import com.hahaxueche.presenter.mySetting.MSPresenter;
import com.hahaxueche.ui.activity.base.BaseActivity;

/**
 * Created by gibxin on 2016/3/7.
 */
public class IndexBaseActivity extends BaseActivity {
    // 上下文实例
    public Context context;
    // 应用实例
    public MyApplication application;
    // 寻找教练Presenter
    public FCPresenter fcPresenter;
    public MSPresenter msPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        application = (MyApplication) this.getApplication();
        fcPresenter = application.getFCPresenter();
        msPresenter = application.getMsPresenter();
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
