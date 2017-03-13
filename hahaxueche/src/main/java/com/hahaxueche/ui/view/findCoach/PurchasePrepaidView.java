package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2017/3/13.
 */

public interface PurchasePrepaidView extends HHBaseView {
    void loadPaymentMethod(ArrayList<PaymentMethod> paymentMethods);

    void showMessage(String message);

    void paySuccess();

    void callPingpp(String result);
}
