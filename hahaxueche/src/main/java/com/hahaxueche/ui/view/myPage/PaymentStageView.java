package com.hahaxueche.ui.view.myPage;

import android.text.Spanned;

import com.hahaxueche.model.payment.PurchasedService;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/26.
 */

public interface PaymentStageView extends HHBaseView {
    void showMessage(String message);

    void setCoachInfo(Coach coach);

    void showPs(PurchasedService ps);

    void enablePayStage(boolean enable);

    void setCurrentPayAmountText(Spanned text);
}
