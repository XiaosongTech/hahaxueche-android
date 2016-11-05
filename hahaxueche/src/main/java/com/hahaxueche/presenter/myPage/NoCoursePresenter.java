package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.NoCourseView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/11/5.
 */

public class NoCoursePresenter implements Presenter<NoCourseView> {
    private NoCourseView mNoCourseView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(NoCourseView view) {
        this.mNoCourseView = view;
        application = HHBaseApplication.get(mNoCourseView.getContext());
        mNoCourseView.initBannerHighlights(application.getConstants().banner_highlights);
    }

    public void detachView() {
        this.mNoCourseView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }
}
