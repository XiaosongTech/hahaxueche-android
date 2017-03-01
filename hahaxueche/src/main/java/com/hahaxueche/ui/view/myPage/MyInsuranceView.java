package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2017/2/25.
 */

public interface MyInsuranceView extends HHBaseView {
    void setViewNoPurchase();

    void setViewNoUploadInfo();

    void setViewSuccess();

    void set149WeiPayEnable(boolean enable);

    void set149YiPayEnable(boolean enable);

    void set169PayEnable(boolean enable);

    void showMessage(String message);

    void finishToPurchaseInsurance(int insuranceType);

    void finishToUploadInfo();
}
