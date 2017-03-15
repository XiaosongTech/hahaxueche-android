package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.payment.InsurancePrices;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyInsuranceView;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscription;

/**
 * Created by wangshirui on 2017/2/25.
 */

public class MyInsurancePresenter extends HHBasePresenter implements Presenter<MyInsuranceView> {
    private MyInsuranceView mView;
    private Subscription subscription;
    private HHBaseApplication application;

    @Override
    public void attachView(MyInsuranceView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mView.setViewNoPurchase();
            mView.setWithPaidCoachPayEnable(true);
            mView.setWithoutCoachPayEnable(true);
        } else if (!user.student.isPurchasedInsurance()) {
            mView.setViewNoPurchase();
            if (user.student.hasPurchasedService()) {
                mView.setWithPaidCoachPayEnable(true);
                mView.setWithoutCoachPayEnable(false);
            } else {
                mView.setWithPaidCoachPayEnable(false);
                mView.setWithoutCoachPayEnable(true);
            }
        } else if (!user.student.isUploadedInsurance()) {
            mView.setViewNoUploadInfo();
        } else {
            mView.setViewSuccess();
            String insuranceAbstract = mView.getContext().getResources()
                    .getString(R.string.insurance_abstract, user.student.identity_card.name,
                            Utils.getDateHanziFromUTC(user.student.insurance_order.policy_start_time));
            mView.setAbstract(insuranceAbstract);
        }
        InsurancePrices ip = application.getConstants().insurance_prices;
        mView.setWithPaidCoachPrice("限时价 " + Utils.getMoney(ip.pay_with_paid_coach_price));
        mView.setWithoutCoachPrice("限时价 " + Utils.getMoney(ip.pay_without_coach_price));
    }

    @Override
    public void detachView() {
        this.mView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void purchaseWithPaidCoach() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            //提示登陆
            mView.alertToLogin();
            return;
        }
        mView.finishToPurchaseInsuranceWithPaidCoach();
    }

    public void purchaseWithoutCoach() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            //提示登陆
            mView.alertToLogin();
            return;
        }
        mView.finishToPurchaseInsuranceWithoutCoach();
    }


    /**
     * 右上角按钮点击
     */
    public void clickRightButton() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.isPurchasedInsurance()) {
            mView.openWebView(WebViewUrl.WEB_URL_PEIFUBAO);
        } else if (!user.student.isUploadedInsurance()) {
            mView.finishToUploadInfo();
        } else {
            mView.navigateToInsuranceInfo();
        }
    }
}
