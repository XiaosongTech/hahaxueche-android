package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.myPage.NotLoginVoucherView;
import com.hahaxueche.util.HHLog;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import rx.Subscription;

/**
 * Created by wangshirui on 2016/12/6.
 */

public class NotLoginVoucherPresenter implements Presenter<NotLoginVoucherView> {
    private NotLoginVoucherView mNotLoginVoucherView;
    private Subscription subscription;
    private HHBaseApplication application;
    private static final String WEB_URL_FREE_TRY = BuildConfig.MOBILE_URL + "/free_trial?promo_code=553353";

    public void attachView(NotLoginVoucherView view) {
        this.mNotLoginVoucherView = view;
        application = HHBaseApplication.get(mNotLoginVoucherView.getContext());
        mNotLoginVoucherView.changeCustomerService();
    }

    public void detachView() {
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void freeTry() {
        //免费试学URL
        String url = WEB_URL_FREE_TRY;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            if (user.student.city_id >= 0) {
                url += "&city_id=" + user.student.city_id;
            }
            if (!TextUtils.isEmpty(user.student.name)) {
                url += "&name=" + user.student.name;
            }
            if (!TextUtils.isEmpty(user.student.cell_phone)) {
                url += "&phone=" + user.student.cell_phone;
            }

        }
        //免费试学点击
        HashMap<String, String> map = new HashMap();
        if (user != null && user.isLogin()) {
            map.put("student_id", user.student.id);
            MobclickAgent.onEvent(mNotLoginVoucherView.getContext(), "homepage_free_trial_tapped", map);
        } else {
            MobclickAgent.onEvent(mNotLoginVoucherView.getContext(), "homepage_free_trial_tapped");
        }
        HHLog.v("free try url -> " + url);
        mNotLoginVoucherView.openWebView(url);
    }

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
        Unicorn.openServiceActivity(mNotLoginVoucherView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }
}
