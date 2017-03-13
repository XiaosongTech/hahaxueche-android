package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2017/3/13.
 */

public interface PrepaySuccessView extends HHBaseView {
    void showMessage(String message);

    void setPrepaidAmount(String amount);

    void setPrepaidTime(String time);
}
