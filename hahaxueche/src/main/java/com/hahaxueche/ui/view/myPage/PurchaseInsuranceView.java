package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2017/2/25.
 */

public interface PurchaseInsuranceView extends HHBaseView {
    void setPayAmount(String text);

    void setNotice(String text);

    void loadPaymentMethod(ArrayList<PaymentMethod> paymentMethods);
}
