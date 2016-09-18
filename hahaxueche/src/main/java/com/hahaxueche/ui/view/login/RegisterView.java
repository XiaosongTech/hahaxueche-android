package com.hahaxueche.ui.view.login;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/10.
 */
public interface RegisterView extends HHBaseView {
    /**
     * 显示信息
     *
     * @param message
     */
    void showMessage(String message);

    /**
     * 页面初始化
     */
    void initView();

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

    /**
     * 跳转到首页
     */
    void navigateToHomepage();
}
