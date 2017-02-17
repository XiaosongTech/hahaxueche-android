package com.hahaxueche.presenter.login;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.login.StartLoginView;
import com.hahaxueche.util.HHLog;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/18.
 */
public class StartLoginPresenter implements Presenter<StartLoginView> {
    private StartLoginView mStartLoginView;
    private Constants constants;

    public void attachView(StartLoginView view) {
        this.mStartLoginView = view;
        HHBaseApplication application = HHBaseApplication.get(mStartLoginView.getContext());
        constants = application.getConstants();
        if (constants != null) {
            mStartLoginView.initBanners(constants.new_login_banners);
        }
    }

    public void detachView() {
        this.mStartLoginView = null;
    }

    public void navigateToLogin() {
        mStartLoginView.navigateToLogin();
    }

    public void navigateToRegister() {
        mStartLoginView.navigateToRegister();
    }

    public void touristLogin() {
        HHBaseApplication application = HHBaseApplication.get(mStartLoginView.getContext());
        application.getSharedPrefUtil().createFakeUser();
        mStartLoginView.navigateToHomepage();
    }

    public void clickBanner(int i) {
        try {
            if (!TextUtils.isEmpty(constants.new_login_banners.get(i).target_url)) {
                mStartLoginView.openWebView(constants.new_login_banners.get(i).target_url);
            }
        } catch (Exception e) {
            HHLog.e(e.getMessage());
            e.printStackTrace();
        }
    }
}
