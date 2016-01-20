package com.hahaxueche;

import android.app.Application;

import com.hahaxueche.presenter.signupLogin.SLPresenter;
import com.hahaxueche.presenter.signupLogin.SLPresenterImpl;

/**
 * ”¶”√Application¿‡
 * Created by gibxin on 2016/1/19.
 */
public class MyApplication extends Application {
    private SLPresenter sLPresenter;
    @Override
    public void onCreate() {
        super.onCreate();
        sLPresenter = new SLPresenterImpl(this);
    }

    public SLPresenter getPresenter() {
        return sLPresenter;
    }
}
