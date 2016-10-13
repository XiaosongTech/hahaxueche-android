package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/11.
 */

public interface PurchaseCoachView extends HHBaseView {
    void loadCoachInfo(Coach coach);

    void unSelectAllClasses();

    void selectClass(int classType);

    void loadPaymentMethod(ArrayList<PaymentMethod> paymentMethods);

    void unSelectAllPayments();

    void selectPayment(int paymentId);

    void setPayText(String text);

    void showMessage(String message);

    void callPingpp(String charge);

    void paySuccess();
}
