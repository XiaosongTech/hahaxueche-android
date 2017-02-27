package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/13.
 */

public interface PaySuccessView extends HHBaseView {
    void loadPayInfo(Coach coach, PurchasedService ps);

    void showMessage(String message);

    void showCoachPayView();

    void showInsurancePayView();

    void setSignText(String text);
}
