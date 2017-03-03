package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.MyInsuranceView;
import com.hahaxueche.util.Common;
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
            mMyInsuranceView.set149WeiPayEnable(true);
            mMyInsuranceView.set149YiPayEnable(true);
            mMyInsuranceView.set169PayEnable(true);
        } else if (!user.student.isPurchasedInsurance()) {
            mMyInsuranceView.setViewNoPurchase();
            if (user.student.hasPurchasedService()) {
                mMyInsuranceView.set149WeiPayEnable(false);
                mMyInsuranceView.set149YiPayEnable(true);
                mMyInsuranceView.set169PayEnable(false);
            } else {
                mMyInsuranceView.set149WeiPayEnable(true);
                mMyInsuranceView.set149YiPayEnable(false);
                mMyInsuranceView.set169PayEnable(true);
            }
        } else if (!user.student.isUploadedInsurance()) {
            mMyInsuranceView.setViewNoUploadInfo();
        } else {
            mMyInsuranceView.setViewSuccess();
            String insuranceAbstract = mMyInsuranceView.getContext().getResources()
                    .getString(R.string.insurance_abstract, user.student.identity_card.name, user.student.insurance_order.paid_at);
            mMyInsuranceView.setAbstract(insuranceAbstract);
        }
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

    public void clickPurchase(int purchaseType) {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin()) {
            //提示登陆
            mMyInsuranceView.alertToLogin();
            return;
        }
        switch (purchaseType) {
            case Common.PURCHASE_INSURANCE_TYPE_149_WEI:
                mMyInsuranceView.finishToFindCoach();
                break;
            case Common.PURCHASE_INSURANCE_TYPE_149_YI:
                mMyInsuranceView.finishToPurchaseInsurance(Common.PURCHASE_INSURANCE_TYPE_149_YI);
                break;
            case Common.PURCHASE_INSURANCE_TYPE_169:
                mMyInsuranceView.finishToPurchaseInsurance(Common.PURCHASE_INSURANCE_TYPE_169);
                break;
            default:
                break;
        }
    }

    /**
     * 右上角按钮点击
     */
    public void clickRightButton() {
        User user = application.getSharedPrefUtil().getUser();
        if (user == null || !user.isLogin() || !user.student.isPurchasedInsurance()) {
            return;
        }
        if (!user.student.isUploadedInsurance()) {
            mMyInsuranceView.finishToUploadInfo();
        } else {
            mMyInsuranceView.navigateToInsuranceInfo();
        }
    }
}