package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.payment.PaymentMethod;
import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/11.
 */

public interface PurchaseCoachView extends HHBaseView {
    void loadCoachInfo(Coach coach);

    void setClassTypeName(String name);

    void setClassTypePrice(String price);

    void loadPaymentMethod(ArrayList<PaymentMethod> paymentMethods);

    void setTotalAmountText(String text);

    void setTotalAmountWithVoucher(String voucherText, String amountText);

    void showMessage(String message);

    void callPingpp(String charge);

    void paySuccess();

    void setVoucherSelectable(boolean select);

    void showUnCumulativeVoucher(boolean isShow);

    void setVoucher(Voucher voucher);

    void showCumulativeVoucher(boolean isShow);

    void addCumulativeVoucher(String title, String price);
}
