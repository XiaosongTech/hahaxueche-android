package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.UploadIdCardView;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/11/25.
 */

public class UploadIdCardPresenter implements Presenter<UploadIdCardView> {
    private UploadIdCardView mUploadIdCardView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(UploadIdCardView view) {
        this.mUploadIdCardView = view;
        application = HHBaseApplication.get(mUploadIdCardView.getContext());
    }

    public void detachView() {
        this.mUploadIdCardView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }
}
