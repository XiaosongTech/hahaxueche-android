package com.hahaxueche.ui.view.login;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/10.
 */
public interface CompleteUserInfoView extends HHBaseView {
    /**
     * 显示信息
     *
     * @param message
     */
    void showMessage(String message);

    /**
     * 激活所有按钮
     */
    void enableButtons();

    /**
     * 禁用所有按钮
     */
    void disableButtons();

    /**
     * 跳转到首页
     */
    void navigateToHomepage();

}
