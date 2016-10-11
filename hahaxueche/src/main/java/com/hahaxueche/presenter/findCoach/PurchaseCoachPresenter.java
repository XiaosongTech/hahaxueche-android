package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PurchaseCoachView;

import java.util.ArrayList;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/10/11.
 */

public class PurchaseCoachPresenter implements Presenter<PurchaseCoachView> {
    private PurchaseCoachView mPurchaseCoachView;
    private Subscription subscription;
    private HHBaseApplication application;
    private Coach mCoach;
    private User mUser;

    @Override
    public void attachView(PurchaseCoachView view) {
        this.mPurchaseCoachView = view;
        application = HHBaseApplication.get(mPurchaseCoachView.getContext());
        mUser = application.getSharedPrefUtil().getUser();
    }

    @Override
    public void detachView() {
        this.mPurchaseCoachView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
        mCoach = null;
        mUser = null;
    }

    public void setCoach(Coach coach) {
        this.mCoach = coach;
        if (mCoach == null) return;
        mPurchaseCoachView.loadCoachInfo(mCoach);
        mPurchaseCoachView.loadPaymentMethod(getPaymentMethod());
    }

    /**
     * 支付方式，目前支持支付宝，银行卡，分期乐
     *
     * @return
     */
    private ArrayList<PaymentMethod> getPaymentMethod() {
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
        PaymentMethod aliPay = new PaymentMethod(0, R.drawable.ic_alipay_icon, "支付宝", "推荐有支付宝账号的用户使用");
        PaymentMethod cardPay = new PaymentMethod(4, R.drawable.ic_cardpay_icon, "银行卡支付", "安全极速支付,无需开通网银");
        PaymentMethod fqlPay = new PaymentMethod(1, R.drawable.logo_fenqile, "分期乐", "推荐分期使用");
        paymentMethods.add(aliPay);
        paymentMethods.add(cardPay);
        paymentMethods.add(fqlPay);
        return paymentMethods;
    }
}
