package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.base.FixedCostItem;
import com.hahaxueche.model.payment.OtherFee;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2017/3/9.
 */

public interface ClassTypeIntroView extends HHBaseView {
    void setFixedFees(ArrayList<FixedCostItem> fixedFees);

    void setOtherFees(ArrayList<OtherFee> otherFees, boolean isForceInsurance);

    void setServiceContentNormal();

    void setServiceContentVIP();

    void setServiceContentWuyou();

    void setTrainingCost(String cost);

    void setTotalAmount(String cost);

    void setInsuranceCost(String cost);
}
