package com.hahaxueche.ui.view.login;

import com.hahaxueche.model.base.Banner;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

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

    /**
     * 打开webview
     *
     * @param url
     */
    void openWebView(String url);

    /**
     * banner init
     *
     * @param bannerArrayList
     */
    void initBanners(ArrayList<Banner> bannerArrayList);
}
