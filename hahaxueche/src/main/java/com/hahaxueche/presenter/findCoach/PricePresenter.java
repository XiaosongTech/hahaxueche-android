package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.payment.OtherFee;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PriceView;
import com.hahaxueche.util.Utils;

/**
 * Created by wangshirui on 2016/10/25.
 */

public class PricePresenter implements Presenter<PriceView> {
    private PriceView mPriceView;

    @Override
    public void attachView(PriceView view) {
        this.mPriceView = view;
    }

    @Override
    public void detachView() {
        this.mPriceView = null;
    }

    public void showPrice(Coach coach) {
        if (coach == null) return;
        HHBaseApplication application = HHBaseApplication.get(mPriceView.getContext());
        Constants constants = application.getConstants();
        City city = constants.getCity(coach.city_id);
        mPriceView.setFixedFees(city.fixed_cost_itemizer);
        int totalFixedFee = city.getTotalFixedFee();
        if (coach.coach_group.training_cost != 0) {
            mPriceView.setTrainFeeC1Normal(Utils.getMoney(coach.coach_group.training_cost - totalFixedFee));
            mPriceView.setTotalFeeC1Normal(Utils.getMoney(coach.coach_group.training_cost));
        } else {
            mPriceView.setTrainFeeC1Normal("无");
            mPriceView.setTotalFeeC1Normal("无");
        }
        if (coach.coach_group.vip_price != 0) {
            mPriceView.setTrainFeeC1VIP(Utils.getMoney(coach.coach_group.vip_price - totalFixedFee));
            mPriceView.setTotalFeeC1VIP(Utils.getMoney(coach.coach_group.vip_price));
        } else {
            mPriceView.setTrainFeeC1VIP("无");
            mPriceView.setTotalFeeC1VIP("无");
        }
        if (coach.coach_group.c2_price != 0) {
            mPriceView.setTrainFeeC2Normal(Utils.getMoney(coach.coach_group.c2_price - totalFixedFee));
            mPriceView.setTotalFeeC2Normal(Utils.getMoney(coach.coach_group.c2_price));
        } else {
            mPriceView.setTrainFeeC2Normal("无");
            mPriceView.setTotalFeeC2Normal("无");
        }
        if (coach.coach_group.c2_vip_price != 0) {
            mPriceView.setTrainFeeC2VIP(Utils.getMoney(coach.coach_group.c2_vip_price - totalFixedFee));
            mPriceView.setTotalFeeC2VIP(Utils.getMoney(coach.coach_group.c2_vip_price));
        } else {
            mPriceView.setTrainFeeC2VIP("无");
            mPriceView.setTotalFeeC2VIP("无");
        }
        if (city.other_fee != null) {
            mPriceView.setOtherFees(city.other_fee);
        }
    }
}
