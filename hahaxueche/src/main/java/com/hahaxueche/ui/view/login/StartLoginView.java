package com.hahaxueche.ui.view.login;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 16/9/18.
 */
public interface StartLoginView extends HHBaseView {
    /**
     * 跳转到登录
     */
    void navigateToLogin();

    /**
     * 跳转到注册
     */
    void navigateToRegister();

    /**
     * 跳转到首页
     */
    void navigateToHomepage();
}
