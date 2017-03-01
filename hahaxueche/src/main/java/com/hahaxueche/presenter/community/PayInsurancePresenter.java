package com.hahaxueche.presenter.community;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.community.PayInsuranceView;

import rx.Subscription;

/**
 * Created by wangshirui on 2017/3/1.
 */

public class PayInsurancePresenter implements Presenter<PayInsuranceView> {
    private PayInsuranceView mPayInsuranceView;
    private Subscription subscription;
    private HHBaseApplication application;

    public void attachView(PayInsuranceView view) {
        this.mPayInsuranceView = view;
        application = HHBaseApplication.get(mPayInsuranceView.getContext());
    }

    public void detachView() {
        this.mPayInsuranceView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    /**
     * 推荐有奖跳转逻辑
     */
    public void toReferFriends() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.is_sales_agent) {
            //非代理
            mPayInsuranceView.navigateToStudentRefer();
        } else {
            mPayInsuranceView.navigateToReferFriends();
        }
    }
}
