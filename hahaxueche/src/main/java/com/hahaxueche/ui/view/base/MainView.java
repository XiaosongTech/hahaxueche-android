package com.hahaxueche.ui.view.base;

import com.hahaxueche.model.payment.Voucher;

/**
 * Created by wangshirui on 2016/11/30.
 */

public interface MainView extends HHBaseView {
    void setMyPageBadge(boolean hasBadge);

    void showSignDialog();

    void navigateToUploadIdCard();

    void navigateToSignContract();

    void navigateToMyContract();

    void showVoucherDialog(String studentId, Voucher voucher);

    void initShareData(String shareUrl);
}
