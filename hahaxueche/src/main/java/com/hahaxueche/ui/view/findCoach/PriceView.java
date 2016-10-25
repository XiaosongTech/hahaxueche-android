package com.hahaxueche.ui.view.findCoach;

import com.hahaxueche.model.base.FixedCostItem;
import com.hahaxueche.model.payment.OtherFee;
import com.hahaxueche.ui.view.base.HHBaseView;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/25.
 */

public interface PriceView extends HHBaseView {
    void setFixedFees(ArrayList<FixedCostItem> fixedFees);

    void setOtherFees(ArrayList<OtherFee> otherFees);

    void setTrainFeeC1Normal(String fee);

    void setTrainFeeC1VIP(String fee);

    void setTrainFeeC2Normal(String fee);

    void setTrainFeeC2VIP(String fee);

    void setTotalFeeC1Normal(String fee);

    void setTotalFeeC1VIP(String fee);

    void setTotalFeeC2Normal(String fee);

    void setTotalFeeC2VIP(String fee);

}
