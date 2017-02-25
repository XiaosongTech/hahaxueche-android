package com.hahaxueche.presenter.myPage;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.R;
import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.PurchaseInsuranceView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.Utils;

import java.util.ArrayList;

import rx.Subscription;

/**
 * Created by wangshirui on 2017/2/25.
 */

public class PurchaseInsurancePresenter implements Presenter<PurchaseInsuranceView> {
    private PurchaseInsuranceView mPurchaseInsuranceView;
    private Subscription subscription;
    private HHBaseApplication application;
    private int mInsuranceType;
    private int mPaymentMethod = -1;

    @Override
    public void attachView(PurchaseInsuranceView view) {
        this.mPurchaseInsuranceView = view;
        application = HHBaseApplication.get(mPurchaseInsuranceView.getContext());
        mPurchaseInsuranceView.loadPaymentMethod(getPaymentMethod());
        mPaymentMethod = 0;//默认支付方式：支付宝
    }

    @Override
    public void detachView() {
        this.mPurchaseInsuranceView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setInsuranceType(int insuranceType) {
        mInsuranceType = insuranceType;
        mPurchaseInsuranceView.setPayAmount("总价：" + Utils.getMoney(mInsuranceType));
        if (mInsuranceType == Common.PURCHASE_INSURANCE_TYPE_130) {
            mPurchaseInsuranceView.setNotice(mPurchaseInsuranceView.getContext().getResources()
                    .getString(R.string.insurance_notice_130));
        } else if (mInsuranceType == Common.PURCHASE_INSURANCE_TYPE_150) {
            mPurchaseInsuranceView.setNotice(mPurchaseInsuranceView.getContext().getResources()
                    .getString(R.string.insurance_notice_150));
        }
    }

    /**
     * 支付方式，目前支持支付宝，银行卡，分期乐
     *
     * @return
     */
    private ArrayList<PaymentMethod> getPaymentMethod() {
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();
        PaymentMethod aliPay = new PaymentMethod(0, R.drawable.ic_alipay_icon, "支付宝", "推荐拥有支付宝账号的用户使用");
        PaymentMethod wxlPay = new PaymentMethod(5, R.drawable.ic_wx_icon, "微信支付", "推荐拥有微信账号的用户使用");
        PaymentMethod cardPay = new PaymentMethod(4, R.drawable.ic_cardpay_icon, "银行卡", "一网通支付，支持所有主流借记卡/信用卡");
        PaymentMethod fqlPay = new PaymentMethod(1, R.drawable.logo_fenqile, "分期乐", "推荐分期使用");
        paymentMethods.add(aliPay);
        paymentMethods.add(wxlPay);
        paymentMethods.add(cardPay);
        paymentMethods.add(fqlPay);
        return paymentMethods;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.mPaymentMethod = paymentMethod;
    }
}
