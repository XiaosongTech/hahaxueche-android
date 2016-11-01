package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.ui.view.base.HHBaseView;

/**
 * Created by wangshirui on 2016/10/20.
 */

public interface PartnerDetailView extends HHBaseView {
    void showPartnerDetail(Partner partner);

    void showMessage(String message);

    void enableApplaud(boolean enable);

    void showApplaud(boolean isApplaud);

    void setApplaudCount(int count);

    void startApplaudAnimation();

    void initShareData(Partner partner);

    void addC1Label(int pos);

    void addC2Label(int pos);

    void addPrice(int pos, int price, int duration, String description);
}
