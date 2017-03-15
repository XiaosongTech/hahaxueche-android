package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PriceView;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

/**
 * Created by wangshirui on 2016/10/25.
 */

public class PricePresenter extends HHBasePresenter implements Presenter<PriceView> {
    private PriceView mView;
    private HHBaseApplication application;

    @Override
    public void attachView(PriceView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    @Override
    public void detachView() {
        this.mView = null;
        application = null;
    }

    public void showPrice(Coach coach) {
        if (coach == null) return;
        HHBaseApplication application = HHBaseApplication.get(mView.getContext());
        Constants constants = application.getConstants();
        City city = constants.getCity(coach.city_id);
        mView.setFixedFees(city.fixed_cost_itemizer);
        int totalFixedFee = city.getTotalFixedFee();
        if (coach.coach_group.training_cost != 0) {
            mView.setTrainFeeC1Normal(Utils.getMoney(coach.coach_group.training_cost - totalFixedFee));
            mView.setTotalFeeC1Normal(Utils.getMoney(coach.coach_group.training_cost));
        } else {
            mView.setTrainFeeC1Normal("无");
            mView.setTotalFeeC1Normal("无");
        }
        if (coach.coach_group.vip_price != 0) {
            mView.setTrainFeeC1VIP(Utils.getMoney(coach.coach_group.vip_price - totalFixedFee));
            mView.setTotalFeeC1VIP(Utils.getMoney(coach.coach_group.vip_price));
        } else {
            mView.setTrainFeeC1VIP("无");
            mView.setTotalFeeC1VIP("无");
        }
        if (coach.coach_group.c2_price != 0) {
            mView.setTrainFeeC2Normal(Utils.getMoney(coach.coach_group.c2_price - totalFixedFee));
            mView.setTotalFeeC2Normal(Utils.getMoney(coach.coach_group.c2_price));
        } else {
            mView.setTrainFeeC2Normal("无");
            mView.setTotalFeeC2Normal("无");
        }
        if (coach.coach_group.c2_vip_price != 0) {
            mView.setTrainFeeC2VIP(Utils.getMoney(coach.coach_group.c2_vip_price - totalFixedFee));
            mView.setTotalFeeC2VIP(Utils.getMoney(coach.coach_group.c2_vip_price));
        } else {
            mView.setTrainFeeC2VIP("无");
            mView.setTotalFeeC2VIP("无");
        }
        if (city.other_fee != null) {
            mView.setOtherFees(city.other_fee);
        }
    }

    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }
}
