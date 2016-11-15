package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.model.payment.Voucher;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/11/11.
 */

public interface MyVoucherView extends HHBaseView {
    /**
     * 加载代金券
     *
     * @param vouchers
     */
    void loadVouchers(ArrayList<Voucher> vouchers);

    /**
     * 没有代金券显示
     */
    void showNoVoucher();

    /**
     * 改变客服文字可点击
     */
    void changeCustomerService();

    void startRefresh();

    void stopRefresh();

    void showMessage(String message);

    void clearVouchers();
}
