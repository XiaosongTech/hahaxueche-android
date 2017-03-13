package com.hahaxueche.ui.view.myPage;

import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2017/2/25.
 */

public interface MyInsuranceView extends HHBaseView {
    void setViewNoPurchase();

    void setViewNoUploadInfo();

    void setViewSuccess();

    void setWithNewCoachPayEnable(boolean enable);

    void setWithPaidCoachPayEnable(boolean enable);

    void setWithoutCoachPayEnable(boolean enable);

    void showMessage(String message);

    void finishToPurchaseInsuranceWithPaidCoach();

    void finishToPurchaseInsuranceWithoutCoach();

    void finishToUploadInfo();

    void finishToFindCoach();

    void setAbstract(String text);

    void navigateToInsuranceInfo();

    void setWithNewCoachPrice(String s);

    void setWithPaidCoachPrice(String s);

    void setWithoutCoachPrice(String s);
}
