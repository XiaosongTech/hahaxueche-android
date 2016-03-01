package com.hahaxueche.ui.activity.mySetting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.hahaxueche.MyApplication;
import com.hahaxueche.presenter.mySetting.MSPresenter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        application = (MyApplication) this.getApplication();
        msPresenter = application.getMsPresenter();
    }
}
