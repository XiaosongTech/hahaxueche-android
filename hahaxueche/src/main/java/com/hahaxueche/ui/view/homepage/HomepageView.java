package com.hahaxueche.ui.view.homepage;

import com.hahaxueche.ui.view.HHBaseView;

/**
 * Created by wangshirui on 16/9/17.
 */
public interface HomepageView extends HHBaseView {
    /**
     * 打开webview
     *
     * @param url
     */
    void openWebView(String url);

    /**
     * 显示信息
     *
     * @param message
     */
    void showMessage(String message);
}
