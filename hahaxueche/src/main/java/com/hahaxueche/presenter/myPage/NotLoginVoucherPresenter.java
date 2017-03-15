package com.hahaxueche.presenter.myPage;

import android.text.TextUtils;

import com.hahaxueche.BuildConfig;
import com.hahaxueche.HHBaseApplication;
import com.hahaxueche.model.user.User;
import com.hahaxueche.presenter.HHBasePresenter;
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

public class NotLoginVoucherPresenter extends HHBasePresenter implements Presenter<NotLoginVoucherView> {
    private NotLoginVoucherView mView;
    private Subscription subscription;
    private HHBaseApplication application;
    private static final String WEB_URL_FREE_TRY = BuildConfig.MOBILE_URL + "/free_trial?promo_code=553353";

    public void attachView(NotLoginVoucherView view) {
        this.mView = view;
        application = HHBaseApplication.get(mView.getContext());
        mView.changeCustomerService();
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
            MobclickAgent.onEvent(mView.getContext(), "homepage_free_trial_tapped", map);
        } else {
            MobclickAgent.onEvent(mView.getContext(), "homepage_free_trial_tapped");
        }
        HHLog.v("free try url -> " + url);
        mView.openWebView(url);
    }

    public void onlineAsk() {
        User user = application.getSharedPrefUtil().getUser();
        super.onlineAsk(user, mView.getContext());
    }
}
