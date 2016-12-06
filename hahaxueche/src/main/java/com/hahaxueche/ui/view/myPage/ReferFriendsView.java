package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/21.
 */

public interface ReferFriendsView extends HHBaseView {
    void setReferRules(String rules);

    /**
     * 城市推荐图片
     *
     * @param url
     */
    void setMyCityReferImage(String url);

    /**
     * 设置推荐二维码图片
     *
     * @param url
     */
    void setQrCodeImage(String url);

    /**
     * 设置可提现金额
     *
     * @param money
     */
    void setWithdrawMoney(String money);

    void showMessage(String message);

    void navigateToWithdraw();

    void navigateToReferList();
}
