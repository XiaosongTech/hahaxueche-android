package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.api.HHApiService;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.base.LocalSettings;
import com.hahaxueche.model.base.UserIdentityParam;
import com.hahaxueche.model.drivingSchool.DrivingSchool;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.UserIdentityInfo;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.HHBasePresenter;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.ClassTypeIntroView;
import com.hahaxueche.util.Common;
import com.hahaxueche.util.HHLog;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshirui on 2017/3/9.
 */

public class ClassTypeIntroPresenter extends HHBasePresenter implements Presenter<ClassTypeIntroView> {
    private ClassTypeIntroView mView;
    private Subscription subscription;
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
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void setFeeDetail(int totalAmount, ClassType classType, boolean isWuyouClass) {
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
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }

    public void getUserIdentity(String cellPhone, Coach mCoach, DrivingSchool mDrivingSchool) {
        HHApiService apiService = application.getApiService();
        UserIdentityParam param = new UserIdentityParam();
        param.phone = cellPhone;
        param.promo_code = "921434";
        if (mCoach != null) {
            param.coach_id = mCoach.id;
            param.field_id = mCoach.coach_group.field_id;
            param.driving_school_id = String.valueOf(mCoach.driving_school_id);
        } else {
            param.driving_school_id = String.valueOf(mDrivingSchool.id);
        }
        if (application.getMyLocation() != null) {
            param.lng = application.getMyLocation().lng;
            param.lat = application.getMyLocation().lat;
        }
        subscription = apiService.getUserIdentity(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<UserIdentityInfo>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        HHLog.e(e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(UserIdentityInfo userIdentityInfo) {
                    }
                });
    }
}
