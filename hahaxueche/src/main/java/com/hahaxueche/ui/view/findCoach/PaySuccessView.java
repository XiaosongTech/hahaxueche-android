package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/13.
 */

public interface PaySuccessView extends HHBaseView {
    void showMessage(String message);

    void showCoachPayView();

    void showInsurancePayView();

    void setSignText(String text);

    void setInsuranceAmount(int amount);

    void setInsurancePaidAt(String paidAt);

    void setPayCoachName(String name);

    void setPayTime(String time);

    void setPayAmount(String amount);

    void setPayOrderNo(String orderNo);
}
