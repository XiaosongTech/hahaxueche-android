package com.hahaxueche.ui.view.login;

import com.hahaxueche.ui.view.HHBaseView;

/**
 * Created by wangshirui on 16/9/9.
 */
public interface LoginView extends HHBaseView {
    /**
     * 显示信息
     *
     * @param message
     */
    void showMessage(String message);

    /**
     * 验证码登录初始状态
     */
    void initAuthLogin();

    /**
     * 密码登录初始状态
     */
    void initPasswordLogin();

    /**
     * 发送验证码后,页面状态
     */
    void showViewAfterSendingAuthCode();

    /**
     * 激活所有按钮
     */
    void enableButtons();

    /**
     * 禁用所有按钮
     */
    void disableButtons();

    /**
     * 跳转到完善资料
     */
    void navigateToCompleteInfo();
}
