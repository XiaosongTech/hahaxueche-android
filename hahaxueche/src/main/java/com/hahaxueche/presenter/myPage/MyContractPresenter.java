package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyContractView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/11/29.
 */

public class MyContractPresenter implements Presenter<MyContractView> {
    private MyContractView mMyContractView;
    private Subscription subscription;
    private HHBaseApplication application;

    @Override
    public void attachView(MyContractView view) {
        this.mMyContractView = view;
        application = HHBaseApplication.get(mMyContractView.getContext());
    }

    @Override
    public void detachView() {
        this.mMyContractView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }
}
