package com.hahaxueche.presenter.login;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.login.StartLoginView;
import com.hahaxueche.util.HHLog;

/**
 * Created by wangshirui on 16/9/18.
 */
public class StartLoginPresenter extends HHBasePresenter implements Presenter<StartLoginView> {
    private StartLoginView mView;
    private Constants constants;

    public void attachView(StartLoginView view) {
        this.mView = view;
        HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        constants = application.getConstants();
        if (constants != null) {
            mView.initBanners(constants.new_login_banners);
        }
    }

    public void detachView() {
        this.mView = null;
    }

    public void navigateToLogin() {
        mView.navigateToLogin();
    }

    public void navigateToRegister() {
        mView.navigateToRegister();
    }

    public void touristLogin() {
        HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        application.getSharedPrefUtil().createFakeUser();
        mView.navigateToHomepage();
    }

    public void clickBanner(int i) {
        try {
            if (!TextUtils.isEmpty(constants.new_login_banners.get(i).target_url)) {
                mView.openWebView(constants.new_login_banners.get(i).target_url);
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
            e.printStackTrace();
        }
    }
}
