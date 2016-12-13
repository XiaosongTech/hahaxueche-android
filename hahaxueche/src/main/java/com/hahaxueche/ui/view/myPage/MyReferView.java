package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.user.Referrer;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/12/13.
 */

public interface MyReferView extends HHBaseView {
    void setPullLoadEnable(boolean enable);

    void refreshReferrerList(ArrayList<Referrer> referrerArrayList);

    void addMoreReferrerList(ArrayList<Referrer> referrerArrayList);

    void navigateToWithdraw();

    /**
     * 设置可提现金额
     *
     * @param money
     */
    void setWithdrawMoney(String money);
}
