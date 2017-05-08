package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.ClassTypeIntroView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

/**
 * Created by wangshirui on 2017/3/9.
 */

public class ClassTypeIntroPresenter extends HHBasePresenter implements Presenter<ClassTypeIntroView> {
    private ClassTypeIntroView mView;
    private HHBaseApplication application;
    public ClassType mClassType;

    @Override
    public void attachView(ClassTypeIntroView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
    }

    @Override
    public void detachView() {
        this.mView = null;
        application = null;
    }

    public void setFeeDetail(int totalAmount, ClassType classType, boolean isWuyouClass, boolean isShowPurchase) {
        mClassType = classType;
        if (classType.type == Common.CLASS_TYPE_NORMAL_C1 || classType.type == Common.CLASS_TYPE_NORMAL_C2) {
            //超值班
            mView.setServiceContentNormal();
        } else if (classType.type == Common.CLASS_TYPE_VIP_C1 || classType.type == Common.CLASS_TYPE_VIP_C2) {
            //VIP班
            mView.setServiceContentVIP();
        } else if (classType.type == Common.CLASS_TYPE_WUYOU_C1 || classType.type == Common.CLASS_TYPE_WUYOU_C2) {
            //无忧班
            mView.setServiceContentWuyou();
        }
        int insuranceWithNewCoachPrice = application.getConstants().insurance_prices.pay_with_new_coach_price;
        if (isWuyouClass) {
            mView.showMoniInFeeDetail();
        }
        Constants constants = application.getConstants();
        int cityId = 0;
        LocalSettings localSettings = application.getSharedPrefUtil().getLocalSettings();
        if (localSettings.cityId > -1) {
            cityId = localSettings.cityId;
        }
        City city = constants.getCity(cityId);
        mView.setFixedFees(city.fixed_cost_itemizer);
        if (city.other_fee != null) {
            mView.setOtherFees(city.other_fee, classType.isForceInsurance, isWuyouClass);
        }
        //培训费计算
        int totalFixedFee = city.getTotalFixedFee();
        int trainingCost = totalAmount - totalFixedFee - (classType.isForceInsurance ? insuranceWithNewCoachPrice : 0);
        mView.setTrainingCost(Utils.getMoney(trainingCost));
        mView.setTotalAmount(Utils.getMoney(totalAmount));
        if (!isShowPurchase) {
            mView.hidePurchase();
        }
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }
}
