package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/11/29.
 */

public interface MyContractView extends HHBaseView {
    void showMessage(String message);

    void setPdf(String url);

    void setSignEnable(boolean enable);
}
