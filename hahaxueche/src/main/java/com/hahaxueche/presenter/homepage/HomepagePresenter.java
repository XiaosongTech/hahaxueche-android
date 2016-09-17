package com.hahaxueche.presenter.homepage;

import android.text.TextUtils;

import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.Presenter;
import com.hahaxueche.ui.view.homepage.HomepageView;
import com.hahaxueche.util.HHLog;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFUserInfo;

import rx.Subscription;

/**
 * Created by wangshirui on 16/9/17.
 */
public class HomepagePresenter implements Presenter<HomepageView> {
    private HomepageView mHomepageView;
    private Subscription subscription;
    private static final String WEB_URL_ABOUT_HAHA = "http://staging.hahaxueche.net/#/student";
    private static final String WEB_URL_ABOUT_COACH = "http://staging.hahaxueche.net/#/coach";
    private static final String WEB_URL_MY_STRENGTHS = "http://activity.hahaxueche.com/share/features";
    private static final String WEB_URL_PROCEDURE = "http://activity.hahaxueche.com/share/steps";
    private static final String WEB_URL_FREE_TRY = "http://m.hahaxueche.com/free_trial?promo_code=553353";

    private HHBaseApplication application;

    public void attachView(HomepageView view) {
        this.mHomepageView = view;
        application = HHBaseApplication.get(mHomepageView.getContext());
    }

    public void detachView() {
        this.mHomepageView = null;
        if (subscription != null) subscription.unsubscribe();
        application = null;
    }

    public void openAboutHaha() {
        mHomepageView.openWebView(WEB_URL_ABOUT_HAHA);
    }

    public void openAboutCoach() {
        mHomepageView.openWebView(WEB_URL_ABOUT_COACH);
    }

    public void openMyStrengths() {
        mHomepageView.openWebView(WEB_URL_MY_STRENGTHS);
    }

    public void openProcedure() {
        mHomepageView.openWebView(WEB_URL_PROCEDURE);
    }

    /**
     * 在线咨询
     */
    public void onlineAsk() {
        String title = "聊天窗口的标题";
        // 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入三个参数分别为来源页面的url，来源页面标题，来源页面额外信息（可自由定义）
        // 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
        ConsultSource source = new ConsultSource("", "android", "");
        //登录用户添加用户信息
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            YSFUserInfo userInfo = new YSFUserInfo();
            userInfo.userId = user.id;
            userInfo.data = "[{\"key\":\"real_name\", \"value\":\"" + user.student.name + "\"},{\"key\":\"mobile_phone\", \"value\":\"" + user.student.cell_phone + "\"}]";
            Unicorn.setUserInfo(userInfo);
        }
        // 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable(), 如果返回为false，该接口不会有任何动作
        Unicorn.openServiceActivity(mHomepageView.getContext(), // 上下文
                title, // 聊天窗口的标题
                source // 咨询的发起来源，包括发起咨询的url，title，描述信息等
        );
    }

    public void freeTry() {
        //免费试学URL
        String url = WEB_URL_FREE_TRY;
        User user = application.getSharedPrefUtil().getUser();
        if (user != null && user.isLogin()) {
            if (user.student.city_id > 0) {
                url += "&city_id=" + user.student.city_id;
            }
            if (!TextUtils.isEmpty(user.student.name)) {
                url += "&name=" + user.student.name;
            }
            if (!TextUtils.isEmpty(user.student.cell_phone)) {
                url += "&phone=" + user.student.cell_phone;
            }

        }
        HHLog.v("free try url -> " + url);
        mHomepageView.openWebView(url);
    }
}
