package com.hahaxueche.ui.view.base;

/**
 * Created by wangshirui on 16/9/18.
 */
public interface WelcomeView extends HHBaseView {
    /**
     * 跳转到准备登录页面
     */
    void navigationToStartLogin();

    /**
     * 跳转到首页
     */
    void navigateToHomepage();

    /**
     * 跳转到完善资料
     */
    void navigateToCompleteInfo();
}
