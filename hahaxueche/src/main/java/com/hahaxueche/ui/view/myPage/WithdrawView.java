package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/11/1.
 */

public interface WithdrawView extends HHBaseView {
    void showMessage(String message);

    void back(boolean isUpdate);

    void startRefresh();

    void stopRefresh();

    void setAvailableAmount(String amount);

    void setBankInfo(BankCard bankCard);
}
