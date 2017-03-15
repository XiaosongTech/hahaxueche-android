package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.NoCourseView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class NoCoursePresenter extends HHBasePresenter implements Presenter<NoCourseView> {
    private NoCourseView mView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(NoCourseView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mView.initBannerHighlights(application.getConstants().banner_highlights);
    }

    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }
}
