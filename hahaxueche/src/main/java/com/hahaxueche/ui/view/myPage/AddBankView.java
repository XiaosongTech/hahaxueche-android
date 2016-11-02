package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.base.Bank;
import com.hahaxueche.model.payment.BankCard;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/11/2.
 */

public interface AddBankView extends HHBaseView {
    void showMessage(String message);

    void back(boolean isUpdate);

    void loadOpenBank(Bank openAccountBank);

    void loadAccount(BankCard bankCard);
}
