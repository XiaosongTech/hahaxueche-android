package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.user.coach.Partner;
import com.hahaxueche.model.user.coach.ProductType;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

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

    void addPrices(ArrayList<ProductType> productTypes);

    void initShareData(Partner partner);
}
