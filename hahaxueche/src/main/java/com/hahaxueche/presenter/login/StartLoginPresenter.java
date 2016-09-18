package com.hahaxueche.presenter.login;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.login.StartLoginView;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/18.
 */
public class StartLoginPresenter implements Presenter<StartLoginView> {
    private StartLoginView mStartLoginView;
    private Subscription subscription;

    public void attachView(StartLoginView view) {
        this.mStartLoginView = view;
    }

    public void detachView() {
        this.mStartLoginView = null;
        if (subscription != null) subscription.unsubscribe();
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
}
