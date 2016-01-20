package com.hahaxueche.ui.activity.signupLogin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.hahaxueche.MyApplication;
import com.hahaxueche.presenter.signupLogin.SLPresenter;

/**
 * 注册登录Activity基类
 * Created by gibxin on 2016/1/19.
 */
public class SLBaseActivity extends Activity {
    // 上下文实例
    public Context context;
    // 应用全局的实例
    public MyApplication application;
    // 核心层的Action实例
    public SLPresenter slPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }
}
