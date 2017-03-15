package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PrepaySuccessView;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscription;

/**
 * Created by wangshirui on 2017/3/13.
 */

public class PrepaySuccessPresenter extends HHBasePresenter implements Presenter<PrepaySuccessView> {
    private PrepaySuccessView mView;
    private Subscription subscription;
    private HHBaseApplication application;

    @Override
    public void attachView(PrepaySuccessView view) {
        this.mView = view;
        application = HHBaseApplication.get(view.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mView.setPrepaidAmount(Utils.getMoney(user.student.prepaid_amount));
            mView.setPrepaidTime(Utils.getLocalDate());
        }
    }

    @Override
    public void detachView() {
        this.mView = null;
        application = null;
        if (subscription != null) subscription.unsubscribe();
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }
}
