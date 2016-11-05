package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.base.Bank;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/2.
 */

public interface SelectBankView extends HHBaseView {
    void showBankList(ArrayList<Bank> banks);

    void showPopularBankList(ArrayList<Bank> popularBanks);
}
