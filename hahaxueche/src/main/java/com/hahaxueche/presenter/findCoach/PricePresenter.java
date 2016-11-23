package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.base.City;
import com.hahaxueche.model.base.Constants;
import com.hahaxueche.model.user.User;
import com.hahaxueche.model.user.coach.Coach;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PriceView;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

/**
 * Created by wangshirui on 2016/10/25.
 */

public class PricePresenter implements Presenter<PriceView> {
    private PriceView mPriceView;
    private HHBaseApplication application;

    @Override
    public void attachView(PriceView view) {
        this.mPriceView = view;
        application = HHBaseApplication.get(mPriceView.getContext());
    }

    @Override
    public void detachView() {
        this.mPriceView = null;
        application = null;
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
        Unicorn.openServiceActivity(mPriceView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }
}
