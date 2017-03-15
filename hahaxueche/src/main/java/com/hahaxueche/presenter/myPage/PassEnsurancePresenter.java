package com.hahaxueche.presenter.myPage;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.PassEnsuranceView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/12/21.
 */

public class PassEnsurancePresenter extends HHBasePresenter implements Presenter<PassEnsuranceView> {
    private PassEnsuranceView mView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(PassEnsuranceView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mView.changeCustomerService();
    }

    public void detachView() {
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }
}