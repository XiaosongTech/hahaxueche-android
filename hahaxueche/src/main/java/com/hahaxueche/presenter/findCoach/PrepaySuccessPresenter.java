package com.hahaxueche.presenter.findCoach;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.findCoach.PrepaySuccessView;
import com.hahaxueche.util.Utils;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscription;

/**
 * Created by wangshirui on 2017/3/13.
 */

public class PrepaySuccessPresenter implements Presenter<PrepaySuccessView> {
    private PrepaySuccessView mPrepaySuccessView;
    private Subscription subscription;
    private HHBaseApplication application;

    @Override
    public void attachView(PrepaySuccessView view) {
        this.mPrepaySuccessView = view;
        application = HHBaseApplication.get(view.getContext());
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            mPrepaySuccessView.setPrepaidAmount(Utils.getMoney(user.student.prepaid_amount));
            mPrepaySuccessView.setPrepaidTime(Utils.getLocalDate());
        }
    }

    @Override
    public void detachView() {
        this.mPrepaySuccessView = null;
        application = null;
        if (subscription != null) subscription.unsubscribe();
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
        Unicorn.openServiceActivity(mPrepaySuccessView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }
}
