package com.hahaxueche.ui.view.base;

/**
 * Created by wangshirui on 2016/12/20.
 */

public interface BaseWebViewView extends HHBaseView {
    void initShareData(String shareUrl);

    void showMessage(String message);

    void startToShare(int shareType);
}
