package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.payment.InsurancePrices;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyInsuranceView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.Utils;
import com.hahaxueche.util.WebViewUrl;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscription;

/**
 * Created by wangshirui on 2017/2/25.
 */

public class MyInsurancePresenter implements Presenter<MyInsuranceView> {
    private MyInsuranceView mMyInsuranceView;
    private Subscription subscription;
    private HHBaseApplication application;

    @Override
    public void attachView(MyInsuranceView view) {
        this.mMyInsuranceView = view;
        application = HHBaseApplication.get(mMyInsuranceView.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            mMyInsuranceView.setViewNoPurchase();
            mMyInsuranceView.setWithNewCoachPayEnable(true);
            mMyInsuranceView.setWithPaidCoachPayEnable(true);
            mMyInsuranceView.setWithoutCoachPayEnable(true);
        } else if (!user.student.isPurchasedInsurance()) {
            mMyInsuranceView.setViewNoPurchase();
            if (user.student.hasPurchasedService()) {
                mMyInsuranceView.setWithNewCoachPayEnable(false);
                mMyInsuranceView.setWithPaidCoachPayEnable(true);
                mMyInsuranceView.setWithoutCoachPayEnable(false);
            } else {
                mMyInsuranceView.setWithNewCoachPayEnable(true);
                mMyInsuranceView.setWithPaidCoachPayEnable(false);
                mMyInsuranceView.setWithoutCoachPayEnable(true);
            }
        } else if (!user.student.isUploadedInsurance()) {
            mMyInsuranceView.setViewNoUploadInfo();
        } else {
            mMyInsuranceView.setViewSuccess();
            String insuranceAbstract = mMyInsuranceView.getContext().getResources()
                    .getString(R.string.insurance_abstract, user.student.identity_card.name,
                            Utils.getDateHanziFromUTC(user.student.insurance_order.policy_start_time));
            mMyInsuranceView.setAbstract(insuranceAbstract);
        }
        InsurancePrices ip = application.getConstants().insurance_prices;
        mMyInsuranceView.setWithNewCoachPrice("限时价 " + Utils.getMoney(ip.pay_with_new_coach_price));
        mMyInsuranceView.setWithPaidCoachPrice("限时价 " + Utils.getMoney(ip.pay_with_paid_coach_price));
        mMyInsuranceView.setWithoutCoachPrice("限时价 " + Utils.getMoney(ip.pay_without_coach_price));
    }

    @Override
    public void detachView() {
        this.mMyInsuranceView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(mMyInsuranceView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }

    public void purchaseWithNewCoach() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            //提示登陆
            mMyInsuranceView.alertToLogin();
            return;
        }
        mMyInsuranceView.finishToFindCoach();
    }

    public void purchaseWithPaidCoach() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            //提示登陆
            mMyInsuranceView.alertToLogin();
            return;
        }
        mMyInsuranceView.finishToPurchaseInsuranceWithPaidCoach();
    }

    public void purchaseWithoutCoach() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            //提示登陆
            mMyInsuranceView.alertToLogin();
            return;
        }
        mMyInsuranceView.finishToPurchaseInsuranceWithoutCoach();
    }


    /**
     * 右上角按钮点击
     */
    public void clickRightButton() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.isPurchasedInsurance()) {
            mMyInsuranceView.openWebView(WebViewUrl.WEB_URL_PEIFUBAO);
        } else if (!user.student.isUploadedInsurance()) {
            mMyInsuranceView.finishToUploadInfo();
        } else {
            mMyInsuranceView.navigateToInsuranceInfo();
        }
    }
}
