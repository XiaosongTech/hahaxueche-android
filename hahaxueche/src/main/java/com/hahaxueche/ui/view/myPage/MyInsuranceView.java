package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2017/2/25.
 */

public interface MyInsuranceView extends HHBaseView {
    void setViewNoPurchase();

    void setViewNoUploadInfo();

    void setViewSuccess();

    void set120PayEnable(boolean enable);

    void set130PayEnable(boolean enable);

    void set150PayEnable(boolean enable);

    void showMessage(String message);
}
