package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.ClassType;
import com.hahaxueche.model.user.coach.Coach;
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

public class ClassTypeIntroPresenter implements Presenter<ClassTypeIntroView> {
    private ClassTypeIntroView mClassTypeIntroView;
    private HHBaseApplication application;
    public ClassType mClassType;
    public Coach mCoach;


    @Override
    public void attachView(ClassTypeIntroView view) {
        this.mClassTypeIntroView = view;
        application = HHBaseApplication.get(mClassTypeIntroView.getContext());
    }

    @Override
    public void detachView() {
        this.mClassTypeIntroView = null;
        application = null;
    }

    public void setFeeDetail(int totalAmount, ClassType classType, Coach coach, boolean isShowPurchase) {
        mClassType = classType;
        mCoach = coach;
        if (classType.type == Common.CLASS_TYPE_NORMAL_C1 || classType.type == Common.CLASS_TYPE_NORMAL_C2) {
            //超值班
            mClassTypeIntroView.setServiceContentNormal();
        } else if (classType.type == Common.CLASS_TYPE_VIP_C1 || classType.type == Common.CLASS_TYPE_VIP_C2) {
            //VIP班
            mClassTypeIntroView.setServiceContentVIP();
        } else if (classType.type == Common.CLASS_TYPE_WUYOU_C1 || classType.type == Common.CLASS_TYPE_WUYOU_C2) {
            //无忧班
            mClassTypeIntroView.setServiceContentWuyou();
        }
        int insuranceWithNewCoachPrice = application.getConstants().insurance_prices.pay_with_new_coach_price;
        if (classType.isForceInsurance) {
            //有赔付宝金额的
            mClassTypeIntroView.setInsuranceCost(Utils.getMoney(insuranceWithNewCoachPrice));
        }
        if (mCoach.coach_group.group_type == Common.GROUP_TYPE_CHEYOU_WUYOU) {
            mClassTypeIntroView.showMoniInFeeDetail();
        }
        Constants constants = application.getConstants();
        City city = constants.getCity(coach.city_id);
        mClassTypeIntroView.setFixedFees(city.fixed_cost_itemizer);
        if (city.other_fee != null) {
            mClassTypeIntroView.setOtherFees(city.other_fee, classType.isForceInsurance, mCoach.coach_group.group_type);
        }
        //培训费计算
        int totalFixedFee = city.getTotalFixedFee();
        int trainingCost = totalAmount - totalFixedFee - (classType.isForceInsurance ? insuranceWithNewCoachPrice : 0);
        mClassTypeIntroView.setTrainingCost(Utils.getMoney(trainingCost));
        mClassTypeIntroView.setTotalAmount(Utils.getMoney(totalAmount));
        if (!isShowPurchase) {
            mClassTypeIntroView.hidePurchase();
        }
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(mClassTypeIntroView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }
}
